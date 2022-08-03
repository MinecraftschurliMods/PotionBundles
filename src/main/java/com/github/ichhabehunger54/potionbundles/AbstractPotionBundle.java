package com.github.ichhabehunger54.potionbundles;

import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
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
    public int getBarWidth(@Nonnull ItemStack stack) {
        if (Config.CLIENT.durabilityBarColor.get() == -1) return 1;
        return Math.round((float) PotionBundleUtils.getUses(stack) / getMaxUses() * 13f);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        tooltip.add(Component.translatable(getDescriptionId() + ".uses", PotionBundleUtils.getUses(stack)));
    }

    @Override
    public int getBarColor(@Nonnull ItemStack stack) {
        return Config.CLIENT.durabilityBarColor.get();
    }

    @Override
    public boolean isBarVisible(@Nonnull ItemStack stack) {
        return Config.CLIENT.showDurabilityBar.get();
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        return Component.translatable(getDescriptionId(), Component.translatable(PotionUtils.getPotion(stack).getName(Util.makeDescriptionId("item", ForgeRegistries.ITEMS.getKey(Items.POTION)) + ".effect.")));
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        if (allowedIn(group)) {
            for (Potion potion : ForgeRegistries.POTIONS) {
                if (potion == Potions.EMPTY) continue;
                ItemStack stack = createStack(new ItemStack(Items.STRING), potion);
                if (!stack.isEmpty()) items.add(stack);
            }
        }
    }

    @Nonnull
    protected ItemStack createStack(@Nonnull ItemStack string, @Nonnull Potion potion) {
        ItemStack stack = PotionUtils.setPotion(new ItemStack(this), potion);
        PotionBundleUtils.setUses(stack, getMaxUses());
        PotionBundleUtils.setString(stack, string);
        return stack;
    }
}
