package ihh.potionbundles;

import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
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

    public PotionBundle() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_BREWING).stacksTo(1));
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        return new TranslatableComponent(PotionBundleUtils.getTranslationKey(stack), new TranslatableComponent(PotionUtils.getPotion(stack).getName(Util.makeDescriptionId("item", Items.POTION.getRegistryName()) + ".effect.")).getString());
    }

    @Override
    public void appendHoverText(final @Nonnull ItemStack stack, @Nullable final Level world, @Nonnull final List<Component> tooltip, @Nonnull final TooltipFlag flag) {
        if (PotionBundleUtils.isLingering(stack)) {
            PotionUtils.addPotionTooltip(stack, tooltip, 0.25F);
        } else {
            super.appendHoverText(stack, world, tooltip, flag);
        }
        tooltip.add(new TranslatableComponent(PotionBundleUtils.getTranslationKey(stack) + ".uses", PotionBundleUtils.getUses(stack)));
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (Config.CLIENT.durabilityBarColor.get() == -1) return 1;
        return (float)PotionBundleUtils.getUses(stack) / PotionBundles.POTION_BUNDLE_SIZE;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return Config.CLIENT.durabilityBarColor.get();
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(final @Nonnull Level world, final @Nonnull Player player, final @Nonnull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!PotionBundleUtils.isThrowable(itemstack)) {
            return ItemUtils.startUsingInstantly(world, player, hand);
        }
        if (PotionBundleUtils.isLingering(itemstack)) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LINGERING_POTION_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        } else {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        }
        if (!world.isClientSide) {
            ThrownPotion thrownpotion = new ThrownPotion(world, player);
            ItemStack fake = new ItemStack(PotionBundleUtils.isLingering(itemstack) ? Items.LINGERING_POTION : Items.SPLASH_POTION);
            PotionUtils.setPotion(fake, PotionUtils.getPotion(itemstack));
            PotionUtils.setCustomEffects(fake, PotionUtils.getCustomEffects(itemstack));
            thrownpotion.setItem(fake);
            thrownpotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
            world.addFreshEntity(thrownpotion);
        }

        PotionBundleUtils.decrementUses(itemstack);
        player.awardStat(Stats.ITEM_USED.get(this));

        if (PotionBundleUtils.getUses(itemstack) == 0) {
            itemstack = Config.SERVER.returnString.get() ? PotionBundleUtils.getString(itemstack) : ItemStack.EMPTY;
        }

        return InteractionResultHolder.sidedSuccess(itemstack, world.isClientSide());
    }

    @Nonnull
    @Override
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull LivingEntity entity) {
        Player player = entity instanceof Player ? (Player) entity : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
        }
        if (!world.isClientSide) {
            for (MobEffectInstance effect : PotionUtils.getMobEffects(stack)) {
                if (effect.getEffect().isInstantenous())
                    effect.getEffect().applyInstantenousEffect(player, player, entity, effect.getAmplifier(), 1);
                else entity.addEffect(new MobEffectInstance(effect));
            }
        }
        PotionBundleUtils.decrementUses(stack);
        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.GLASS_BOTTLE));
        }
        return PotionBundleUtils.getUses(stack) == 0
                ? Config.SERVER.returnString.get()
                ? PotionBundleUtils.getString(stack)
                : ItemStack.EMPTY
                : stack;
    }

    @Override
    public void fillItemCategory(final @Nonnull CreativeModeTab group, final @Nonnull NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            for (Potion potion : ForgeRegistries.POTION_TYPES) {
                if (potion == Potions.EMPTY) {
                    continue;
                }
                items.add(PotionBundleUtils.createStack(new ItemStack(Items.STRING), potion));
                if (Config.SERVER.allowSplashPotion.get()) {
                    items.add(PotionBundleUtils.createStack(new ItemStack(Items.STRING), potion, false));
                }
                if (Config.SERVER.allowLingeringPotion.get()) {
                    items.add(PotionBundleUtils.createStack(new ItemStack(Items.STRING), potion, true));
                }
            }
        }
    }
}
