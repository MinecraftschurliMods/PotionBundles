package ihh.potionbundles;

import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = PotionBundles.MODID)
public class ClientHandler {

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(ClientHandler::registerItemModelOverrides);
    }

    private static void registerItemModelOverrides() {
        ItemModelsProperties.register(PotionBundles.POTION_BUNDLE.get(), new ResourceLocation(PotionBundles.MODID, "uses"), (stack, world, living) -> {
            if (!stack.hasTag() || !stack.getOrCreateTag().contains(PotionBundle.USES_KEY)) return 0;
            return stack.getOrCreateTag().getInt(PotionBundle.USES_KEY);
        });
    }
}
