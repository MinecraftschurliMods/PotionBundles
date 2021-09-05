package com.github.ichhabehunger54.potionbundles;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * @author Minecraftschurli
 * @version 2021-07-24
 */
public final class PotionBundleUtils {
    public static final String USES_KEY = "Uses";
    public static final String STRING_KEY = "String";

    private PotionBundleUtils() {}

    public static int getUses(ItemStack stack) {
        return stack.getOrCreateTag().getInt(USES_KEY);
    }

    public static void setUses(ItemStack stack, int uses) {
        stack.getOrCreateTag().putInt(USES_KEY, uses);
    }

    public static void decrementUses(ItemStack stack) {
        setUses(stack, getUses(stack) - 1);
    }

    public static ItemStack getString(ItemStack stack) {
        ItemStack s = ItemStack.of(stack.getOrCreateTag().getCompound(STRING_KEY));
        return s.isEmpty() ? new ItemStack(Items.STRING) : s;
    }

    public static void setString(ItemStack stack, ItemStack string) {
        stack.getOrCreateTag().put(STRING_KEY, string.serializeNBT());
    }

    public static Item getPotionForBundle(Level world, AbstractPotionBundle bundle) {
        return world.getRecipeManager()
                .getAllRecipesFor(RecipeType.CRAFTING)
                .stream()
                .filter(recipe -> recipe.getSerializer() == PotionBundles.POTION_BUNDLE_RECIPE_SERIALIZER.get())
                .filter(PotionBundleRecipe.class::isInstance)
                .map(PotionBundleRecipe.class::cast)
                .filter(recipe -> recipe.getBundleItem() == bundle)
                .findFirst()
                .map(PotionBundleRecipe::getPotionItem)
                .orElse(null);
    }
}
