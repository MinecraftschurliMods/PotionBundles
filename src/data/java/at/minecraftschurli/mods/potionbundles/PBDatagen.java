package at.minecraftschurli.mods.potionbundles;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = PotionBundles.MODID)
public final class PBDatagen {
    @SubscribeEvent
    public static void generateClientData(final GatherDataEvent.Client event) {
        event.createProvider(PBModelProvider::new);
        event.createProvider(PBRecipeProvider.Runner::new);
    }
}
