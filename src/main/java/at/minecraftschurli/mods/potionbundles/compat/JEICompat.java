package at.minecraftschurli.mods.potionbundles.compat;

import at.minecraftschurli.mods.potionbundles.PotionBundleRecipe;
import at.minecraftschurli.mods.potionbundles.PotionBundles;
import at.minecraftschurli.mods.potionbundles.PotionBundlesItems;
import at.minecraftschurli.mods.potionbundles.item.AbstractPotionBundle;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.vanilla.IJeiShapedRecipeBuilder;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.common.Internal;
import mezz.jei.library.plugins.vanilla.ingredients.subtypes.PotionSubtypeInterpreter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.Objects;
import java.util.stream.Stream;

@JeiPlugin
public final class JEICompat implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(PotionBundles.MODID, PotionBundles.MODID);
    }

    @Override
    public void registerRecipes(final IRecipeRegistration registration) {
        Minecraft minecraft = Objects.requireNonNull(Minecraft.getInstance());
        ClientLevel level = Objects.requireNonNull(minecraft.level);
        RecipeMap recipeMap = Internal.getClientSyncedRecipes();
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        var recipes = recipeMap.byType(RecipeType.CRAFTING)
            .stream()
            .map(RecipeHolder::value)
            .filter(PotionBundleRecipe.class::isInstance)
            .map(PotionBundleRecipe.class::cast)
            .flatMap(recipe -> mapRecipes(jeiHelpers, recipe, level.registryAccess()))
            .toList();
        registration.addRecipes(RecipeTypes.CRAFTING, recipes);
    }

    private Stream<RecipeHolder<CraftingRecipe>> mapRecipes(IJeiHelpers jeiHelpers, final PotionBundleRecipe recipe, HolderLookup.Provider registries) {
        String group = "jei.potionbundle";
        AbstractPotionBundle bundleItem = recipe.getBundleItem();
        int maxUses = bundleItem.getMaxUses();
        Item potionItem = recipe.getPotionItem();
        Ingredient string = recipe.getString();
        IVanillaRecipeFactory vanillaRecipeFactory = jeiHelpers.getVanillaRecipeFactory();
        return registries
            .lookup(Registries.POTION)
            .stream()
            .flatMap(HolderLookup::listElements)
            .map(potion -> {
                ItemStackTemplate input = new ItemStackTemplate(potionItem, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(potion)).build());
                Ingredient potionIngredient = Ingredient.of(input.item().value());
                ItemStackTemplate output = new ItemStackTemplate(bundleItem, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(potion)).set(PotionBundles.USES.get(), maxUses).build());
                Identifier potionId = potion.key().identifier();
                Identifier recipeId = Identifier.fromNamespaceAndPath(ModIds.MINECRAFT_ID, group + "." + potionId.getNamespace() + "." + potionId.getPath());
                ResourceKey<Recipe<?>> resourceKey = ResourceKey.create(Registries.RECIPE, recipeId);
                SlotDisplay slotDisplay = new SlotDisplay.ItemStackSlotDisplay(output);
                IJeiShapedRecipeBuilder builder = vanillaRecipeFactory.createShapedRecipeBuilder(CraftingBookCategory.MISC, slotDisplay)
                    .group(group)
                    .define('S', string)
                    .define('P', potionIngredient, new SlotDisplay.ItemStackSlotDisplay(input));
                StringBuilder sb = new StringBuilder();
                sb.append('S');
                for (int i = 0; i < maxUses; i++) {
                    sb.append('P');
                    if (sb.length() == 3) {
                        builder.pattern(sb.toString());
                        sb.setLength(0);
                    }
                }
                if (!sb.isEmpty()) {
                    builder.pattern(sb.toString());
                }
                return new RecipeHolder<>(resourceKey, builder.build());
            });
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration r) {
        r.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, PotionBundlesItems.POTION_BUNDLE.get(), PotionSubtypeInterpreter.INSTANCE);
        r.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, PotionBundlesItems.SPLASH_POTION_BUNDLE.get(), PotionSubtypeInterpreter.INSTANCE);
        r.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, PotionBundlesItems.LINGERING_POTION_BUNDLE.get(), PotionSubtypeInterpreter.INSTANCE);
    }
}
