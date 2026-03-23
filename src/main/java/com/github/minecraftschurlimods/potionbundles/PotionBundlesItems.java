package com.github.minecraftschurlimods.potionbundles;

import com.github.minecraftschurlimods.potionbundles.item.LingeringPotionBundle;
import com.github.minecraftschurlimods.potionbundles.item.PotionBundle;
import com.github.minecraftschurlimods.potionbundles.item.SplashPotionBundle;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface PotionBundlesItems {
    DeferredRegister.Items ITEMS = DeferredRegister.createItems(PotionBundles.MODID);
    DeferredItem<PotionBundle> POTION_BUNDLE = ITEMS.register("potion_bundle", PotionBundle::new);
    DeferredItem<SplashPotionBundle> SPLASH_POTION_BUNDLE = ITEMS.register("splash_potion_bundle", SplashPotionBundle::new);
    DeferredItem<LingeringPotionBundle> LINGERING_POTION_BUNDLE = ITEMS.register("lingering_potion_bundle", LingeringPotionBundle::new);
}
