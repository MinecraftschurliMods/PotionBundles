package ihh.potionbundles;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("potionbundles")
public class PotionBundles {
    public PotionBundles() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerItemColorHandler);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IRecipeSerializer.class, this::registerRecipeSerializers);
    }

    private void registerItemColorHandler(ColorHandlerEvent.Item e) {
        e.getItemColors().register((stack, index) -> index > 0 ? -1 : PotionUtils.getColor(stack), POTION_BUNDLE.get());
    }

    private void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> e) {
        e.getRegistry().register(PotionBundleRecipe.SERIALIZER);
    }

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, "potionbundles");
    public static final RegistryObject<Item> POTION_BUNDLE = ITEMS.register("potion_bundle", () -> new PotionBundle(new Item.Properties().group(ItemGroup.BREWING)));
}
