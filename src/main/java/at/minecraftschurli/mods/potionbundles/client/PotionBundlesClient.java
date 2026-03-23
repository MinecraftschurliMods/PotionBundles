package at.minecraftschurli.mods.potionbundles.client;

import at.minecraftschurli.mods.potionbundles.PotionBundles;
import at.minecraftschurli.mods.potionbundles.PotionBundlesItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@Mod(value = PotionBundles.MODID, dist = Dist.CLIENT)
public class PotionBundlesClient {
    public PotionBundlesClient(ModContainer container, IEventBus modEventBus) {
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerItemColorHandler);
    }

    public void clientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            @SuppressWarnings("deprecation")
            ItemPropertyFunction propertyFunction = (stack, world, living, seed) -> stack.getOrDefault(PotionBundles.USES, 0);
            ResourceLocation uses = ResourceLocation.fromNamespaceAndPath(PotionBundles.MODID, "uses");
            for (DeferredHolder<Item, ?> item : PotionBundlesItems.ITEMS.getEntries()) {
                ItemProperties.register(item.get(), uses, propertyFunction);
            }
        });
    }

    public void registerItemColorHandler(RegisterColorHandlersEvent.Item e) {
        e.register((stack, index) -> index > 0 ? -1 : stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor(), PotionBundlesItems.ITEMS.getEntries().stream().map(DeferredHolder::get).toArray(Item[]::new));
    }
}
