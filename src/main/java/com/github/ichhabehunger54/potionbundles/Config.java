package com.github.ichhabehunger54.potionbundles;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    static final ForgeConfigSpec clientSpec;
    public static final Config.Client CLIENT;
    static final ForgeConfigSpec serverSpec;
    public static final Config.Server SERVER;

    static {
        Pair<Config.Client, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(Config.Client::new);
        clientSpec = clientPair.getRight();
        CLIENT = clientPair.getLeft();
        Pair<Config.Server, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(Config.Server::new);
        serverSpec = serverPair.getRight();
        SERVER = serverPair.getLeft();
    }

    public static class Client {
        public final ForgeConfigSpec.IntValue durabilityBarColor;
        public final ForgeConfigSpec.BooleanValue showDurabilityBar;

        Client(ForgeConfigSpec.Builder builder) {
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
        public final ForgeConfigSpec.BooleanValue returnString;

        Server(ForgeConfigSpec.Builder builder) {
            returnString = builder
                    .comment("Whether to return the string upon finishing the bundle or not.")
                    .translation("config." + PotionBundles.MODID + ".returnString")
                    .define("returnString", true);
        }
    }
}
