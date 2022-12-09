package com.github.ichhabehunger54.potionbundles;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PotionBundleRecipe extends CustomRecipe {
    private final Ingredient string;
    private final Item potion;
    private final AbstractPotionBundle bundle;

    public PotionBundleRecipe(ResourceLocation id, @Nonnull Ingredient string, @Nonnull Item potion, @Nonnull AbstractPotionBundle bundle) {
        super(id, CraftingBookCategory.EQUIPMENT);
        this.string = string;
        this.potion = potion;
        this.bundle = bundle;
    }

    @Override
    public boolean matches(CraftingContainer inv, @Nonnull Level world) {
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

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv) {
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

    @Nonnull
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

    @Nonnull
    public Ingredient getString() {
        return string;
    }

    static class Serializer implements RecipeSerializer<PotionBundleRecipe> {
        @Nonnull
        @Override
        public PotionBundleRecipe fromJson(@Nonnull ResourceLocation rl, @Nonnull JsonObject json) {
            Ingredient string = Ingredient.fromJson(json.get("string"));
            Item potion = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(json.get("potion").getAsString()));
            if (potion == null)
                throw new JsonParseException("Tried using an invalid item as potion item for recipe " + rl);
            Item bundle = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(json.get("bundle").getAsString()));
            if (bundle == null)
                throw new JsonParseException("Tried using an invalid item as potion bundle item for recipe " + rl);
            if (bundle instanceof AbstractPotionBundle bundle1)
                return new PotionBundleRecipe(rl, string, potion, bundle1);
            else
                throw new JsonParseException("The defined PotionBundle is not an instance of AbstractPotionBundle in recipe " + rl);
        }

        @Nullable
        @Override
        public PotionBundleRecipe fromNetwork(@Nonnull ResourceLocation rl, @Nonnull FriendlyByteBuf buf) {
            Ingredient string = Ingredient.fromNetwork(buf);
            Item potion = ForgeRegistries.ITEMS.getValue(buf.readResourceLocation());
            Item bundle = ForgeRegistries.ITEMS.getValue(buf.readResourceLocation());
            assert potion != null;
            assert bundle instanceof AbstractPotionBundle;
            return new PotionBundleRecipe(rl, string, potion, (AbstractPotionBundle) bundle);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buf, @Nonnull PotionBundleRecipe recipe) {
            recipe.string.toNetwork(buf);
            buf.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(recipe.potion)));
            buf.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(recipe.bundle)));
        }
    }
}
