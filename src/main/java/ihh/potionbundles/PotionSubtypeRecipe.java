package ihh.potionbundles;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PotionSubtypeRecipe extends CustomRecipe {
    private final ItemStack potion;
    private final ItemStack result;

    public PotionSubtypeRecipe(ResourceLocation id, ItemStack potion, ItemStack result) {
        super(id);
        this.potion = potion;
        this.result = result;
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
            } else if (is.getItem() == this.potion.getItem()) {
                if (potions == 0) potion = PotionUtils.getPotion(is);
                else if (potions > 0 && PotionUtils.getPotion(is) != potion) return false;
                potions++;
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
            if (is.getItem() == this.potion.getItem()) {
                ItemStack stack = result.copy();
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
        return PotionBundles.POTION_SUBTYPE_RECIPE.get();
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<PotionSubtypeRecipe> {
        @Nonnull
        @Override
        public PotionSubtypeRecipe fromJson(@Nonnull ResourceLocation id, @Nonnull JsonObject json) {
            ItemStack potion = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "potion"), true);
            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            return new PotionSubtypeRecipe(id, potion, result);
        }

        @Override
        public PotionSubtypeRecipe fromNetwork(@Nonnull ResourceLocation id, FriendlyByteBuf buf) {
            ItemStack potion = buf.readItem();
            ItemStack result = buf.readItem();
            return new PotionSubtypeRecipe(id, potion, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, PotionSubtypeRecipe recipe) {
            buf.writeItem(recipe.potion);
            buf.writeItem(recipe.result);
        }
    }
}
