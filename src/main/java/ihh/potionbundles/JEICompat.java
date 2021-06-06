package ihh.potionbundles;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

@JeiPlugin
public class JEICompat implements IModPlugin {
    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(PotionBundles.MODID, PotionBundles.MODID);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration r) {
        r.registerSubtypeInterpreter(PotionBundles.POTION_BUNDLE.get(), stack -> String.valueOf(PotionUtils.getPotionFromItem(stack).getRegistryName()));
    }
}
