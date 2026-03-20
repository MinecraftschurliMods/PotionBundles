package at.minecraftschurli.mods.potionbundles.util;

import at.minecraftschurli.mods.potionbundles.PotionBundles;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

public final class PotionBundleUtils {
    private PotionBundleUtils() {
        throw new NotImplementedException("You can't instantiate a utility class");
    }

    public static int getUses(ItemStack stack) {
        return stack.getOrDefault(PotionBundles.USES, 0);
    }

    public static void setUses(ItemStack stack, int uses) {
        stack.set(PotionBundles.USES, uses);
    }

    public static void decrementUses(ItemStack stack) {
        setUses(stack, getUses(stack) - 1);
    }

    public static ItemStack getString(ItemStack stack) {
        PotionBundleString string = stack.get(PotionBundles.STRING);
        return string == null ? new ItemStack(Items.STRING) : string.toItemStack();
    }

    public static void setString(ItemStack stack, @Nullable PotionBundleString string) {
        stack.set(PotionBundles.STRING, string);
    }
}
