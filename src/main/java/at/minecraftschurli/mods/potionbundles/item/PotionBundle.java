package at.minecraftschurli.mods.potionbundles.item;

import at.minecraftschurli.mods.potionbundles.util.PotionBundleUtils;
import at.minecraftschurli.mods.potionbundles.ServerConfig;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

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
        RandomSource randomsource = entity.getRandom();
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        if (consumable == null) return stack;
        consumable.emitParticlesAndSounds(randomsource, entity, stack, 16);
        Player player;
        if (entity instanceof ServerPlayer serverplayer) {
            serverplayer.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, stack);
            player = serverplayer;
        } else {
            player = null;
        }

        stack.getAllOfType(ConsumableListener.class).forEach(p_366420_ -> p_366420_.onConsume(level, entity, stack, consumable));
        if (!level.isClientSide()) {
            consumable.onConsumeEffects().forEach(p_366779_ -> p_366779_.apply(level, stack, entity));
        }

        entity.gameEvent(consumable.animation() == ItemUseAnimation.DRINK ? GameEvent.DRINK : GameEvent.EAT);

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
