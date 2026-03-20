package at.minecraftschurli.mods.potionbundles.client;

import at.minecraftschurli.mods.potionbundles.util.PotionBundleUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record PotionBundleUsesModelProperty() implements RangeSelectItemModelProperty {
    public static final PotionBundleUsesModelProperty INSTANCE = new PotionBundleUsesModelProperty();
    public static final MapCodec<PotionBundleUsesModelProperty> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        return PotionBundleUtils.getUses(stack);
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return CODEC;
    }
}
