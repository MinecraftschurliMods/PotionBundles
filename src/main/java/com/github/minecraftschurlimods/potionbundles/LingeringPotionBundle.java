package com.github.minecraftschurlimods.potionbundles;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LingeringPotionBundle extends AbstractThrowablePotionBundle {
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        PotionUtils.addPotionTooltip(stack, tooltip, 0.25F);
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    protected void playThrowSound(Level world, Player player) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LINGERING_POTION_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
    }
}
