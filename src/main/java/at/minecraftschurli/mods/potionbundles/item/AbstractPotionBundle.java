package at.minecraftschurli.mods.potionbundles.item;

import at.minecraftschurli.mods.potionbundles.util.PotionBundleString;
import at.minecraftschurli.mods.potionbundles.util.PotionBundleUtils;
import at.minecraftschurli.mods.potionbundles.PotionBundles;
import at.minecraftschurli.mods.potionbundles.client.ClientConfig;
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
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractPotionBundle extends PotionItem {
    protected AbstractPotionBundle(Item.Properties properties) {
        super(properties);
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
        if (ClientConfig.INSTANCE.durabilityBarColor.get() == -1) return 1;
        return Math.round((float) PotionBundleUtils.getUses(stack) / getMaxUses() * 13f);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable(getDescriptionId() + ".uses", PotionBundleUtils.getUses(stack)));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return ClientConfig.INSTANCE.durabilityBarColor.get();
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return ClientConfig.INSTANCE.showDurabilityBar.get();
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(getDescriptionId(), this.getVanillaPotion().getName(stack));
    }

    public abstract Item getVanillaPotion();

    @VisibleForTesting
    public ItemStack createStack(@Nullable PotionBundleString string, Holder<Potion> potion, List<MobEffectInstance> customEffects, @Nullable Integer customColor, @Nullable String customName) {
        return createStack(string, new PotionContents(Optional.of(potion), Optional.ofNullable(customColor), customEffects, Optional.ofNullable(customName)));
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
