package com.github.minecraftschurlimods.potionbundles;

import com.mojang.logging.LogUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.List;

@Mod(PotionBundles.MODID)
public class PotionBundles {
    public static final String MODID = "potionbundles";
    public static final int POTION_BUNDLE_SIZE = 3;
    static final Logger LOGGER = LogUtils.getLogger();
    static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final DeferredItem<PotionBundle> POTION_BUNDLE = ITEMS.register("potion_bundle", PotionBundle::new);
    public static final DeferredItem<SplashPotionBundle> SPLASH_POTION_BUNDLE = ITEMS.register("splash_potion_bundle", SplashPotionBundle::new);
    public static final DeferredItem<LingeringPotionBundle> LINGERING_POTION_BUNDLE = ITEMS.register("lingering_potion_bundle", LingeringPotionBundle::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PotionBundleRecipe>> POTION_BUNDLE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("crafting_special_potion_bundle", PotionBundleRecipe.Serializer::new);

    public PotionBundles(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        modEventBus.addListener(PotionBundles::registerItemsToCreativeTabs);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
    }

    private static void registerItemsToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != CreativeModeTabs.FOOD_AND_DRINKS) return;
        ItemStack stringBasic;
        ItemStack stringSplash;
        ItemStack stringLingering;
        if (event.getParameters().holders() instanceof RegistryAccess registryAccess) {
            RecipeManager recipeManager = SidedGetter.getRecipeManager();
            if (recipeManager != null) {
                stringBasic = getStringFromRecipe(POTION_BUNDLE.get(), registryAccess, recipeManager);
                stringSplash = getStringFromRecipe(SPLASH_POTION_BUNDLE.get(), registryAccess, recipeManager);
                stringLingering = getStringFromRecipe(LINGERING_POTION_BUNDLE.get(), registryAccess, recipeManager);
            } else {
                LOGGER.error("No RecipeManager available, can't get correct string for potion bundles.");
                stringBasic = stringSplash = stringLingering = ItemStack.EMPTY;
            }
        } else {
            LOGGER.error("No RegistryAccess available, can't get correct string for potion bundles.");
            stringBasic = stringSplash = stringLingering = ItemStack.EMPTY;
        }
        addBundlesForAllPotions(event, POTION_BUNDLE.get(), stringBasic);
        addBundlesForAllPotions(event, SPLASH_POTION_BUNDLE.get(), stringSplash);
        addBundlesForAllPotions(event, LINGERING_POTION_BUNDLE.get(), stringLingering);
    }

    private static void addBundlesForAllPotions(BuildCreativeModeTabContentsEvent populator, AbstractPotionBundle bundle, ItemStack string) {
        for (Potion potion : BuiltInRegistries.POTION) {
            if (potion == Potions.EMPTY) continue;
            ItemStack stack = bundle.createStack(string, potion, List.of(), null);
            if (!stack.isEmpty()) populator.accept(stack);
        }
    }

    private static ItemStack getStringFromRecipe(AbstractPotionBundle bundle, RegistryAccess registryAccess, RecipeManager recipeManager) {
        for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
            Recipe<?> recipe = holder.value();
            if (recipe.getSerializer() != POTION_BUNDLE_RECIPE_SERIALIZER.get()) continue;
            if (recipe.getResultItem(registryAccess).getItem() != bundle) continue;
            for (ItemStack stack : ((PotionBundleRecipe) recipe).getString().getItems()) {
                if (!stack.isEmpty()) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
