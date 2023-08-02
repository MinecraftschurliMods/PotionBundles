package com.github.ichhabehunger54.potionbundles;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
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
        if (event.getTab() != CreativeModeTabs.FOOD_AND_DRINKS) return;
        addBundlesForAllPotions(event, POTION_BUNDLE.get());
        addBundlesForAllPotions(event, SPLASH_POTION_BUNDLE.get());
        addBundlesForAllPotions(event, LINGERING_POTION_BUNDLE.get());
    }

    private static void addBundlesForAllPotions(CreativeModeTabEvent.BuildContents populator, @NotNull AbstractPotionBundle bundle) {
        ItemStack string = getStringFromRecipe(bundle);
        for (Potion potion : ForgeRegistries.POTIONS.getValues()) {
            if (potion == Potions.EMPTY) continue;
            ItemStack stack = bundle.createStack(string, potion, List.of(), null);
            if (!stack.isEmpty()) populator.accept(stack);
        }
    }

    @Nonnull
    private static ItemStack getStringFromRecipe(@Nonnull AbstractPotionBundle bundle) {
        Pair<RecipeManager, RegistryAccess> pair = DistExecutor.unsafeRunForDist(
                () -> () -> {
                    ClientPacketListener connection = Minecraft.getInstance().getConnection();
                    return Pair.of(connection.getRecipeManager(), connection.registryAccess());
                },
                () -> () -> {
                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    return Pair.of(server.getRecipeManager(), server.registryAccess());
                }
        );
        for (Recipe<?> recipe : pair.getFirst().getRecipes()) {
            if (recipe.getSerializer() != POTION_BUNDLE_RECIPE_SERIALIZER.get()) continue;
            if (recipe.getResultItem(pair.getSecond()).getItem() != bundle) continue;
            PotionBundleRecipe potionBundleRecipe = (PotionBundleRecipe) recipe;
            Ingredient stringIngredient = potionBundleRecipe.getString();
            ItemStack[] stacks = stringIngredient.getItems();
            for (ItemStack stack : stacks) {
                if (!stack.isEmpty()) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
