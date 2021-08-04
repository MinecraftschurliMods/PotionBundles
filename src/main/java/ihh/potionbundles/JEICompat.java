package ihh.potionbundles;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.PotionUtils;

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
        r.registerSubtypeInterpreter(PotionBundles.POTION_BUNDLE.get(), (ingredient, context) -> String.valueOf(PotionUtils.getPotion(ingredient).getRegistryName()));
    }
}
