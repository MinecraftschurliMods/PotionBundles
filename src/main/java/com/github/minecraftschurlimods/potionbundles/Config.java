package com.github.minecraftschurlimods.potionbundles;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    static final ModConfigSpec clientSpec;
    public static final Config.Client CLIENT;
    static final ModConfigSpec serverSpec;
    public static final Config.Server SERVER;

    static {
        Pair<Config.Client, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(Config.Client::new);
        clientSpec = clientPair.getRight();
        CLIENT = clientPair.getLeft();
        Pair<Config.Server, ModConfigSpec> serverPair = new ModConfigSpec.Builder().configure(Config.Server::new);
        serverSpec = serverPair.getRight();
        SERVER = serverPair.getLeft();
    }

    public static class Client {
        public final ModConfigSpec.IntValue durabilityBarColor;
        public final ModConfigSpec.BooleanValue showDurabilityBar;

        Client(ModConfigSpec.Builder builder) {
            durabilityBarColor = builder
                    .comment("What color the damage bar of a potion bundle should have. Set to -1 to not display a damage bar.")
                    .translation("config." + PotionBundles.MODID + ".durabilityBarColor")
                    .defineInRange("durabilityBarColor", 0x0000dd, -1, 0xffffff);
            showDurabilityBar = builder
                    .comment("Whether to show the durability bar of the potion bundles.")
                    .translation("config." + PotionBundles.MODID + ".showDurabilityBar")
                    .define("showDurabilityBar", false);
        }
    }

    public static class Server {
        public final ModConfigSpec.BooleanValue returnString;

        Server(ModConfigSpec.Builder builder) {
            returnString = builder
                    .comment("Whether to return the string upon finishing the bundle or not.")
                    .translation("config." + PotionBundles.MODID + ".returnString")
                    .define("returnString", true);
        }
    }
}
