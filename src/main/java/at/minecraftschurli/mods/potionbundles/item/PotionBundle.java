package at.minecraftschurli.mods.potionbundles.item;

import at.minecraftschurli.mods.potionbundles.util.PotionBundleUtils;
import at.minecraftschurli.mods.potionbundles.ServerConfig;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class PotionBundle extends AbstractPotionBundle {
    
    public PotionBundle(Item.Properties properties) {
        super(properties);
    }

    @Override
    public Item getVanillaPotion() {
        return Items.POTION;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        int count = stack.count();
        ItemStack itemstack = super.finishUsingItem(stack, level, entity);
        if (itemstack.count() == count) {
            return itemstack;
        }
        itemstack.setCount(count);

        Player player = entity instanceof Player p ? p : null;

        if (player == null || !player.hasInfiniteMaterials()) {
            PotionBundleUtils.decrementUses(stack);

            if (player != null) {
                player.getInventory().placeItemBackInInventory(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        if (PotionBundleUtils.getUses(stack) > 0) {
            return stack;
        }

        if (ServerConfig.INSTANCE.returnString.get()) {
            return PotionBundleUtils.getString(stack);
        }

        return ItemStack.EMPTY;
    }
}
