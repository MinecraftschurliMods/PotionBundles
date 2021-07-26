package ihh.potionbundles;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class PotionBundleRecipe extends CustomRecipe {
    private final Ingredient string;
    private final Item potion;
    private final AbstractPotionBundle bundle;

    public PotionBundleRecipe(final ResourceLocation id,
                              final Ingredient string,
                              final Item potion,
                              final AbstractPotionBundle bundle) {
        super(id);
        this.string = string;
        this.potion = potion;
        this.bundle = bundle;
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
            } else if (is.getItem() == this.potion) {
                if (this.bundle != null && this.bundle.isEnabled()) {
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
    public ItemStack assemble(final CraftingContainer inv) {
        Potion potion = null;
        ItemStack string = null;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (potion == null && is.getItem() == this.potion) {
                potion = PotionUtils.getPotion(is);
            }
            if (string == null && this.string.test(is)) {
                string = is;
            }
            if (potion != null && string != null) {
                return this.bundle.createStack(string, potion);
            }
        }
        return ItemStack.EMPTY;
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

    public AbstractPotionBundle getBundleItem() {
        return this.bundle;
    }

    public Item getPotionItem() {
        return this.potion;
    }

    static class Serializer
            extends ForgeRegistryEntry<RecipeSerializer<?>>
            implements RecipeSerializer<PotionBundleRecipe> {

        @Nonnull
        @Override
        public PotionBundleRecipe fromJson(final @Nonnull ResourceLocation rl, final @Nonnull JsonObject json) {
            Ingredient string = Ingredient.fromJson(json.get("string"));
            Item potion = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(json.get("potion").getAsString()));
            Item bundle = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(json.get("bundle").getAsString()));
            if (bundle instanceof AbstractPotionBundle bundle1) {
                return new PotionBundleRecipe(rl, string, potion, bundle1);
            } else {
                throw new JsonParseException("The defined PotionBundle is not an instance of AbstractPotionBundle");
            }
        }

        @Nullable
        @Override
        public PotionBundleRecipe fromNetwork(final @Nonnull ResourceLocation rl, final @Nonnull FriendlyByteBuf buf) {
            Ingredient string = Ingredient.fromNetwork(buf);
            Item potion = ForgeRegistries.ITEMS.getValue(buf.readResourceLocation());
            Item bundle = ForgeRegistries.ITEMS.getValue(buf.readResourceLocation());
            return new PotionBundleRecipe(rl, string, potion, (AbstractPotionBundle) bundle);
        }

        @Override
        public void toNetwork(final @Nonnull FriendlyByteBuf buf, final @Nonnull PotionBundleRecipe recipe) {
            recipe.string.toNetwork(buf);
            buf.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(recipe.potion)));
            buf.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(recipe.bundle)));
        }
    }
}
