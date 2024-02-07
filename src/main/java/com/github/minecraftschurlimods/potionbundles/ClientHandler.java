package com.github.minecraftschurlimods.potionbundles;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = PotionBundles.MODID)
public class ClientHandler {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            @SuppressWarnings("deprecation")
            ItemPropertyFunction propertyFunction = (stack, world, living, seed) -> !stack.hasTag() || !stack.getOrCreateTag().contains(PotionBundleUtils.USES_KEY) ? 0 : PotionBundleUtils.getUses(stack);
            ResourceLocation uses = new ResourceLocation(PotionBundles.MODID, "uses");
            for (RegistryObject<Item> item : PotionBundles.ITEMS.getEntries()) {
                ItemProperties.register(item.get(), uses, propertyFunction);
            }
        });
    }

    @SubscribeEvent
    public static void registerItemColorHandler(RegisterColorHandlersEvent.Item e) {
        e.register((stack, index) -> index > 0 ? -1 : PotionUtils.getColor(stack), PotionBundles.ITEMS.getEntries().stream().map(RegistryObject::get).toArray(Item[]::new));
    }
}
