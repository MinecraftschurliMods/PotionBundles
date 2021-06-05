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
        final Pair<Config.Client, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(Config.Client::new);
        clientSpec = clientPair.getRight();
        CLIENT = clientPair.getLeft();
        final Pair<Config.Server, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(Config.Server::new);
        serverSpec = serverPair.getRight();
        SERVER = serverPair.getLeft();
    }

    public static class Client {
        public final ForgeConfigSpec.IntValue durabilityBarColor;

        Client(ForgeConfigSpec.Builder builder) {
            durabilityBarColor = builder.comment("What color the damage bar of a potion bundle should have. -1 means don't display a damage bar.").translation(PotionBundles.MODID + ".configgui.durabilityBarColor").defineInRange("durabilityBarColor", 0x0000dd, -1, 0xffffff);
        }
    }

    public static class Server {
        public final BooleanValue returnString;

        Server(ForgeConfigSpec.Builder builder) {
            returnString = builder.comment("Whether to return the string on finishing the bundle or not.").translation(PotionBundles.MODID + ".configgui.returnString").define("returnString", true);
        }
    }
}
