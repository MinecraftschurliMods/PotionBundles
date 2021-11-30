package com.github.ichhabehunger54.potionbundles;

//import dev.ftb.mods.ftblibrary.config.ColorConfig;
//import dev.ftb.mods.ftblibrary.config.ConfigGroup;
//import dev.ftb.mods.ftblibrary.icon.Color4I; TODO: add when ftb library updates to mojmaps
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

//    public static ConfigGroup createConfigGui() {
//        ConfigGroup group = new ConfigGroup("config." + PotionBundles.MODID);
//        CLIENT.createConfigGui(group);
//        SERVER.createConfigGui(group);
//        return group;
//    } TODO: add when ftb library updates to mojmaps

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

//        void createConfigGui(ConfigGroup group) {
//            ConfigGroup subGroup = group.getGroup("config." + PotionBundles.MODID + ".serverGroup");
//            subGroup.add("config." + PotionBundles.MODID + ".durabilityBarColor", new ColorConfig(), Color4I.rgb(durabilityBarColor.get()), (Color4I color) -> durabilityBarColor.set(color.rgb()), Color4I.rgb(0x0000dd));
//            subGroup.savedCallback = b -> {
//                if (b) {
//                    clientSpec.save();
//                }
//            };
//        } TODO: add when ftb library updates to mojmaps
    }

    public static class Server {
        public final ForgeConfigSpec.BooleanValue returnString;
        public final ForgeConfigSpec.BooleanValue allowSplashPotion;
        public final ForgeConfigSpec.BooleanValue allowLingeringPotion;

        Server(ForgeConfigSpec.Builder builder) {
            returnString = builder
                    .comment("Whether to return the string upon finishing the bundle or not.")
                    .translation("config." + PotionBundles.MODID + ".returnString")
                    .define("returnString", true);
            allowSplashPotion = builder
                    .comment("Allow the creation of Potion Bundles from Splash Potions.")
                    .translation("config." + PotionBundles.MODID + ".allowSplashPotion")
                    .define("allowSplashPotion", false);
            allowLingeringPotion = builder
                    .comment("Allow the creation of Potion Bundles from Lingering Potions.")
                    .translation("config." + PotionBundles.MODID + ".allowLingeringPotion")
                    .define("allowLingeringPotion", false);
        }

//        void createConfigGui(ConfigGroup group) {
//            ConfigGroup subGroup = group.getGroup("config." + PotionBundles.MODID + ".serverGroup");
//            subGroup.addBool("config." + PotionBundles.MODID + ".returnString", returnString.get(), returnString::set, true);
//            subGroup.addBool("config." + PotionBundles.MODID + ".allowSplashPotion", allowSplashPotion.get(), allowSplashPotion::set, false);
//            subGroup.addBool("config." + PotionBundles.MODID + ".allowLingeringPotion", allowLingeringPotion.get(), allowLingeringPotion::set, false);
//            subGroup.savedCallback = b -> {
//                if (b) {
//                    serverSpec.save();
//                }
//            };
//        } TODO: add when ftb library updates to mojmaps
    }
}
