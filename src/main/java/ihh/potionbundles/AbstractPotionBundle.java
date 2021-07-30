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
    public double getDurabilityForDisplay(ItemStack stack) {
        if (Config.CLIENT.durabilityBarColor.get() == -1) return 1;
        return (float)PotionBundleUtils.getUses(stack) / PotionBundles.POTION_BUNDLE_SIZE;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        tooltip.add(new TranslatableComponent(getDescriptionId() + ".uses", PotionBundleUtils.getUses(stack)));
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return Config.CLIENT.durabilityBarColor.get();
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        return new TranslatableComponent(getDescriptionId(), new TranslatableComponent(PotionUtils.getPotion(stack).getName(Util.makeDescriptionId("item", Items.POTION.getRegistryName()) + ".effect.")));
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
            for (Potion potion : ForgeRegistries.POTION_TYPES) {
                if (potion == Potions.EMPTY) continue;
                ItemStack stack = createStack(new ItemStack(Items.STRING), potion);
                if (!stack.isEmpty()) items.add(stack);
            }
        }
    }

    @Nonnull
    protected ItemStack createStack(@Nonnull ItemStack string, @Nonnull Potion potion) {
        ItemStack stack = PotionUtils.setPotion(new ItemStack(this), potion);
        PotionBundleUtils.setUses(stack, PotionBundles.POTION_BUNDLE_SIZE);
        PotionBundleUtils.setString(stack, string);
        return stack;
    }
}
