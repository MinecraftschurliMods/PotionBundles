package com.github.minecraftschurlimods.potionbundles;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.*;

import java.util.Objects;
import java.util.stream.Stream;

@JeiPlugin
public class JEICompat implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(PotionBundles.MODID, PotionBundles.MODID);
    }

    @Override
    public void registerRecipes(final IRecipeRegistration registration) {
        Minecraft minecraft = Objects.requireNonNull(Minecraft.getInstance());
        ClientLevel level = Objects.requireNonNull(minecraft.level);
        RecipeManager recipeManager = level.getRecipeManager();
        var recipes = recipeManager.getAllRecipesFor(RecipeType.CRAFTING)
                .stream()
                .map(RecipeHolder::value)
                .filter(PotionBundleRecipe.class::isInstance)
                .map(PotionBundleRecipe.class::cast)
                .flatMap(this::mapRecipes)
                .toList();
        registration.addRecipes(RecipeTypes.CRAFTING, recipes);
    }

    private Stream<RecipeHolder<CraftingRecipe>> mapRecipes(final PotionBundleRecipe recipe) {
        String group = "jei.potionbundle";
        AbstractPotionBundle bundleItem = recipe.getBundleItem();
        int maxUses = bundleItem.getMaxUses();
        Item potionItem = recipe.getPotionItem();
        return BuiltInRegistries.POTION
                .stream()
                .map(potion -> {
                    Ingredient potionIngredient = Ingredient.of(PotionUtils.setPotion(new ItemStack(potionItem), potion));
                    NonNullList<Ingredient> inputs = NonNullList.withSize(maxUses + 1, Ingredient.EMPTY);
                    for (int i = 0; i < maxUses; i++) {
                        inputs.set(i, potionIngredient);
                    }
                    inputs.set(maxUses, recipe.getString());
                    ItemStack output = PotionUtils.setPotion(new ItemStack(bundleItem), potion);
                    PotionBundleUtils.setUses(output, maxUses);
                    ResourceLocation id = new ResourceLocation(ModIds.MINECRAFT_ID, group + "." + output.getDescriptionId() + "." + potion.getName(""));
                    return new RecipeHolder<>(id, new ShapelessRecipe(group, CraftingBookCategory.EQUIPMENT, output, inputs));
                });
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration r) {
        IIngredientSubtypeInterpreter<ItemStack> byPotion = (ingredient, context) -> String.valueOf(BuiltInRegistries.POTION.getKey(PotionUtils.getPotion(ingredient)));
        r.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, PotionBundles.POTION_BUNDLE.get(), byPotion);
        r.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, PotionBundles.SPLASH_POTION_BUNDLE.get(), byPotion);
        r.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, PotionBundles.LINGERING_POTION_BUNDLE.get(), byPotion);
    }
}
