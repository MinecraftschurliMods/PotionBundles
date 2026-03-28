package at.minecraftschurli.mods.potionbundles.client;

import at.minecraftschurli.mods.potionbundles.PotionBundles;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class ClientConfig {
    static final ModConfigSpec SPEC;
    public static final ClientConfig INSTANCE;

    public final ModConfigSpec.IntValue durabilityBarColor;
    public final ModConfigSpec.BooleanValue showDurabilityBar;

    private ClientConfig(ModConfigSpec.Builder builder) {
        durabilityBarColor = builder
            .comment("What color the damage bar of a potion bundle should have. Set to -1 to not display a damage bar.")
            .translation("config." + PotionBundles.MODID + ".durabilityBarColor")
            .defineInRange("durabilityBarColor", 0x0000dd, -1, 0xffffff);
        showDurabilityBar = builder
            .comment("Whether to show the durability bar of the potion bundles.")
            .translation("config." + PotionBundles.MODID + ".showDurabilityBar")
            .define("showDurabilityBar", false);
    }

    static {
        Pair<ClientConfig, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        SPEC = clientPair.getRight();
        INSTANCE = clientPair.getLeft();
    }
}
