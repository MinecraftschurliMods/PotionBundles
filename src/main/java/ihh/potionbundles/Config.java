package ihh.potionbundles;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    public static class Client {
        public final ForgeConfigSpec.IntValue durabilityBarColor;
        Client(ForgeConfigSpec.Builder builder) {
            this.durabilityBarColor = builder.comment("Define what color the damage bar of a potion bundle should have. -1 means don't display a damage bar.")
                    .translation(PotionBundles.MODID+".configgui.durabilityBarColor")
                    .defineInRange("durabilityBarColor", 0x0000dd, -1, 0xffffff);
        }
    }

    public static class Server {
        public final BooleanValue returnString;
        Server(ForgeConfigSpec.Builder builder) {
            this.returnString = builder.comment("Whether to return the string on finishing the bundle or not.")
                    .translation(PotionBundles.MODID+".configgui.returnString")
                    .define("returnString", true);
        }
    }

    static final ForgeConfigSpec clientSpec;
    public static final Config.Client CLIENT;
    static {
        final Pair<Config.Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config.Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    static final ForgeConfigSpec serverSpec;
    public static final Config.Server SERVER;
    static {
        final Pair<Config.Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config.Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }
}
