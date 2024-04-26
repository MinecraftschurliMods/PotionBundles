package com.github.minecraftschurlimods.potionbundles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record PotionBundleString(Holder<Item> item, DataComponentPatch components) {
    public static final MapCodec<PotionBundleString> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(PotionBundleString::item),
            DataComponentPatch.CODEC.fieldOf("components").forGetter(PotionBundleString::components)
    ).apply(inst, PotionBundleString::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, PotionBundleString> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.ITEM),
            PotionBundleString::item,
            DataComponentPatch.STREAM_CODEC,
            PotionBundleString::components,
            PotionBundleString::new
    );

    public ItemStack toItemStack() {
        return new ItemStack(item(), 1, components());
    }

    public static PotionBundleString fromItemHolder(Holder<Item> item) {
        return new PotionBundleString(item, DataComponentPatch.EMPTY);
    }

    public static PotionBundleString fromItem(Item item) {
        return fromItem(item, DataComponentPatch.EMPTY);
    }

    public static PotionBundleString fromItem(Item item, DataComponentPatch components) {
        return new PotionBundleString(item.builtInRegistryHolder(), components);
    }

    public static PotionBundleString fromItemStack(ItemStack itemStack) {
        return new PotionBundleString(itemStack.getItemHolder(), itemStack.getComponentsPatch());
    }
}
