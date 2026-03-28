package at.minecraftschurli.mods.potionbundles;

import at.minecraftschurli.mods.potionbundles.client.PotionBundleUsesModelProperty;
import net.minecraft.client.color.item.Potion;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.stream.IntStream;

final class PBModelProvider extends ModelProvider {
    PBModelProvider(PackOutput out) {
        super(out, PotionBundles.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        for (DeferredHolder<Item, ? extends Item> entry : PotionBundlesItems.ITEMS.getEntries()) {
            Item item = entry.get();
            itemModels.itemModelOutput.accept(
                item,
                ItemModelUtils.rangeSelect(
                    PotionBundleUsesModelProperty.INSTANCE,
                    IntStream.range(1, PotionBundles.POTION_BUNDLE_SIZE + 1)
                        .mapToObj(uses -> new RangeSelectItemModel.Entry(
                            uses,
                            ItemModelUtils.tintedModel(
                                itemModels.generateLayeredItem(
                                    ModelLocationUtils.getModelLocation(item, "_" + uses),
                                    new Material(Identifier.fromNamespaceAndPath(PotionBundles.MODID, "item/potion_bundle_overlay_" + uses)),
                                    TextureMapping.getItemTexture(item, "_" + uses)),
                                new Potion())))
                        .toList()));
        }
    }
}
