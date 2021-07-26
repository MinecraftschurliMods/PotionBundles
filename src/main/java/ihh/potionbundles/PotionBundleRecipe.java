package ihh.potionbundles;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PotionBundleRecipe extends SpecialRecipe {
    private final Ingredient string;

    public PotionBundleRecipe(ResourceLocation id, final Ingredient string) {
        super(id);
        this.string = string;
    }

    @Override
    public boolean matches(CraftingInventory inv, @Nonnull World world) {
        int potions = 0;
        boolean string = false;
        Potion potion = Potions.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (this.string.test(is)) {
                if (string) return false;
                string = true;
            } else if (is.getItem() instanceof PotionItem) {
                final AbstractPotionBundle bundle = PotionBundleUtils.getBundleForPotion((PotionItem) is.getItem());
                if (bundle != null && bundle.isEnabled()) {
                    if (potions == 0) {
                        potion = PotionUtils.getPotion(is);
                        potions++;
                    } else if (potions > 0) {
                        if (PotionUtils.getPotion(is) != potion) {
                            return false;
                        }
                        potions++;
                    }
                    if (potions > PotionBundles.POTION_BUNDLE_SIZE) {
                        return false;
                    }
                } else if (!is.isEmpty()) {
                    return false;
                }
            } else if (!is.isEmpty()) {
                return false;
            }
        }
        return potions == PotionBundles.POTION_BUNDLE_SIZE && string;
    }

    @Nonnull
    @Override
    public ItemStack assemble(final CraftingInventory inv) {
        Potion potion = null;
        AbstractPotionBundle bundleItem = null;
        ItemStack string = null;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (potion == null && is.getItem() instanceof PotionItem) {
                potion = PotionUtils.getPotion(is);
                bundleItem = PotionBundleUtils.getBundleForPotion((PotionItem) is.getItem());
                if (bundleItem == null || !bundleItem.isEnabled()) {
                    return ItemStack.EMPTY;
                }
            }
            if (string == null && this.string.test(is)) {
                string = is;
            }
            if (potion != null && string != null) {
                return bundleItem.createStack(string, potion);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 3;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return PotionBundles.POTION_BUNDLE_RECIPE_SERIALIZER.get();
    }

    static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<PotionBundleRecipe> {

        @Nonnull
        @Override
        public PotionBundleRecipe fromJson(final @Nonnull ResourceLocation rl, final @Nonnull JsonObject json) {
            return new PotionBundleRecipe(rl, Ingredient.fromJson(json.get("string")));
        }

        @Nullable
        @Override
        public PotionBundleRecipe fromNetwork(final @Nonnull ResourceLocation rl, final @Nonnull PacketBuffer buf) {
            return new PotionBundleRecipe(rl, Ingredient.fromNetwork(buf));
        }

        @Override
        public void toNetwork(final @Nonnull PacketBuffer buf, final @Nonnull PotionBundleRecipe recipe) {
            recipe.string.toNetwork(buf);
        }
    }
}
