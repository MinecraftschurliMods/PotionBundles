package ihh.potionbundles;

import javax.annotation.Nonnull;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import org.apache.logging.log4j.LogManager;

public class PotionBundleRecipe extends SpecialRecipe {
    public static final IRecipeSerializer<?> SERIALIZER = new SpecialRecipeSerializer<>(PotionBundleRecipe::new).setRegistryName("crafting_special_potion_bundle");

    public PotionBundleRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        int potions = 0;
        boolean string = false;
        Potion potion = Potions.EMPTY;
        for(int i = 0; i < inv.getSizeInventory(); i++) {
            LogManager.getLogger().debug("Getting slot " + i);
            ItemStack is = inv.getStackInSlot(i);
            if(Tags.Items.STRING.contains(is.getItem())) {
                if(string) return false;
                string = true;
            } else if(is.getItem() == Items.POTION) {
                if(potions == 0) {
                    potion = PotionUtils.getPotionFromItem(is);
                    potions++;
                } else if(potions > 0) {
                    if(PotionUtils.getPotionFromItem(is) != potion) return false;
                    potions++;
                }
                if(potions > 3) return false;
            } else if(!is.isEmpty()) return false;
        }
        return potions == 3 && string;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack is = inv.getStackInSlot(i);
            if(is.getItem() == Items.POTION) {
                ItemStack stack = new ItemStack(PotionBundles.POTION_BUNDLE.get());
                if(!stack.hasTag()) stack.setTag(new CompoundNBT());
                stack.getTag().putInt("Uses", 3);
                return PotionUtils.addPotionToItemStack(stack, PotionUtils.getPotionFromItem(is));
            }
        }
        return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
