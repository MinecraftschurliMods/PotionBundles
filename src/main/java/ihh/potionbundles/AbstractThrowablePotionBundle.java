package ihh.potionbundles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * @author Minecraftschurli
 * @version 2021-07-26
 */
public abstract class AbstractThrowablePotionBundle extends AbstractPotionBundle {
    @Nonnull
    @Override
    public ActionResult<ItemStack> use(final @Nonnull World world,
                                       final @Nonnull PlayerEntity player,
                                       final @Nonnull Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        this.playThrowSound(world, player);
        if (!world.isClientSide) {
            PotionEntity thrownpotion = new PotionEntity(world, player);
            ItemStack fake = new ItemStack(PotionBundleUtils.getPotionForBundle(world, this));
            PotionUtils.setPotion(fake, PotionUtils.getPotion(itemstack));
            PotionUtils.setCustomEffects(fake, PotionUtils.getCustomEffects(itemstack));
            thrownpotion.setItem(fake);
            thrownpotion.shootFromRotation(player,
                    player.xRot,
                    player.yRot,
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

        return ActionResult.sidedSuccess(itemstack, world.isClientSide());
    }

    protected abstract void playThrowSound(final @Nonnull World world, final @Nonnull PlayerEntity player);
}
