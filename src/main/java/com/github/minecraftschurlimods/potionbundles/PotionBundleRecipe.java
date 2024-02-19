package com.github.minecraftschurlimods.potionbundles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Function;

public class PotionBundleRecipe extends CustomRecipe {
    private static final Codec<PotionBundleRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("string").forGetter(PotionBundleRecipe::getString),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("potion").forGetter(PotionBundleRecipe::getPotionItem),
            BuiltInRegistries.ITEM.byNameCodec().comapFlatMap(bundle -> bundle instanceof AbstractPotionBundle bundle1 ? DataResult.success(bundle1) : DataResult.error(() -> "The defined PotionBundle is not an instance of AbstractPotionBundle"), Function.identity()).fieldOf("bundle").forGetter(PotionBundleRecipe::getBundleItem)
    ).apply(inst, PotionBundleRecipe::new));

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
        Potion potion = Potions.EMPTY;
        List<MobEffectInstance> customEffects = null;
        int color = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (this.string.test(is)) {
                if (string) return false;
                string = true;
                continue;
            }
            if (is.getItem() == this.potion) {
                if (potions == 0) {
                    color = PotionUtils.getColor(is);
                    potion = PotionUtils.getPotion(is);
                    customEffects = PotionUtils.getCustomEffects(is);
                    potions++;
                } else if (potions > 0) {
                    if (PotionUtils.getColor(is) != color) return false;
                    if (PotionUtils.getPotion(is) != potion) return false;
                    if (!PotionUtils.getCustomEffects(is).equals(customEffects)) return false;
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
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        Potion potion = null;
        List<MobEffectInstance> customEffects = null;
        ItemStack string = null;
        Integer customColor = null;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (potion == null && is.is(this.potion)) {
                potion = PotionUtils.getPotion(is);
                customEffects = PotionUtils.getCustomEffects(is);
                if (is.getOrCreateTag().contains("CustomPotionColor", 99)) customColor = PotionUtils.getColor(is);
            }
            if (string == null && this.string.test(is)) string = is.copy().split(1);
            if (potion != null && string != null) return this.bundle.createStack(string, potion, customEffects, customColor);
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
        public Codec<PotionBundleRecipe> codec() {
            return CODEC;
        }

        @Override
        public PotionBundleRecipe fromNetwork(FriendlyByteBuf buf) {
            Ingredient string = Ingredient.fromNetwork(buf);
            Item potion = BuiltInRegistries.ITEM.get(buf.readResourceLocation());
            Item bundle = BuiltInRegistries.ITEM.get(buf.readResourceLocation());
            assert bundle instanceof AbstractPotionBundle;
            return new PotionBundleRecipe(string, potion, (AbstractPotionBundle) bundle);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, PotionBundleRecipe recipe) {
            recipe.string.toNetwork(buf);
            buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(recipe.potion));
            buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(recipe.bundle));
        }
    }
}
