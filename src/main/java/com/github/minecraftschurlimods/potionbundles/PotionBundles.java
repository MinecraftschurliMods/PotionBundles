package com.github.minecraftschurlimods.potionbundles;

import com.mojang.logging.LogUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.List;

@Mod(PotionBundles.MODID)
public class PotionBundles {
    public static final String MODID = "potionbundles";
    public static final int POTION_BUNDLE_SIZE = 3;
    static final Logger LOGGER = LogUtils.getLogger();
    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static final RegistryObject<PotionBundle> POTION_BUNDLE = ITEMS.register("potion_bundle", PotionBundle::new);
    public static final RegistryObject<SplashPotionBundle> SPLASH_POTION_BUNDLE = ITEMS.register("splash_potion_bundle", SplashPotionBundle::new);
    public static final RegistryObject<LingeringPotionBundle> LINGERING_POTION_BUNDLE = ITEMS.register("lingering_potion_bundle", LingeringPotionBundle::new);
    public static final RegistryObject<RecipeSerializer<PotionBundleRecipe>> POTION_BUNDLE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("crafting_special_potion_bundle", PotionBundleRecipe.Serializer::new);

    public PotionBundles() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
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
        for (Potion potion : ForgeRegistries.POTIONS.getValues()) {
            if (potion == Potions.EMPTY) continue;
            ItemStack stack = bundle.createStack(string, potion, List.of(), null);
            if (!stack.isEmpty()) populator.accept(stack);
        }
    }

    private static ItemStack getStringFromRecipe(AbstractPotionBundle bundle, RegistryAccess registryAccess, RecipeManager recipeManager) {
        for (Recipe<?> recipe : recipeManager.getRecipes()) {
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
