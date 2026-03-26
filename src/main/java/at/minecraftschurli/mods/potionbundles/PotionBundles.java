package at.minecraftschurli.mods.potionbundles;

import at.minecraftschurli.mods.potionbundles.item.AbstractPotionBundle;
import at.minecraftschurli.mods.potionbundles.util.PotionBundleString;
import at.minecraftschurli.mods.potionbundles.util.PotionBundleUtils;
import at.minecraftschurli.mods.potionbundles.util.SidedGetter;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(PotionBundles.MODID)
public final class PotionBundles {
    public static final String MODID = "potionbundles";
    public static final int POTION_BUNDLE_SIZE = 3;
    static final Logger LOGGER = LogUtils.getLogger();
    static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final Supplier<DataComponentType<Integer>> USES = DATA_COMPONENTS.registerComponentType("uses", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final Supplier<DataComponentType<PotionBundleString>> STRING = DATA_COMPONENTS.registerComponentType("string", builder -> builder.persistent(PotionBundleString.CODEC.codec()).networkSynchronized(PotionBundleString.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PotionBundleRecipe>> POTION_BUNDLE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("crafting_special_potion_bundle", () -> new RecipeSerializer<>(PotionBundleRecipe.CODEC, PotionBundleRecipe.STREAM_CODEC));

    public PotionBundles(ModContainer container, IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
        PotionBundlesItems.ITEMS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        modEventBus.addListener(PotionBundles::registerItemsToCreativeTabs);
        container.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    }

    private static void registerItemsToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != CreativeModeTabs.FOOD_AND_DRINKS) return;
        PotionBundleString stringBasic;
        PotionBundleString stringSplash;
        PotionBundleString stringLingering;
        HolderLookup.Provider holderLookupProvider = event.getParameters().holders();
        RecipeManager recipeManager = SidedGetter.getRecipeManager();
        if (recipeManager != null) {
            stringBasic = getStringFromRecipe(PotionBundlesItems.POTION_BUNDLE.get(), holderLookupProvider, recipeManager);
            stringSplash = getStringFromRecipe(PotionBundlesItems.SPLASH_POTION_BUNDLE.get(), holderLookupProvider, recipeManager);
            stringLingering = getStringFromRecipe(PotionBundlesItems.LINGERING_POTION_BUNDLE.get(), holderLookupProvider, recipeManager);
        } else {
            LOGGER.info("No RecipeManager available, can't get correct string for potion bundles.");
            stringBasic = stringSplash = stringLingering = null;
        }
        addBundlesForAllPotions(event, PotionBundlesItems.POTION_BUNDLE.get(), stringBasic);
        addBundlesForAllPotions(event, PotionBundlesItems.SPLASH_POTION_BUNDLE.get(), stringSplash);
        addBundlesForAllPotions(event, PotionBundlesItems.LINGERING_POTION_BUNDLE.get(), stringLingering);
    }

    private static void addBundlesForAllPotions(BuildCreativeModeTabContentsEvent populator, AbstractPotionBundle bundle, @Nullable PotionBundleString string) {
        BuiltInRegistries.POTION.listElements().forEach(potion -> {
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
            if (!(recipe instanceof PotionBundleRecipe potionBundleRecipe) || potionBundleRecipe.getBundleItem() != bundle) continue;
            ContextMap context = new ContextMap.Builder()
                .withParameter(SlotDisplayContext.REGISTRIES, holderLookupProvider)
                .create(SlotDisplayContext.CONTEXT);
            for (ItemStack stack : potionBundleRecipe.getString().display().resolveForStacks(context)) {
                if (!stack.isEmpty()) {
                    return PotionBundleString.fromItemStack(stack);
                }
            }
        }
        return null;
    }
}
