package at.minecraftschurli.mods.potionbundles;

import at.minecraftschurli.mods.potionbundles.item.AbstractPotionBundle;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

final class PBRecipeProvider extends RecipeProvider {
    PBRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
    }

    @Override
    protected void buildRecipes() {
        for (DeferredHolder<Item, ? extends Item> entry : PotionBundlesItems.ITEMS.getEntries()) {
            if (!(entry.get() instanceof AbstractPotionBundle bundle)) continue;
            SpecialRecipeBuilder.special(() -> new PotionBundleRecipe(
                Ingredient.of(items.getOrThrow(Tags.Items.STRINGS)),
                bundle.getVanillaPotion(),
                bundle
            )).save(output, ResourceKey.create(Registries.RECIPE, entry.getId()));
        }
    }
}
