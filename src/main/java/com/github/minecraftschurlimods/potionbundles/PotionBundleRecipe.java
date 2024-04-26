package com.github.minecraftschurlimods.potionbundles;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.function.Function;

public class PotionBundleRecipe extends CustomRecipe {
    private static final MapCodec<PotionBundleRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("string").forGetter(PotionBundleRecipe::getString),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("potion").forGetter(PotionBundleRecipe::getPotionItem),
            BuiltInRegistries.ITEM.byNameCodec().comapFlatMap(bundle -> bundle instanceof AbstractPotionBundle bundle1 ? DataResult.success(bundle1) : DataResult.error(() -> "The defined PotionBundle is not an instance of AbstractPotionBundle"), Function.identity()).fieldOf("bundle").forGetter(PotionBundleRecipe::getBundleItem)
    ).apply(inst, PotionBundleRecipe::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, PotionBundleRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            PotionBundleRecipe::getString,
            ByteBufCodecs.registry(Registries.ITEM),
            PotionBundleRecipe::getPotionItem,
            ByteBufCodecs.registry(Registries.ITEM).map(AbstractPotionBundle.class::cast, Function.identity()),
            PotionBundleRecipe::getBundleItem,
            PotionBundleRecipe::new
    );

    private final Ingredient string;
    private final Item potion;
    private final AbstractPotionBundle bundle;

    public PotionBundleRecipe(Ingredient string, Item potion, AbstractPotionBundle bundle) {
        super(CraftingBookCategory.EQUIPMENT);
        this.string = string;
        this.potion = potion;
        this.bundle = bundle;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level world) {
        int potions = 0;
        boolean string = false;
        PotionContents potionContents = null;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (this.string.test(is)) {
                if (string) return false;
                string = true;
                continue;
            }
            if (is.getItem() == this.potion) {
                if (potions == 0) {
                    potionContents = is.get(DataComponents.POTION_CONTENTS);
                    potions++;
                } else if (potions > 0) {
                    if (!Objects.equals(potionContents, is.get(DataComponents.POTION_CONTENTS))) {
                        return false;
                    }
                    potions++;
                }
                if (potions > this.bundle.getMaxUses()) return false;
                continue;
            }
            if (!is.isEmpty()) return false;
        }
        return potions == this.bundle.getMaxUses() && string;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, HolderLookup.Provider provider) {
        PotionBundleString string = null;
        PotionContents potionContents = null;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (potionContents == null && is.is(this.potion)) {
                potionContents = is.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            }
            if (string == null && this.string.test(is)) {
                string = PotionBundleString.fromItemStack(is);
            }
            if (potionContents != null && string != null) return this.bundle.createStack(string, potionContents);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > this.bundle.getMaxUses();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PotionBundles.POTION_BUNDLE_RECIPE_SERIALIZER.get();
    }

    public AbstractPotionBundle getBundleItem() {
        return this.bundle;
    }

    public Item getPotionItem() {
        return this.potion;
    }

    public Ingredient getString() {
        return string;
    }

    static class Serializer implements RecipeSerializer<PotionBundleRecipe> {
        @Override
        public MapCodec<PotionBundleRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PotionBundleRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
