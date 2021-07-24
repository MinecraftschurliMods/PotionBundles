package ihh.potionbundles;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;

public class PotionBundleRecipe extends CustomRecipe {

    public PotionBundleRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(final CraftingContainer inv, final @Nonnull Level world) {
        int potions = 0;
        boolean string = false;
        Potion potion = Potions.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (Tags.Items.STRING.contains(is.getItem())) {
                if (string) return false;
                string = true;
            } else if (is.getItem() == Items.POTION) {
                if (potions == 0) {
                    potion = PotionUtils.getPotion(is);
                    potions++;
                } else if (potions > 0) {
                    if (PotionUtils.getPotion(is) != potion) return false;
                    potions++;
                }
                if (potions > 3) return false;
            } else if (!is.isEmpty()) return false;
        }
        return potions == 3 && string;
    }

    @Nonnull
    @Override
    public ItemStack assemble(final CraftingContainer inv) {
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (is.getItem() == Items.POTION) {
                ItemStack stack = new ItemStack(PotionBundles.POTION_BUNDLE.get());
                stack.getOrCreateTag().putInt(PotionBundle.USES_KEY, 3);
                return PotionUtils.setPotion(stack, PotionUtils.getPotion(is));
            }
        }
        return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 3;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return PotionBundles.POTION_BUNDLE_RECIPE_SERIALIZER.get();
    }
}
