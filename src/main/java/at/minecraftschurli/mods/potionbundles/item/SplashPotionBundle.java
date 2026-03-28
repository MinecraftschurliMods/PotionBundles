package at.minecraftschurli.mods.potionbundles.item;

import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SplashPotionBundle extends AbstractThrowablePotionBundle {

    public SplashPotionBundle(Properties properties) {
        super(properties);
    }

    @Override
    public Item getVanillaPotion() {
        return Items.SPLASH_POTION;
    }

    @Override
    protected AbstractThrownPotion createPotion(ServerLevel level, LivingEntity entity, ItemStack stack) {
        return new ThrownSplashPotion(level, entity, stack);
    }

    @Override
    protected AbstractThrownPotion createPotion(Level level, Position pos, ItemStack stack) {
        return new ThrownSplashPotion(level, pos.x(), pos.y(), pos.z(), stack);
    }

    @Override
    protected void playThrowSound(final Level level, final Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
    }
}
