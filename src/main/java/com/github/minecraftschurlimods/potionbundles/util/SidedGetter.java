package com.github.minecraftschurlimods.potionbundles.util;

import com.github.minecraftschurlimods.potionbundles.client.ClientSidedGetter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public final class SidedGetter {
    public static @Nullable RecipeManager getRecipeManager() {
        if (EffectiveSide.get().isServer()) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            return server == null ? null : server.getRecipeManager();
        }
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return ClientSidedGetter.getRecipeManager();
        }
        return null;
    }
}
