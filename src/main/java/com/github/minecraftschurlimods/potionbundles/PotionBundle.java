package com.github.minecraftschurlimods.potionbundles;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;

public class PotionBundle extends AbstractPotionBundle {
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).addPotionTooltip(tooltip::add, 1.0F, context.tickRate());
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        Player player = entity instanceof Player ? (Player) entity : null;
        if (player instanceof ServerPlayer) CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
        if (!world.isClientSide) {
            PotionContents potioncontents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            potioncontents.forEachEffect(effectInstance -> {
                if (effectInstance.getEffect().value().isInstantenous()) {
                    effectInstance.getEffect().value().applyInstantenousEffect(player, player, entity, effectInstance.getAmplifier(), 1.0);
                } else {
                    entity.addEffect(effectInstance);
                }
            });
        }

        if (player == null || !player.hasInfiniteMaterials()) {
            PotionBundleUtils.decrementUses(stack);

            if (player != null) {
                player.awardStat(Stats.ITEM_USED.get(this));
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        entity.gameEvent(GameEvent.DRINK);

        if (PotionBundleUtils.getUses(stack) != 0) {
            return stack;
        }

        if (Config.SERVER.returnString.get()) {
            return PotionBundleUtils.getString(stack);
        }

        return ItemStack.EMPTY;
    }
}
