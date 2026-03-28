package at.minecraftschurli.mods.potionbundles.test;

import at.minecraftschurli.mods.potionbundles.PotionBundles;
import at.minecraftschurli.mods.potionbundles.PotionBundlesItems;
import at.minecraftschurli.mods.potionbundles.item.AbstractThrowablePotionBundle;
import at.minecraftschurli.mods.potionbundles.item.PotionBundle;
import at.minecraftschurli.mods.potionbundles.util.PotionBundleString;
import at.minecraftschurli.mods.potionbundles.util.PotionBundleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.testframework.gametest.GameTest;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PotionBundleTest {
    @GameTest(template = "empty_3x3")
    public static void testPotionBundle(GameTestHelper helper) {
        PotionBundleString string = PotionBundleString.fromItem(Items.STRING);
        PotionBundle potionBundle = PotionBundlesItems.POTION_BUNDLE.get();
        int maxUses = potionBundle.getMaxUses();
        MobEffectInstance customEffect = new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20);
        ItemStack bundle = potionBundle.createStack(string, Potions.WATER, List.of(customEffect), null, null);
        assertUses(helper, bundle, maxUses, "Uses don't use items max uses");
        Player player = setupPlayer(helper, bundle);
        int duration = potionBundle.getUseDuration(bundle, player);
        GameTestSequence sequence = helper.startSequence();
        for (int i = 0; i < maxUses; i++) {
            int index = i + 1;
            sequence.thenExecute(() -> player.setItemInHand(InteractionHand.MAIN_HAND, bundle.use(helper.getLevel(), player, InteractionHand.MAIN_HAND) instanceof InteractionResult.Success success ? success.heldItemTransformedTo() instanceof ItemStack s ? s : bundle : bundle))
                    .thenExecuteAfter(duration + 1, () -> {
                        player.stopUsingItem();
                        assertBundleUsed(helper, player, index, maxUses, string, bundle);
                        assertItemsInInventory(helper, player.getInventory(), new ItemStack(Items.GLASS_BOTTLE), index, "Did not return glass bottle");
                        MobEffectInstance effect = player.getEffect(customEffect.getEffect());
                        if (effect == null || effect.getDuration() + 1 != customEffect.getDuration()) {
                            helper.fail("Did not apply effect");
                        }
                    });
        }
        sequence.thenSucceed();
    }

    @GameTest(template = "empty_3x3")
    public static void testSplashPotionBundle(GameTestHelper helper) {
        AbstractThrowablePotionBundle potionBundle = PotionBundlesItems.SPLASH_POTION_BUNDLE.get();
        testAbstractThrownPotionBundle(helper, potionBundle, EntityType.SPLASH_POTION);
    }

    @GameTest(template = "empty_3x3")
    public static void testLingeringPotionBundle(GameTestHelper helper) {
        AbstractThrowablePotionBundle potionBundle = PotionBundlesItems.LINGERING_POTION_BUNDLE.get();
        testAbstractThrownPotionBundle(helper, potionBundle, EntityType.LINGERING_POTION);
    }

    private static <T extends AbstractThrownPotion> void testAbstractThrownPotionBundle(GameTestHelper helper, AbstractThrowablePotionBundle potionBundle, EntityType<T> entityType) {
        PotionBundleString string = PotionBundleString.fromItem(Items.STRING);
        MobEffectInstance customEffect = new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20);
        int maxUses = potionBundle.getMaxUses();
        ItemStack bundle = potionBundle.createStack(string, Potions.WATER, List.of(customEffect), null, null);
        assertUses(helper, bundle, maxUses, "Uses don't use items max uses");
        Player player = setupPlayer(helper, bundle);
        GameTestSequence sequence = helper.startSequence();
        for (int i = 0; i < maxUses; i++) {
            int index = i + 1;
            sequence.thenExecute(() -> player.setItemInHand(InteractionHand.MAIN_HAND, bundle.use(helper.getLevel(), player, InteractionHand.MAIN_HAND) instanceof InteractionResult.Success success ? success.heldItemTransformedTo() instanceof ItemStack s ? s : bundle : bundle))
                    .thenExecute(() -> {
                        assertBundleUsed(helper, player, index, maxUses, string, bundle);
                        List<T> potions = helper.getEntities(entityType, new BlockPos(1, 1, 1), 2);
                        if (potions.isEmpty()) {
                            helper.fail("Did not throw potion");
                        }
                        if (potions.size() > 1) {
                            helper.fail("Threw too many potions");
                        }
                        AbstractThrownPotion abstractThrownPotion = potions.getFirst();
                        ItemStack abstractThrownPotionItem = abstractThrownPotion.getItem();
                        PotionContents potionContents = abstractThrownPotionItem.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                        List<MobEffectInstance> mobEffects = new ArrayList<>();
                        potionContents.forEachEffect(mobEffects::add, abstractThrownPotionItem.getOrDefault(DataComponents.POTION_DURATION_SCALE, 1f));
                        if (mobEffects.size() != 1 || !mobEffects.getFirst().equals(customEffect)) {
                            helper.fail("Wrong potion thrown");
                        }
                        if (potionBundle == PotionBundlesItems.SPLASH_POTION_BUNDLE.get()) {
                            if (!abstractThrownPotionItem.is(Items.SPLASH_POTION)) {
                                helper.fail("Wrong potion type thrown");
                            }
                        }
                        if (potionBundle == PotionBundlesItems.LINGERING_POTION_BUNDLE.get()) {
                            if (!abstractThrownPotionItem.is(Items.LINGERING_POTION)) {
                                helper.fail("Wrong potion type thrown");
                            }
                        }
                        abstractThrownPotion.discard();
                    });
        }
        sequence.thenSucceed();
    }

    private static void assertBundleUsed(GameTestHelper helper, Player player, int index, int maxUses, PotionBundleString string, ItemStack bundle) {
        ItemStack inHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        assertNotEmpty(helper, inHand, "Bundle should not be empty");
        if (index == maxUses) {
            assertSameItem(helper, inHand, string.toItemStack(), "Bundle should be empty and returned the string");
        } else {
            assertSameItem(helper, inHand, bundle, "Wrong item in hand");
            assertUses(helper, inHand, maxUses - index, "Uses not decremented");
        }
    }

    private static void assertSameItem(GameTestHelper helper, ItemStack stack, ItemStack expectes, String message) {
        if (!ItemStack.isSameItem(stack, expectes)) {
            helper.fail(message);
        }
    }

    private static void assertNotEmpty(GameTestHelper helper, ItemStack stack, String message) {
        if (stack.isEmpty()) {
            helper.fail(message);
        }
    }

    private static void assertUses(GameTestHelper helper, ItemStack bundle, int expectedUses, String message) {
        if (PotionBundleUtils.getUses(bundle) != expectedUses) {
            helper.fail(message);
        }
    }

    private static void assertItemsInInventory(GameTestHelper helper, Inventory inventory, ItemStack item, int count, String message) {
        int slot = inventory.findSlotMatchingItem(item);
        if (slot == -1 || inventory.getItem(slot).getCount() != count) {
            helper.fail(message);
        }
    }

    private static @NotNull Player setupPlayer(GameTestHelper helper, ItemStack bundle) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(helper.absoluteVec(Vec3.ZERO));
        helper.getLevel().addFreshEntity(player);
        player.setItemInHand(InteractionHand.MAIN_HAND, bundle);
        return player;
    }
}
