package ihh.potionbundles;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    static final ForgeConfigSpec clientSpec;
    static final Config.Client CLIENT;
    static final ForgeConfigSpec serverSpec;
    static final Config.Server SERVER;

    static {
        final Pair<Config.Client, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder()
                .configure(Config.Client::new);
        clientSpec = clientPair.getRight();
        CLIENT = clientPair.getLeft();
        final Pair<Config.Server, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder()
                .configure(Config.Server::new);
        serverSpec = serverPair.getRight();
        SERVER = serverPair.getLeft();
    }

    public static class Client {
        public final ForgeConfigSpec.IntValue durabilityBarColor;

        Client(ForgeConfigSpec.Builder builder) {
            durabilityBarColor = builder
                    .comment("What color the damage bar of a potion bundle should have. " +
                             "Set to -1 to not display a damage bar.")
                    .translation("config." + PotionBundles.MODID + ".durabilityBarColor")
                    .defineInRange("durabilityBarColor", 0x0000dd, -1, 0xffffff);
        }
    }

    public static class Server {
        public final BooleanValue returnString;
        public final BooleanValue allowSplashPotion;
        public final BooleanValue allowLingeringPotion;

        Server(ForgeConfigSpec.Builder builder) {
            returnString = builder
                    .comment("Whether to return the string upon finishing the bundle or not.")
                    .translation("config." + PotionBundles.MODID + ".returnString")
                    .define("returnString", true);
            allowSplashPotion = builder
                    .comment("Whether to allow the creation of Potion Bundles from Splash Potions.")
                    .translation("config." + PotionBundles.MODID + ".allowSplashPotion")
                    .define("allowSplashPotion", false);
            allowLingeringPotion = builder
                    .comment("Whether to allow the creation of Potion Bundles from Lingering Potions.")
                    .translation("config." + PotionBundles.MODID + ".allowLingeringPotion")
                    .define("allowLingeringPotion", false);
        }
    }
}
