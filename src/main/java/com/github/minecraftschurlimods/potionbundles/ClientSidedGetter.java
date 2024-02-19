package com.github.minecraftschurlimods.potionbundles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class ClientSidedGetter {
    @Nullable
    public static RecipeManager getRecipeManager() {
        if (EffectiveSide.get().isServer()) {
            return ServerLifecycleHooks.getCurrentServer().getRecipeManager();
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientPacketListener connection = minecraft.getConnection();
        if (connection != null) return connection.getRecipeManager();
        ClientLevel level = minecraft.level;
        if (level != null) return level.getRecipeManager();
        return null;
    }

    @Nullable
    public static RegistryAccess getRegistryAccess() {
        if (EffectiveSide.get().isServer()) {
            return ServerLifecycleHooks.getCurrentServer().registryAccess();
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientPacketListener connection = minecraft.getConnection();
        if (connection != null) return connection.registryAccess();
        ClientLevel level = minecraft.level;
        if (level != null) return level.registryAccess();
        return null;
    }
}
