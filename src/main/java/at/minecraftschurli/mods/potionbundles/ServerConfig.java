package at.minecraftschurli.mods.potionbundles;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class ServerConfig {
    static final ModConfigSpec SPEC;
    public static final ServerConfig INSTANCE;

    public final ModConfigSpec.BooleanValue returnString;

    private ServerConfig(ModConfigSpec.Builder builder) {
        returnString = builder
            .comment("Whether to return the string upon finishing the bundle or not.")
            .translation("config." + PotionBundles.MODID + ".returnString")
            .define("returnString", true);
    }

    static {
        Pair<ServerConfig, ModConfigSpec> serverPair = new ModConfigSpec.Builder().configure(ServerConfig::new);
        SPEC = serverPair.getRight();
        INSTANCE = serverPair.getLeft();
    }
}
