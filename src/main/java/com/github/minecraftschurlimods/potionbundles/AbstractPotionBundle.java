package com.github.minecraftschurlimods.potionbundles;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;
import java.util.Optional;

public abstract class AbstractPotionBundle extends PotionItem {
    public AbstractPotionBundle() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = super.getDefaultInstance();
        itemstack.set(PotionBundles.USES, getMaxUses());
        return itemstack;
    }

    @VisibleForTesting
    public int getMaxUses() {
        return PotionBundles.POTION_BUNDLE_SIZE;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (Config.CLIENT.durabilityBarColor.get() == -1) return 1;
        return Math.round((float) PotionBundleUtils.getUses(stack) / getMaxUses() * 13f);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(getDescriptionId() + ".uses", PotionBundleUtils.getUses(stack)));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Config.CLIENT.durabilityBarColor.get();
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return Config.CLIENT.showDurabilityBar.get();
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(getDescriptionId(), PotionBundleUtils.getPotionForBundle(this).getName(stack));
    }

    @VisibleForTesting
    public ItemStack createStack(@Nullable PotionBundleString string, Holder<Potion> potion, List<MobEffectInstance> customEffects, @Nullable Integer customColor) {
        return createStack(string, new PotionContents(Optional.of(potion), Optional.ofNullable(customColor), customEffects));
    }

    @VisibleForTesting
    public ItemStack createStack(@Nullable PotionBundleString string, PotionContents potionContents) {
        ItemStack stack = new ItemStack(this);
        stack.set(DataComponents.POTION_CONTENTS, potionContents);
        PotionBundleUtils.setUses(stack, getMaxUses());
        PotionBundleUtils.setString(stack, string);
        return stack;
    }
}
