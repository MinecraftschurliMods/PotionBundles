package ihh.potionbundles;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class PotionBundle extends AbstractPotionBundle {

    @Override
    public void appendHoverText(@Nonnull final ItemStack stack, @Nullable final Level world, @Nonnull final List<Component> tooltip, @Nonnull final TooltipFlag flag) {
        PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Nonnull
    @Override
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull LivingEntity entity) {
        Player player = entity instanceof Player ? (Player) entity : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
        }
        if (!world.isClientSide) {
            for (MobEffectInstance effect : PotionUtils.getMobEffects(stack)) {
                if (effect.getEffect().isInstantenous())
                    effect.getEffect().applyInstantenousEffect(player, player, entity, effect.getAmplifier(), 1);
                else entity.addEffect(new MobEffectInstance(effect));
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
