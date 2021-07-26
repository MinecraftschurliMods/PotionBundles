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
public class LingeringPotionBundle extends AbstractThrowablePotionBundle {

    @Override
    public void appendHoverText(final @Nonnull ItemStack stack,
                                final @Nullable World world,
                                final @Nonnull List<ITextComponent> tooltip,
                                final @Nonnull ITooltipFlag flag) {
        PotionUtils.addPotionTooltip(stack, tooltip, 0.25F);
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    protected void playThrowSound(final @Nonnull World world, final @Nonnull PlayerEntity player) {
        world.playSound(null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.LINGERING_POTION_THROW,
                SoundCategory.NEUTRAL,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    @Override
    protected boolean isEnabled() {
        return Config.SERVER.allowLingeringPotion.get();
    }
}
