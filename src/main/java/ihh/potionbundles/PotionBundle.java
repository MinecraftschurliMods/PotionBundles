package ihh.potionbundles;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.Stats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class PotionBundle extends AbstractPotionBundle {

    @Override
    public void appendHoverText(final @Nonnull ItemStack stack,
                                final @Nullable World world,
                                final @Nonnull List<ITextComponent> tooltip,
                                final @Nonnull ITooltipFlag flag) {
        PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Nonnull
    @Override
    public ItemStack finishUsingItem(final @Nonnull ItemStack stack,
                                     final @Nonnull World world,
                                     final @Nonnull LivingEntity entity) {
        PlayerEntity player = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;
        if (player instanceof ServerPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity) player, stack);
        }
        if (!world.isClientSide) {
            for (EffectInstance effect : PotionUtils.getMobEffects(stack)) {
                if (effect.getEffect().isInstantenous())
                    effect.getEffect().applyInstantenousEffect(player, player, entity, effect.getAmplifier(), 1);
                else entity.addEffect(new EffectInstance(effect));
            }
        }
        PotionBundleUtils.decrementUses(stack);
        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.GLASS_BOTTLE));
        }
        return PotionBundleUtils.getUses(stack) == 0
                ? Config.SERVER.returnString.get()
                ? PotionBundleUtils.getString(stack)
                : ItemStack.EMPTY
                : stack;
    }

    @Override
    protected boolean isEnabled() {
        return true;
    }
}
