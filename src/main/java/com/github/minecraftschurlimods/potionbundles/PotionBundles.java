package com.github.minecraftschurlimods.potionbundles;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mod(PotionBundles.MODID)
public class PotionBundles {
    public static final String MODID = "potionbundles";
    public static final int POTION_BUNDLE_SIZE = 3;
    static final Logger LOGGER = LogUtils.getLogger();
    static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(MODID);
    static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final Supplier<DataComponentType<Integer>> USES = DATA_COMPONENTS.registerComponentType("uses", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final Supplier<DataComponentType<PotionBundleString>> STRING = DATA_COMPONENTS.registerComponentType("string", builder -> builder.persistent(PotionBundleString.CODEC.codec()).networkSynchronized(PotionBundleString.STREAM_CODEC));
    public static final DeferredItem<PotionBundle> POTION_BUNDLE = ITEMS.register("potion_bundle", PotionBundle::new);
    public static final DeferredItem<SplashPotionBundle> SPLASH_POTION_BUNDLE = ITEMS.register("splash_potion_bundle", SplashPotionBundle::new);
    public static final DeferredItem<LingeringPotionBundle> LINGERING_POTION_BUNDLE = ITEMS.register("lingering_potion_bundle", LingeringPotionBundle::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PotionBundleRecipe>> POTION_BUNDLE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("crafting_special_potion_bundle", PotionBundleRecipe.Serializer::new);

    public PotionBundles(ModContainer container, IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
        ITEMS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        modEventBus.addListener(PotionBundles::registerItemsToCreativeTabs);
        container.registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
        container.registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
        NeoForge.EVENT_BUS.addListener((AddReloadListenerEvent evt) -> evt.addListener((SimpleReloadListener) PotionBundleUtils::onReload));
    }

    private static void registerItemsToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != CreativeModeTabs.FOOD_AND_DRINKS) return;
        PotionBundleString stringBasic;
        PotionBundleString stringSplash;
        PotionBundleString stringLingering;
        HolderLookup.Provider holderLookupProvider = event.getParameters().holders();
        RecipeManager recipeManager = SidedGetter.getRecipeManager();
        if (recipeManager != null) {
            stringBasic = getStringFromRecipe(POTION_BUNDLE.get(), holderLookupProvider, recipeManager);
            stringSplash = getStringFromRecipe(SPLASH_POTION_BUNDLE.get(), holderLookupProvider, recipeManager);
            stringLingering = getStringFromRecipe(LINGERING_POTION_BUNDLE.get(), holderLookupProvider, recipeManager);
        } else {
            LOGGER.error("No RecipeManager available, can't get correct string for potion bundles.");
            stringBasic = stringSplash = stringLingering = null;
        }
        addBundlesForAllPotions(event, POTION_BUNDLE.get(), stringBasic);
        addBundlesForAllPotions(event, SPLASH_POTION_BUNDLE.get(), stringSplash);
        addBundlesForAllPotions(event, LINGERING_POTION_BUNDLE.get(), stringLingering);
    }

    private static void addBundlesForAllPotions(BuildCreativeModeTabContentsEvent populator, AbstractPotionBundle bundle, @Nullable PotionBundleString string) {
        BuiltInRegistries.POTION.holders().forEach(potion -> {
            ItemStack stack = bundle.createStack(string, new PotionContents(potion));
            if (!stack.isEmpty()) {
                populator.accept(stack);
            }
        });
    }

    @Nullable
    private static PotionBundleString getStringFromRecipe(AbstractPotionBundle bundle, HolderLookup.Provider holderLookupProvider, RecipeManager recipeManager) {
        for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
            Recipe<?> recipe = holder.value();
            if (recipe.getSerializer() != POTION_BUNDLE_RECIPE_SERIALIZER.get()) continue;
            if (recipe.getResultItem(holderLookupProvider).getItem() != bundle) continue;
            for (ItemStack stack : ((PotionBundleRecipe) recipe).getString().getItems()) {
                if (!stack.isEmpty()) {
                    return PotionBundleString.fromItemStack(stack);
                }
            }
        }
        return null;
    }

    @FunctionalInterface
    private interface SimpleReloadListener extends PreparableReloadListener {
        @Override
        default CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
            return CompletableFuture.<Void>completedFuture(null).thenCompose(preparationBarrier::wait).thenAcceptAsync(($) -> onReload(), gameExecutor);
        }

        void onReload();
    }
}
