package com.github.minecraftschurlimods.potionbundles;

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
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PotionBundle extends AbstractPotionBundle {
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        PotionUtils.addPotionTooltip(stack, tooltip, 1.0F, world == null ? 20.0F : world.tickRateManager().tickrate());
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        Player player = entity instanceof Player ? (Player) entity : null;
        if (player instanceof ServerPlayer) CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
        if (!world.isClientSide) for (MobEffectInstance effect : PotionUtils.getMobEffects(stack)) {
            if (effect.getEffect().isInstantenous()) effect.getEffect().applyInstantenousEffect(player, player, entity, effect.getAmplifier(), 1);
            else entity.addEffect(new MobEffectInstance(effect));
        }
        PotionBundleUtils.decrementUses(stack);
        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.GLASS_BOTTLE));
        }
        return PotionBundleUtils.getUses(stack) == 0 ? Config.SERVER.returnString.get() ? PotionBundleUtils.getString(stack) : ItemStack.EMPTY : stack;
    }
}
