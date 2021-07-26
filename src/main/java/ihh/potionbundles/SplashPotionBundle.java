package ihh.potionbundles;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Minecraftschurli
 * @version 2021-07-26
 */
public class SplashPotionBundle extends AbstractThrowablePotionBundle {

    @Override
    public void appendHoverText(@Nonnull final ItemStack stack, @Nullable final World world, @Nonnull final List<ITextComponent> tooltip, @Nonnull final ITooltipFlag flag) {
        PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    protected boolean isEnabled() {
        return Config.SERVER.allowSplashPotion.get();
    }

    @Override
    protected void playThrowSound(final World world, final PlayerEntity player) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
    }
}
