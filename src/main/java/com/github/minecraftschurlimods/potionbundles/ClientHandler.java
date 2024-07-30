package com.github.minecraftschurlimods.potionbundles;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD, modid = PotionBundles.MODID)
public class ClientHandler {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            @SuppressWarnings("deprecation")
            ItemPropertyFunction propertyFunction = (stack, world, living, seed) -> stack.getOrDefault(PotionBundles.USES, 0);
            ResourceLocation uses = ResourceLocation.fromNamespaceAndPath(PotionBundles.MODID, "uses");
            for (DeferredHolder<Item, ?> item : PotionBundles.ITEMS.getEntries()) {
                ItemProperties.register(item.get(), uses, propertyFunction);
            }
        });
    }

    @SubscribeEvent
    public static void registerItemColorHandler(RegisterColorHandlersEvent.Item e) {
        e.register((stack, index) -> index > 0 ? -1 : stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor(), PotionBundles.ITEMS.getEntries().stream().map(DeferredHolder::get).toArray(Item[]::new));
    }
}
