package com.github.ichhabehunger54.potionbundles;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Mod(PotionBundles.MODID)
public class PotionBundles {
    public static final String MODID = "potionbundles";
    public static final int POTION_BUNDLE_SIZE = 3;
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
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

    private static void registerItemsToCreativeTabs(CreativeModeTabEvent.BuildContents event) {
        event.register(CreativeModeTabs.FOOD_AND_DRINKS, (enabledFlags, populator, hasPermissions) -> {
            ItemStack prevStack = ItemStack.EMPTY;
            prevStack = addBundlesForAllPotions(populator, POTION_BUNDLE.get(), prevStack);
            prevStack = addBundlesForAllPotions(populator, SPLASH_POTION_BUNDLE.get(), prevStack);
            addBundlesForAllPotions(populator, LINGERING_POTION_BUNDLE.get(), prevStack);
        });
    }

    private static ItemStack addBundlesForAllPotions(CreativeModeTabEvent.CreativeModeTabPopulator populator, @NotNull AbstractPotionBundle bundle, ItemStack prevStack) {
        for (Potion potion : ForgeRegistries.POTIONS) {
            if (potion == Potions.EMPTY) continue;
            ItemStack stack = bundle.createStack(new ItemStack(Items.STRING), potion, List.of(), null);
            if (!stack.isEmpty()) populator.accept(stack, ItemStack.EMPTY, prevStack);
            prevStack = stack;
        }
        return prevStack;
    }
}
