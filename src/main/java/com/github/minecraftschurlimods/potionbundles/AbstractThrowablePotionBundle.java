package com.github.minecraftschurlimods.potionbundles;

import net.minecraft.core.component.DataComponents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class AbstractThrowablePotionBundle extends AbstractPotionBundle {
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        playThrowSound(world, player);
        if (!world.isClientSide) {
            ThrownPotion thrownpotion = new ThrownPotion(world, player);
            ItemStack fake = new ItemStack(PotionBundleUtils.getPotionForBundle(this));
            fake.set(DataComponents.POTION_CONTENTS, itemstack.get(DataComponents.POTION_CONTENTS));
            thrownpotion.setItem(fake);
            thrownpotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
            world.addFreshEntity(thrownpotion);
        }
        if (!player.hasInfiniteMaterials()) {
            PotionBundleUtils.decrementUses(itemstack);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        if (PotionBundleUtils.getUses(itemstack) != 0) {
            return InteractionResultHolder.sidedSuccess(itemstack, world.isClientSide());
        }
        return InteractionResultHolder.sidedSuccess(Config.SERVER.returnString.get() ? PotionBundleUtils.getString(itemstack) : ItemStack.EMPTY, world.isClientSide());
    }

    protected abstract void playThrowSound(Level world, Player player);
}
