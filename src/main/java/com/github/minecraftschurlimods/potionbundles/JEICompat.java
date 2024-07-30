package com.github.minecraftschurlimods.potionbundles;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@JeiPlugin
public class JEICompat implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(PotionBundles.MODID, PotionBundles.MODID);
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
                .flatMap(recipe -> mapRecipes(recipe, level.registryAccess()))
                .toList();
        registration.addRecipes(RecipeTypes.CRAFTING, recipes);
    }

    private Stream<RecipeHolder<CraftingRecipe>> mapRecipes(final PotionBundleRecipe recipe, HolderLookup.Provider registries) {
        String group = "jei.potionbundle";
        AbstractPotionBundle bundleItem = recipe.getBundleItem();
        int maxUses = bundleItem.getMaxUses();
        Item potionItem = recipe.getPotionItem();
        return registries
                .lookup(Registries.POTION)
                .stream()
                .flatMap(HolderLookup::listElements)
                .map(potion -> {
                    PotionContents potionContents = new PotionContents(potion);
                    Ingredient potionIngredient = DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, potionContents, potionItem);
                    NonNullList<Ingredient> inputs = NonNullList.withSize(maxUses + 1, Ingredient.EMPTY);
                    for (int i = 0; i < maxUses; i++) {
                        inputs.set(i, potionIngredient);
                    }
                    inputs.set(maxUses, recipe.getString());
                    ItemStack output = PotionContents.createItemStack(bundleItem, potion);
                    PotionBundleUtils.setUses(output, maxUses);
                    ResourceLocation id = ResourceLocation.withDefaultNamespace(group + "." + output.getDescriptionId() + "." + Potion.getName(Optional.of(potion), ""));
                    return new RecipeHolder<>(id, new ShapelessRecipe(group, CraftingBookCategory.EQUIPMENT, output, inputs));
                });
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration r) {
        IIngredientSubtypeInterpreter<ItemStack> byPotion = (ingredient, context) -> ingredient.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion().map(Holder::getRegisteredName).orElse("unknown");
        r.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, PotionBundles.POTION_BUNDLE.get(), byPotion);
        r.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, PotionBundles.SPLASH_POTION_BUNDLE.get(), byPotion);
        r.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, PotionBundles.LINGERING_POTION_BUNDLE.get(), byPotion);
    }
}
