package at.minecraftschurli.mods.potionbundles;

import at.minecraftschurli.mods.potionbundles.item.LingeringPotionBundle;
import at.minecraftschurli.mods.potionbundles.item.PotionBundle;
import at.minecraftschurli.mods.potionbundles.item.SplashPotionBundle;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumables;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface PotionBundlesItems {
    DeferredRegister.Items ITEMS = DeferredRegister.createItems(PotionBundles.MODID);
    DeferredItem<PotionBundle> POTION_BUNDLE = ITEMS.registerItem("potion_bundle", PotionBundle::new, p -> p.stacksTo(1).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK));
    DeferredItem<SplashPotionBundle> SPLASH_POTION_BUNDLE = ITEMS.registerItem("splash_potion_bundle", SplashPotionBundle::new, p -> p.stacksTo(1).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY));
    DeferredItem<LingeringPotionBundle> LINGERING_POTION_BUNDLE = ITEMS.registerItem("lingering_potion_bundle", LingeringPotionBundle::new, p -> p.stacksTo(1).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).component(DataComponents.POTION_DURATION_SCALE, 0.25F));
}
