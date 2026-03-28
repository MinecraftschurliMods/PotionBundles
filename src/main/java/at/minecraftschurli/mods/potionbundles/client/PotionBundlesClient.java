package at.minecraftschurli.mods.potionbundles.client;

import at.minecraftschurli.mods.potionbundles.PotionBundles;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;

@Mod(value = PotionBundles.MODID, dist = Dist.CLIENT)
public final class PotionBundlesClient {
    public PotionBundlesClient(ModContainer container, IEventBus modEventBus) {
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modEventBus.addListener(PotionBundlesClient::registerModelProperty);
    }
    
    private static void registerModelProperty(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(Identifier.fromNamespaceAndPath(PotionBundles.MODID, "uses"), PotionBundleUsesModelProperty.CODEC);
    }
}
