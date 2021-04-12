package ihh.potionbundles;

import net.minecraft.item.ItemModelsProperties;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = PotionBundles.MODID)
public class ClientHandler {
    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemModelsProperties.register(PotionBundles.POTION_BUNDLE.get(), new ResourceLocation(PotionBundles.MODID, "uses"), (stack, world, living) -> !stack.hasTag() || !stack.getOrCreateTag().contains(PotionBundle.USES_KEY) ? 0 : stack.getOrCreateTag().getInt(PotionBundle.USES_KEY)));
    }

    @SubscribeEvent
    public static void registerItemColorHandler(ColorHandlerEvent.Item e) {
        e.getItemColors().register((stack, index) -> index > 0 ? -1 : PotionUtils.getColor(stack), PotionBundles.POTION_BUNDLE.get());
    }
}
