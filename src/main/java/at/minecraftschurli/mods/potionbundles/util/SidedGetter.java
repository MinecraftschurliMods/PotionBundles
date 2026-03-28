package at.minecraftschurli.mods.potionbundles.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public final class SidedGetter {
    public static @Nullable RecipeManager getRecipeManager() {
        if (EffectiveSide.get().isServer()) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            return server == null ? null : server.getRecipeManager();
        }
        return null;
    }
}
