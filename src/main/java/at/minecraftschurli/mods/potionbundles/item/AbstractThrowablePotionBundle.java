package at.minecraftschurli.mods.potionbundles.item;

import at.minecraftschurli.mods.potionbundles.util.PotionBundleUtils;
import at.minecraftschurli.mods.potionbundles.ServerConfig;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

public abstract class AbstractThrowablePotionBundle extends AbstractPotionBundle implements ProjectileItem {
    public static float PROJECTILE_SHOOT_POWER = 0.5F;

    protected AbstractThrowablePotionBundle(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        playThrowSound(level, player);
        ItemStack itemstack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverlevel) {
            Item vanillaPotion = getVanillaPotion();
            ItemStack fake = new ItemStack(vanillaPotion);
            fake.copyFrom(itemstack, DataComponents.POTION_CONTENTS, DataComponents.POTION_DURATION_SCALE);
            Projectile.spawnProjectileFromRotation(this::createPotion, serverlevel, fake, player, -20.0F, PROJECTILE_SHOOT_POWER, 1.0F);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.hasInfiniteMaterials()) {
            PotionBundleUtils.decrementUses(itemstack);
        }
        if (PotionBundleUtils.getUses(itemstack) <= 0 && ServerConfig.INSTANCE.returnString.get()) {
            return InteractionResult.SUCCESS.heldItemTransformedTo(PotionBundleUtils.getString(itemstack));
        }
        return InteractionResult.SUCCESS;
    }

    protected abstract AbstractThrownPotion createPotion(ServerLevel level, LivingEntity entity, ItemStack stack);

    protected abstract AbstractThrownPotion createPotion(Level level, Position pos, ItemStack stack);

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        return this.createPotion(level, pos, stack);
    }

    @Override
    public ProjectileItem.DispenseConfig createDispenseConfig() {
        return ProjectileItem.DispenseConfig.builder()
            .uncertainty(ProjectileItem.DispenseConfig.DEFAULT.uncertainty() * 0.5F)
            .power(ProjectileItem.DispenseConfig.DEFAULT.power() * 1.25F)
            .build();
    }

    protected abstract void playThrowSound(Level level, Player player);
}
