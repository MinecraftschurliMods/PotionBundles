package at.minecraftschurli.mods.potionbundles;

import at.minecraftschurli.mods.potionbundles.item.AbstractPotionBundle;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.concurrent.CompletableFuture;

final class PBRecipeProvider extends RecipeProvider {
    private PBRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        for (DeferredHolder<Item, ? extends Item> entry : PotionBundlesItems.ITEMS.getEntries()) {
            if (!(entry.get() instanceof AbstractPotionBundle bundle)) continue;
            SpecialRecipeBuilder.special(cat -> new PotionBundleRecipe(
                Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(Tags.Items.STRINGS)),
                bundle.getVanillaPotion(),
                bundle
            )).save(output, ResourceKey.create(Registries.RECIPE, entry.getId()));
        }
    }

    static class Runner extends RecipeProvider.Runner {
        Runner(PackOutput out, CompletableFuture<HolderLookup.Provider> provider) {
            super(out, provider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new PBRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Potion Bundles Recipes";
        }
    }
}
