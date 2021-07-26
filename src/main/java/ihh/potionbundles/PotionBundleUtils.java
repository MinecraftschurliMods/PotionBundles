package ihh.potionbundles;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Minecraftschurli
 * @version 2021-07-24
 */
public final class PotionBundleUtils {
    public static final String USES_KEY = "Uses";
    public static final String STRING_KEY = "String";
    private static boolean init;
    private static BiMap<ResourceLocation, ResourceLocation> REF_MAP;

    private PotionBundleUtils() {}

    static void init() {
        if (init) return;
        init = true;
        REF_MAP = HashBiMap.create();
        REF_MAP.put(Items.POTION.getRegistryName(), PotionBundles.POTION_BUNDLE.getId());
        REF_MAP.put(Items.SPLASH_POTION.getRegistryName(), PotionBundles.SPLASH_POTION_BUNDLE.getId());
        REF_MAP.put(Items.LINGERING_POTION.getRegistryName(), PotionBundles.LINGERING_POTION_BUNDLE.getId());
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

    public static AbstractPotionBundle getBundleForPotion(final PotionItem potion) {
        Item i = ForgeRegistries.ITEMS.getValue(REF_MAP.get(ForgeRegistries.ITEMS.getKey(potion)));
        return i instanceof AbstractPotionBundle ? (AbstractPotionBundle) i : null;
    }

    public static PotionItem getPotionForBundle(final AbstractPotionBundle bundle) {
        Item i = ForgeRegistries.ITEMS.getValue(REF_MAP.inverse().get(ForgeRegistries.ITEMS.getKey(bundle)));
        return i instanceof PotionItem ? (PotionItem) i : null;
    }
}
