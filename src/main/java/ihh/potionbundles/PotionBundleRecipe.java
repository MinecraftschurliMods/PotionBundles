package ihh.potionbundles;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PotionBundleRecipe extends CustomRecipe {

    private final Ingredient string;

    public PotionBundleRecipe(ResourceLocation id, final Ingredient string) {
        super(id);
        this.string = string;
    }

    @Override
    public boolean matches(final CraftingContainer inv, final @Nonnull Level world) {
        int potions = 0;
        boolean string = false;
        Potion potion = Potions.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (this.string.test(is)) {
                if (string) return false;
                string = true;
            } else if (isPotion(is.getItem())) {
                if (potions == 0) {
                    potion = PotionUtils.getPotion(is);
                    potions++;
                } else if (potions > 0) {
                    if (PotionUtils.getPotion(is) != potion) return false;
                    potions++;
                }
                if (potions > PotionBundles.POTION_BUNDLE_SIZE) return false;
            } else if (!is.isEmpty()) return false;
        }
        return potions == PotionBundles.POTION_BUNDLE_SIZE && string;
    }

    @Nonnull
    @Override
    public ItemStack assemble(final CraftingContainer inv) {
        Potion potion = null;
        Item potionItem = null;
        ItemStack string = null;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (potion == null && isPotion(is.getItem())) {
                potion = PotionUtils.getPotion(is);
                potionItem = is.getItem();
            }
            if (string == null && this.string.test(is)) {
                string = is;
            }
            if (potion != null && string != null) {
                if (potionItem == Items.POTION) {
                    return PotionBundleUtils.createStack(string, potion);
                } else {
                    return PotionBundleUtils.createStack(string, potion, potionItem == Items.LINGERING_POTION);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private boolean isPotion(final Item item) {
        return item == Items.POTION || (item == Items.SPLASH_POTION && Config.SERVER.allowSplashPotion.get()) || (item == Items.LINGERING_POTION && Config.SERVER.allowLingeringPotion.get());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > PotionBundles.POTION_BUNDLE_SIZE;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return PotionBundles.POTION_BUNDLE_RECIPE_SERIALIZER.get();
    }

    static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<PotionBundleRecipe> {

        @Nonnull
        @Override
        public PotionBundleRecipe fromJson(final @Nonnull ResourceLocation rl, final @Nonnull JsonObject json) {
            return new PotionBundleRecipe(rl, Ingredient.fromJson(json.get("string")));
        }

        @Nullable
        @Override
        public PotionBundleRecipe fromNetwork(final @Nonnull ResourceLocation rl, final @Nonnull FriendlyByteBuf buf) {
            return new PotionBundleRecipe(rl, Ingredient.fromNetwork(buf));
        }

        @Override
        public void toNetwork(final @Nonnull FriendlyByteBuf buf, final @Nonnull PotionBundleRecipe recipe) {
            recipe.string.toNetwork(buf);
        }
    }
}
