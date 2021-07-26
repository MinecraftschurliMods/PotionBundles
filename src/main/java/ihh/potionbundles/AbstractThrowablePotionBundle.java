package ihh.potionbundles;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * @author Minecraftschurli
 * @version 2021-07-26
 */
public abstract class AbstractThrowablePotionBundle extends AbstractPotionBundle {
    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(final @Nonnull Level world,
                                                  final @Nonnull Player player,
                                                  final @Nonnull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        this.playThrowSound(world, player);
        if (!world.isClientSide) {
            ThrownPotion thrownpotion = new ThrownPotion(world, player);
            ItemStack fake = new ItemStack(PotionBundleUtils.getPotionForBundle(world, this));
            PotionUtils.setPotion(fake, PotionUtils.getPotion(itemstack));
            PotionUtils.setCustomEffects(fake, PotionUtils.getCustomEffects(itemstack));
            thrownpotion.setItem(fake);
            thrownpotion.shootFromRotation(
                    player,
                    player.getXRot(),
                    player.getYRot(),
                    -20.0F,
                    0.5F,
                    1.0F);
            world.addFreshEntity(thrownpotion);
        }

        PotionBundleUtils.decrementUses(itemstack);
        player.awardStat(Stats.ITEM_USED.get(this));

        if (PotionBundleUtils.getUses(itemstack) == 0) {
            itemstack = Config.SERVER.returnString.get() ? PotionBundleUtils.getString(itemstack) : ItemStack.EMPTY;
        }

        return InteractionResultHolder.sidedSuccess(itemstack, world.isClientSide());
    }

    protected abstract void playThrowSound(final @Nonnull Level world, final @Nonnull Player player);
}
