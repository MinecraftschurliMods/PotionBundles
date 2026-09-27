package at.minecraftschurli.mods.potionbundles;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = PotionBundles.MODID)
public final class PBDatagen {
    private static final RegistrySetBuilder RELOADABLE = new RegistrySetBuilder()
        .add(RecipeProvider.asBootstrap(PBRecipeProvider::new));

    @SubscribeEvent
    public static void generateClientData(final GatherDataEvent.Client event) {
        event.createProvider(PBModelProvider::new);
        event.createReloadableRegistryObjects(RELOADABLE);
    }
}
