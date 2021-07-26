package ihh.potionbundles;

import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
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
        super(new Item.Properties().tab(CreativeModeTab.TAB_BREWING).stacksTo(1));
    }

    @Override
    public void appendHoverText(final @Nonnull ItemStack stack, @Nullable final Level world, @Nonnull final List<Component> tooltip, @Nonnull final TooltipFlag flag) {
        tooltip.add(new TranslatableComponent(this.getDescriptionId() + ".uses", PotionBundleUtils.getUses(stack)));
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (Config.CLIENT.durabilityBarColor.get() == -1) return 1;
        return (float)PotionBundleUtils.getUses(stack) / PotionBundles.POTION_BUNDLE_SIZE;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return Config.CLIENT.durabilityBarColor.get();
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        return new TranslatableComponent(this.getDescriptionId(), new TranslatableComponent(PotionUtils.getPotion(stack).getName(Util.makeDescriptionId("item", Items.POTION.getRegistryName()) + ".effect.")));
    }

    @Override
    public void fillItemCategory(final @Nonnull CreativeModeTab group, final @Nonnull NonNullList<ItemStack> items) {
        if (this.allowdedIn(group) && this.isEnabled()) {
            for (Potion potion : ForgeRegistries.POTION_TYPES) {
                if (potion == Potions.EMPTY) {
                    continue;
                }
                final ItemStack stack = this.createStack(new ItemStack(Items.STRING), potion);
                if (!stack.isEmpty()) {
                    items.add(stack);
                }
            }
        }
    }

    @Nonnull
    protected ItemStack createStack(final ItemStack string, final Potion potion) {
        ItemStack stack = PotionUtils.setPotion(new ItemStack(this), potion);
        PotionBundleUtils.setUses(stack, PotionBundles.POTION_BUNDLE_SIZE);
        PotionBundleUtils.setString(stack, string);
        return stack;
    }

    protected abstract boolean isEnabled();
}
