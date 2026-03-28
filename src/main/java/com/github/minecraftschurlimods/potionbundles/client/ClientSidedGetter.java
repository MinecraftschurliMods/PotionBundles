package com.github.minecraftschurlimods.potionbundles.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;

public class ClientSidedGetter {
    public static @Nullable RecipeManager getRecipeManager() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPacketListener connection = minecraft.getConnection();
        if (connection != null) return connection.getRecipeManager();
        ClientLevel level = minecraft.level;
        if (level != null) return level.getRecipeManager();
        return null;
    }
}
