package ihh.potionbundles;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

/**
 * @author Minecraftschurli
 * @version 2021-07-24
 */
public final class PotionBundleUtils {
    public static final String USES_KEY = "Uses";
    public static final String LINGERING_KEY = "Lingering";
    public static final String STRING_KEY = "String";
    public static final String THROWABLE_KEY = "Throwable";

    private PotionBundleUtils() {}

    public static String getTranslationKey(final ItemStack stack) {
        if (isThrowable(stack)) {
            if (isLingering(stack)) {
                return "item.potionbundles.lingering_potion_bundle";
            } else {
                return "item.potionbundles.splash_potion_bundle";
            }
        } else {
            return "item.potionbundles.potion_bundle";
        }
    }

    public static boolean isThrowable(final ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(THROWABLE_KEY);
    }

    public static void setThrowable(final ItemStack stack, boolean throwable) {
        stack.getOrCreateTag().putBoolean(THROWABLE_KEY, throwable);
    }

    public static boolean isLingering(final ItemStack stack) {
        return isThrowable(stack) && stack.getOrCreateTag().getBoolean(LINGERING_KEY);
    }

    public static void setLingering(final ItemStack stack, boolean lingering) {
        if (isThrowable(stack)) return;
        stack.getOrCreateTag().putBoolean(LINGERING_KEY, lingering);
    }

    public static int getUses(final ItemStack stack) {
        return stack.getOrCreateTag().getInt(USES_KEY);
    }

    public static void setUses(final ItemStack stack, int uses) {
        stack.getOrCreateTag().putInt(USES_KEY, uses);
    }

    public static void decrementUses(final ItemStack stack) {
        setUses(stack, getUses(stack) - 1);
    }

    public static ItemStack getString(final ItemStack stack) {
        ItemStack s = ItemStack.of(stack.getOrCreateTag().getCompound(STRING_KEY));
        return s.isEmpty() ? new ItemStack(Items.STRING) : s;
    }

    public static void setString(final ItemStack stack, final ItemStack string) {
        stack.getOrCreateTag().put(STRING_KEY, string.serializeNBT());
    }

    public static ItemStack createStack(ItemStack string, Potion potion, boolean lingering) {
        ItemStack stack = PotionUtils.setPotion(new ItemStack(PotionBundles.POTION_BUNDLE.get()), potion);
        setUses(stack, PotionBundles.POTION_BUNDLE_SIZE);
        setString(stack, string);
        setThrowable(stack, true);
        setLingering(stack, lingering);
        return stack;
    }

    public static ItemStack createStack(ItemStack string, Potion potion) {
        ItemStack stack = PotionUtils.setPotion(new ItemStack(PotionBundles.POTION_BUNDLE.get()), potion);
        setUses(stack, PotionBundles.POTION_BUNDLE_SIZE);
        setString(stack, string);
        setThrowable(stack, false);
        return stack;
    }
}
