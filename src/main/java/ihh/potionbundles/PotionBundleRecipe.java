package ihh.potionbundles;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;

public class PotionBundleRecipe extends SpecialRecipe {
    public static final IRecipeSerializer<?> SERIALIZER = new SpecialRecipeSerializer<>(PotionBundleRecipe::new).setRegistryName("crafting_special_potion_bundle");

    public PotionBundleRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inv, @Nonnull World world) {
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
    public ItemStack assemble(CraftingInventory inv) {
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
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
