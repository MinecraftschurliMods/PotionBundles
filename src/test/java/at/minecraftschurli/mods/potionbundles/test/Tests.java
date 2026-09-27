package at.minecraftschurli.mods.potionbundles.test;

import at.minecraftschurli.mods.potionbundles.PotionBundles;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.testframework.annotation.RegisterStructureTemplate;
import net.neoforged.testframework.conf.ClientConfiguration;
import net.neoforged.testframework.conf.Feature;
import net.neoforged.testframework.conf.FrameworkConfiguration;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;
import net.neoforged.testframework.impl.MutableTestFramework;
import net.neoforged.testframework.summary.GitHubActionsStepSummaryDumper;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

@EventBusSubscriber(modid = PotionBundles.MODID)
public final class Tests {
    public static final String TEMPLATE_3x3 = PotionBundles.MODID + ":empty_3x3";

    @RegisterStructureTemplate(TEMPLATE_3x3)
    public static final StructureTemplate TEMPLATE3x3 = StructureTemplateBuilder.empty(3, 3, 3);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    private static void init(FMLConstructModEvent event) {
        ModList.get().getModContainerById(PotionBundles.MODID).ifPresent(Tests::init);
    }

    private static void init(ModContainer container) {
        MutableTestFramework framework = FrameworkConfiguration.builder(Identifier.fromNamespaceAndPath(container.getModId(), "tests"))
            .clientConfiguration(() -> ClientConfiguration.builder().toggleOverlayKey(GLFW.GLFW_KEY_J).openManagerKey(GLFW.GLFW_KEY_N).build())
            .enable(Feature.CLIENT_SYNC, Feature.CLIENT_MODIFICATIONS, Feature.TEST_STORE)
            .dumpers(new GitHubActionsStepSummaryDumper("PotionBundles Gametest Summary"))
            .build()
            .create();
        framework.init(Objects.requireNonNull(container.getEventBus()), container);
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> {
            LiteralArgumentBuilder<CommandSourceStack> node = Commands.literal("tests");
            framework.registerCommands(node);
            event.getDispatcher().register(node);
        });
    }
}
