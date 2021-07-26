package ihh.potionbundles;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Minecraftschurli
 * @version 2021-07-26
 */
public class LingeringPotionBundle extends AbstractThrowablePotionBundle {

    @Override
    public void appendHoverText(@Nonnull final ItemStack stack, @Nullable final Level world, @Nonnull final List<Component> tooltip, @Nonnull final TooltipFlag flag) {
        PotionUtils.addPotionTooltip(stack, tooltip, 0.25F);
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    protected void playThrowSound(final Level world, final Player player) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LINGERING_POTION_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    @Override
    protected boolean isEnabled() {
        return Config.SERVER.allowLingeringPotion.get();
    }
}
