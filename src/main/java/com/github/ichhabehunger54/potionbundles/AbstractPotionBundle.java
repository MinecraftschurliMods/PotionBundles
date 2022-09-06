package com.github.ichhabehunger54.potionbundles;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractPotionBundle extends PotionItem {
    public AbstractPotionBundle() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_BREWING).stacksTo(1));
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
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        tooltip.add(new TranslatableComponent(getDescriptionId() + ".uses", PotionBundleUtils.getUses(stack)));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Config.CLIENT.durabilityBarColor.get();
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return Config.CLIENT.showDurabilityBar.get();
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        return new TranslatableComponent(getDescriptionId(), Items.POTION.getName(stack));
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
            for (Potion potion : ForgeRegistries.POTIONS) {
                if (potion == Potions.EMPTY) continue;
                ItemStack stack = createStack(new ItemStack(Items.STRING), potion, List.of(), null);
                if (!stack.isEmpty()) items.add(stack);
            }
        }
    }

    @Nonnull
    protected ItemStack createStack(@Nonnull ItemStack string, @Nonnull Potion potion, @Nonnull List<MobEffectInstance> customEffects, @Nullable Integer customColor) {
        ItemStack stack = new ItemStack(this);
        PotionUtils.setPotion(stack, potion);
        PotionUtils.setCustomEffects(stack, customEffects);
        if (customColor != null) stack.getOrCreateTag().putInt("CustomPotionColor", customColor);
        PotionBundleUtils.setUses(stack, getMaxUses());
        PotionBundleUtils.setString(stack, string);
        return stack;
    }
}
