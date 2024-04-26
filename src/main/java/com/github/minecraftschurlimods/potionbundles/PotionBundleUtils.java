package com.github.minecraftschurlimods.potionbundles;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class PotionBundleUtils {
    private static final Map<AbstractPotionBundle, Item> POTION_FOR_BUNDLE_CACHE = new HashMap<>();

    private PotionBundleUtils() {
        throw new NotImplementedException("You can't instantiate a utility class");
    }

    public static int getUses(ItemStack stack) {
        return stack.getOrDefault(PotionBundles.USES, 0);
    }

    public static void setUses(ItemStack stack, int uses) {
        stack.set(PotionBundles.USES, uses);
    }

    public static void decrementUses(ItemStack stack) {
        setUses(stack, getUses(stack) - 1);
    }

    public static ItemStack getString(ItemStack stack) {
        PotionBundleString string = stack.get(PotionBundles.STRING);
        return string == null ? new ItemStack(Items.STRING) : string.toItemStack();
    }

    public static void setString(ItemStack stack, @Nullable PotionBundleString string) {
        stack.set(PotionBundles.STRING, string);
    }

    static void onReload() {
        POTION_FOR_BUNDLE_CACHE.clear();
    }

    public static Item getPotionForBundle(AbstractPotionBundle bundle) {
        return POTION_FOR_BUNDLE_CACHE.computeIfAbsent(bundle, b -> Optional.ofNullable(SidedGetter.getRecipeManager()).flatMap(recipeManager -> recipeManager
                .getAllRecipesFor(RecipeType.CRAFTING)
                .stream()
                .map(RecipeHolder::value)
                .filter(recipe -> recipe.getSerializer() == PotionBundles.POTION_BUNDLE_RECIPE_SERIALIZER.get())
                .filter(PotionBundleRecipe.class::isInstance)
                .map(PotionBundleRecipe.class::cast)
                .filter(recipe -> recipe.getBundleItem() == b)
                .findFirst()
                .map(PotionBundleRecipe::getPotionItem))
                .orElse(Items.POTION));
    }
}
