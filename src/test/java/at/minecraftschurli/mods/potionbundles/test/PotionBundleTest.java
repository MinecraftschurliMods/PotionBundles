package at.minecraftschurli.mods.potionbundles.test;

import at.minecraftschurli.mods.potionbundles.PotionBundles;
import at.minecraftschurli.mods.potionbundles.PotionBundlesItems;
import at.minecraftschurli.mods.potionbundles.item.AbstractPotionBundle;
import at.minecraftschurli.mods.potionbundles.item.AbstractThrowablePotionBundle;
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
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

@ForEachTest(groups = PotionBundles.MODID)
public class PotionBundleTest {
    @GameTest(template = Tests.TEMPLATE_3x3)
    @TestHolder(description = "Tests the potion bundle item functionality")
    public static void testPotionBundle(GameTestHelper helper) {
        PotionContents potionContents = new PotionContents(Optional.empty(), Optional.empty(), List.of(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20)), Optional.empty());
        testPotionBundle(helper, PotionBundlesItems.POTION_BUNDLE.get(), PotionBundleString.fromItem(Items.STRING), potionContents, (index, player) -> {
            assertItemsInInventory(helper, player.getInventory(), new ItemStack(Items.GLASS_BOTTLE), index, "Did not return glass bottle");
            assertSameEffects(helper, potionContents, 1.0f, player.getActiveEffects(), "Wrong effects applied");
            player.removeAllEffects();
        });
    }

    @GameTest(template = Tests.TEMPLATE_3x3)
    @TestHolder(description = "Tests the splash potion bundle item functionality")
    public static void testSplashPotionBundle(GameTestHelper helper) {
        AbstractThrowablePotionBundle potionBundle = PotionBundlesItems.SPLASH_POTION_BUNDLE.get();
        testAbstractThrownPotionBundle(helper, potionBundle, Items.SPLASH_POTION, 1.0f, EntityTypes.SPLASH_POTION);
    }

    @GameTest(template = Tests.TEMPLATE_3x3)
    @TestHolder(description = "Tests the lingering potion bundle item functionality")
    public static void testLingeringPotionBundle(GameTestHelper helper) {
        AbstractThrowablePotionBundle potionBundle = PotionBundlesItems.LINGERING_POTION_BUNDLE.get();
        testAbstractThrownPotionBundle(helper, potionBundle, Items.LINGERING_POTION, 0.25F, EntityTypes.LINGERING_POTION);
    }

    private static void testPotionBundle(GameTestHelper helper, AbstractPotionBundle potionBundle, PotionBundleString string, PotionContents potionContents, BiConsumer<Integer, Player> assertFunction) {
        int maxUses = potionBundle.getMaxUses();
        ItemStack bundle = potionBundle.createStack(string, potionContents);
        assertUses(helper, bundle, maxUses, "Uses don't use items max uses");
        Player player = setupPlayer(helper, bundle);
        int duration = potionBundle.getUseDuration(bundle, player);
        GameTestSequence sequence = helper.startSequence();
        for (int i = 0; i < maxUses; i++) {
            int index = i + 1;
            Runnable assertions = () -> {
                player.stopUsingItem();
                assertBundleUsed(helper, player, index, maxUses, string, bundle);
                assertFunction.accept(index, player);
            };
            sequence.thenExecute(() -> player.setItemInHand(InteractionHand.MAIN_HAND, bundle.use(helper.getLevel(), player, InteractionHand.MAIN_HAND) instanceof InteractionResult.Success success ? success.heldItemTransformedTo() instanceof ItemStack s ? s : bundle : bundle));
            if (duration > 0) {
                sequence.thenExecuteAfter(duration, assertions);
            } else {
                sequence.thenExecute(assertions);
            }
        }
        sequence.thenSucceed();
    }

    private static <T extends AbstractThrownPotion> void testAbstractThrownPotionBundle(GameTestHelper helper, AbstractThrowablePotionBundle potionBundle, Item potionItem, float durationScale, EntityType<T> entityType) {
        PotionBundleString string = PotionBundleString.fromItem(Items.STRING);
        testPotionBundle(helper, potionBundle, string, new PotionContents(Optional.empty(), Optional.empty(), List.of(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20)), Optional.empty()), (_, _) -> {
            List<T> potions = helper.getEntities(entityType, new BlockPos(1, 1, 1), 2);
            if (potions.isEmpty()) {
                helper.fail("Did not throw potion");
            }
            if (potions.size() > 1) {
                helper.fail("Threw too many potions");
            }
            AbstractThrownPotion abstractThrownPotion = potions.getFirst();
            ItemStack abstractThrownPotionItem = abstractThrownPotion.getItem();
            if (!abstractThrownPotionItem.is(potionItem)) {
                helper.fail("Wrong potion type thrown");
            }
            float actualDurationScale = abstractThrownPotionItem.getOrDefault(DataComponents.POTION_DURATION_SCALE, 1f);
            if (actualDurationScale != durationScale) {
                helper.fail("Potion with wrong duration scale thrown");
            }
            PotionContents potionContents = abstractThrownPotionItem.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            List<MobEffectInstance> mobEffects = new ArrayList<>();
            potionContents.forEachEffect(mobEffects::add, abstractThrownPotionItem.getOrDefault(DataComponents.POTION_DURATION_SCALE, 1f));
            assertSameEffects(helper, potionContents, durationScale, mobEffects, "Potion with wrong effects thrown");
            abstractThrownPotion.discard();
        });
    }

    private static void assertSameEffects(GameTestHelper helper, PotionContents expectedContents, float durationScale, Collection<MobEffectInstance> actualEffects, String message) {
        List<MobEffectInstance> actualList = new ArrayList<>(actualEffects);
        List<MobEffectInstance> expectedList = new ArrayList<>();
        expectedContents.forEachEffect(expectedList::add, durationScale);
        if (actualList.size() != expectedList.size()) {
            helper.fail(message + ": Expected " + expectedList.size() + " effects, but got " + actualList.size());
        }
        for (MobEffectInstance expected : expectedList) {
            boolean found = false;
            for (MobEffectInstance actual : actualList) {
                if (actual.equals(expected)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                helper.fail(message + ": Expected effect " + expected + " not found");
            }
        }
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
        try (Transaction transaction = Transaction.openRoot()) {
            int actualCount = PlayerInventoryWrapper.of(inventory).extract(ItemResource.of(item), count, transaction);
            if (actualCount != count) {
                helper.fail(message);
            }
        }
    }

    private static Player setupPlayer(GameTestHelper helper, ItemStack bundle) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(helper.absoluteVec(Vec3.ZERO));
        helper.getLevel().addFreshEntity(player);
        player.setItemInHand(InteractionHand.MAIN_HAND, bundle);
        return player;
    }
}
