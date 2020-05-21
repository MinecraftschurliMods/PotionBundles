package ihh.potionbundles;

import javax.annotation.Nonnull;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEICompat implements IModPlugin {
    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation("potionbundles", "potionbundles");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration r) {
        r.registerSubtypeInterpreter(PotionBundles.POTION_BUNDLE.get(), stack -> PotionUtils.getPotionFromItem(stack).getRegistryName().toString());
    }
}
