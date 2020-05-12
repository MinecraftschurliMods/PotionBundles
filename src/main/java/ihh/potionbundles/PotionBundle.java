package ihh.potionbundles;

import javax.annotation.Nonnull;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.stats.Stats;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionBundle extends PotionItem {
    public static final String USES_KEY = "Uses";

    public PotionBundle(Properties properties) {
        super(properties);
        this.addPropertyOverride(new ResourceLocation("uses"), (stack, world, entity) -> {
            if (!stack.hasTag() || !stack.getTag().contains(USES_KEY)) return 0;
            return stack.getTag().getInt(USES_KEY);
        });
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new TranslationTextComponent("item.potionbundles.potion_bundle", new TranslationTextComponent(PotionUtils.getPotionFromItem(stack).getNamePrefixed(Util.makeTranslationKey("item", Items.POTION.getRegistryName()) + ".effect.")).getFormattedText());
    }

    @Nonnull
    @Override
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull LivingEntity entity) {
        if (!stack.hasTag() || !stack.getTag().contains(USES_KEY) || PotionUtils.getPotionFromItem(stack) == Potions.EMPTY)
            return stack;
        PlayerEntity player = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;
        if (player instanceof ServerPlayerEntity)
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity) player, stack);
        if (!world.isRemote) {
            for (EffectInstance effect : PotionUtils.getEffectsFromStack(stack)) {
                if (effect.getPotion().isInstant())
                    effect.getPotion().affectEntity(player, player, entity, effect.getAmplifier(), 1);
                else entity.addPotionEffect(new EffectInstance(effect));
            }
        }
        CompoundNBT tag = stack.getTag();
        if (player != null) {
            player.addStat(Stats.ITEM_USED.get(this));
            tag.putInt(USES_KEY, tag.getInt(USES_KEY) - 1);
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.GLASS_BOTTLE));
        }
        if (tag.getInt(USES_KEY) == 0) return new ItemStack(Items.STRING);
        return stack;
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) for (Potion potion : ForgeRegistries.POTION_TYPES) {
            if (potion == Potions.EMPTY) continue;
            ItemStack stack = PotionUtils.addPotionToItemStack(new ItemStack(this), potion);
            stack.getOrCreateTag().putInt(USES_KEY, 3);
            items.add(stack);
        }
    }
}
