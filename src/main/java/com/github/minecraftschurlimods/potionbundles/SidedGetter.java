package com.github.minecraftschurlimods.potionbundles;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class SidedGetter {
    @Nullable
    public static RecipeManager getRecipeManager() {
        return switch (FMLEnvironment.dist) {
            case CLIENT -> ClientSidedGetter.getRecipeManager();
            case DEDICATED_SERVER -> ServerLifecycleHooks.getCurrentServer().getRecipeManager();
        };
    }

    @Nullable
    public static RegistryAccess getRegistryAccess() {
        return switch (FMLEnvironment.dist) {
            case CLIENT -> ClientSidedGetter.getRegistryAccess();
            case DEDICATED_SERVER -> ServerLifecycleHooks.getCurrentServer().registryAccess();
        };
    }
}
