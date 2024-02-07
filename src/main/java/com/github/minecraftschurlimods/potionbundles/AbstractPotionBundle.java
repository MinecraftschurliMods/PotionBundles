package com.github.minecraftschurlimods.potionbundles;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractPotionBundle extends PotionItem {
    public AbstractPotionBundle() {
        super(new Item.Properties().stacksTo(1));
    }

    protected int getMaxUses() {
        return PotionBundles.POTION_BUNDLE_SIZE;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (Config.CLIENT.durabilityBarColor.get() == -1) return 1;
        return Math.round((float) PotionBundleUtils.getUses(stack) / getMaxUses() * 13f);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
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
        return Component.translatable(getDescriptionId(), Items.POTION.getName(stack));
    }

    protected ItemStack createStack(ItemStack string, Potion potion, List<MobEffectInstance> customEffects, @Nullable Integer customColor) {
        ItemStack stack = new ItemStack(this);
        PotionUtils.setPotion(stack, potion);
        PotionUtils.setCustomEffects(stack, customEffects);
        if (customColor != null) stack.getOrCreateTag().putInt("CustomPotionColor", customColor);
        PotionBundleUtils.setUses(stack, getMaxUses());
        PotionBundleUtils.setString(stack, string);
        return stack;
    }
}
