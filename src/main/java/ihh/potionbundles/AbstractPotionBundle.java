package ihh.potionbundles;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Minecraftschurli
 * @version 2021-07-26
 */
public abstract class AbstractPotionBundle extends PotionItem {
    public AbstractPotionBundle() {
        super(new Item.Properties().tab(ItemGroup.TAB_BREWING).stacksTo(1));
    }

    @Override
    public void appendHoverText(final @Nonnull ItemStack stack,
                                final @Nullable World world,
                                final @Nonnull List<ITextComponent> tooltip,
                                final @Nonnull ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent(
                this.getDescriptionId() + ".uses",
                PotionBundleUtils.getUses(stack)));
    }

    @Override
    public double getDurabilityForDisplay(final ItemStack stack) {
        if (Config.CLIENT.durabilityBarColor.get() == -1) return 1;
        return (float)PotionBundleUtils.getUses(stack) / PotionBundles.POTION_BUNDLE_SIZE;
    }

    @Override
    public int getRGBDurabilityForDisplay(final ItemStack stack) {
        return Config.CLIENT.durabilityBarColor.get();
    }

    @Nonnull
    @Override
    public ITextComponent getName(final @Nonnull ItemStack stack) {
        return new TranslationTextComponent(
                this.getDescriptionId(),
                new TranslationTextComponent(
                        PotionUtils.getPotion(stack)
                                .getName(
                                        Util.makeDescriptionId(
                                                "item",
                                                Items.POTION.getRegistryName()
                                        ) + ".effect."
                                )
                )
        );
    }

    @Override
    public void fillItemCategory(final @Nonnull ItemGroup group, final @Nonnull NonNullList<ItemStack> items) {
        if (this.allowdedIn(group) && (this.isEnabled() || group == ItemGroup.TAB_SEARCH)) {
            for (Potion potion : ForgeRegistries.POTION_TYPES) {
                if (potion == Potions.EMPTY) continue;
                final ItemStack stack = this.createStack(new ItemStack(Items.STRING), potion);
                if (!stack.isEmpty()) items.add(stack);
            }
        }
    }

    @Nonnull
    protected ItemStack createStack(final @Nonnull ItemStack string, final @Nonnull Potion potion) {
        ItemStack stack = PotionUtils.setPotion(new ItemStack(this), potion);
        PotionBundleUtils.setUses(stack, PotionBundles.POTION_BUNDLE_SIZE);
        PotionBundleUtils.setString(stack, string);
        return stack;
    }

    protected abstract boolean isEnabled();
}
