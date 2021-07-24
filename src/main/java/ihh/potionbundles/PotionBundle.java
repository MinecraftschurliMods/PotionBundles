package ihh.potionbundles;

import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class PotionBundle extends PotionItem {
    public static final String USES_KEY = "Uses";

    public PotionBundle(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        return new TranslatableComponent("item.potionbundles.potion_bundle", new TranslatableComponent(PotionUtils.getPotion(stack).getName(Util.makeDescriptionId("item", Items.POTION.getRegistryName()) + ".effect.")).getString());
    }

    @Override
    public void appendHoverText(final @Nonnull ItemStack stack, @Nullable final Level world, @Nonnull final List<Component> tooltip, @Nonnull final TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new TranslatableComponent("item.potionbundles.potion_bundle.uses", stack.getOrCreateTag().getInt(USES_KEY)));
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (Config.CLIENT.durabilityBarColor.get() == -1) return 1;
        return stack.getOrCreateTag().getInt(USES_KEY) / 3.0;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return Config.CLIENT.durabilityBarColor.get();
    }

    @Nonnull
    @Override
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull LivingEntity entity) {
        if (!stack.hasTag() || !stack.getOrCreateTag().contains(USES_KEY) || PotionUtils.getPotion(stack) == Potions.EMPTY)
            return stack;
        Player player = entity instanceof Player ? (Player) entity : null;
        if (player instanceof ServerPlayer)
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
        if (!world.isClientSide) for (MobEffectInstance effect : PotionUtils.getCustomEffects(stack)) {
            if (effect.getEffect().isInstantenous())
                effect.getEffect().applyInstantenousEffect(player, player, entity, effect.getAmplifier(), 1);
            else entity.addEffect(new MobEffectInstance(effect));
        }
        CompoundTag tag = stack.getOrCreateTag();
        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            tag.putInt(USES_KEY, tag.getInt(USES_KEY) - 1);
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.GLASS_BOTTLE));
        }
        return tag.getInt(USES_KEY) == 0 ? Config.SERVER.returnString.get() ? new ItemStack(Items.STRING) : ItemStack.EMPTY : stack;
    }

    @Override
    public void fillItemCategory(final @Nonnull CreativeModeTab group, final @Nonnull NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) for (Potion potion : ForgeRegistries.POTION_TYPES) {
            if (potion == Potions.EMPTY) continue;
            ItemStack stack = PotionUtils.setPotion(new ItemStack(this), potion);
            stack.getOrCreateTag().putInt(USES_KEY, 3);
            items.add(stack);
        }
    }
}
