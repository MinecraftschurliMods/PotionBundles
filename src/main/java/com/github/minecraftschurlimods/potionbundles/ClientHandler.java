package com.github.minecraftschurlimods.potionbundles;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.ModList;
//import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = PotionBundles.MODID)
public class ClientHandler {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            ItemProperties.register(PotionBundles.POTION_BUNDLE.get(), new ResourceLocation(PotionBundles.MODID, "uses"), (stack, world, living, seed) -> !stack.hasTag() || !stack.getOrCreateTag().contains(PotionBundleUtils.USES_KEY) ? 0 : PotionBundleUtils.getUses(stack));
            ItemProperties.register(PotionBundles.SPLASH_POTION_BUNDLE.get(), new ResourceLocation(PotionBundles.MODID, "uses"), (stack, world, living, seed) -> !stack.hasTag() || !stack.getOrCreateTag().contains(PotionBundleUtils.USES_KEY) ? 0 : PotionBundleUtils.getUses(stack));
            ItemProperties.register(PotionBundles.LINGERING_POTION_BUNDLE.get(), new ResourceLocation(PotionBundles.MODID, "uses"), (stack, world, living, seed) -> !stack.hasTag() || !stack.getOrCreateTag().contains(PotionBundleUtils.USES_KEY) ? 0 : PotionBundleUtils.getUses(stack));
        });
    }

    @SubscribeEvent
    public static void registerItemColorHandler(RegisterColorHandlersEvent.Item e) {
        e.register((stack, index) -> index > 0 ? -1 : PotionUtils.getColor(stack), PotionBundles.POTION_BUNDLE.get());
        e.register((stack, index) -> index > 0 ? -1 : PotionUtils.getColor(stack), PotionBundles.SPLASH_POTION_BUNDLE.get());
        e.register((stack, index) -> index > 0 ? -1 : PotionUtils.getColor(stack), PotionBundles.LINGERING_POTION_BUNDLE.get());
    }
}
