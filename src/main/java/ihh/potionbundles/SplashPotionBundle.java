package ihh.potionbundles;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Minecraftschurli
 * @version 2021-07-26
 */
public class SplashPotionBundle extends AbstractThrowablePotionBundle {
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        PotionUtils.addPotionTooltip(stack, tooltip, 1F);
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    protected void playThrowSound(final @Nonnull Level world, final @Nonnull Player player) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    @Override
    protected boolean isEnabled() {
        return Config.SERVER.allowSplashPotion.get();
    }
}
