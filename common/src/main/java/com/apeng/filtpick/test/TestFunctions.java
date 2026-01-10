package com.apeng.filtpick.test;

import com.apeng.filtpick.gui.screen.FiltPickScreen;
import com.apeng.filtpick.mixinduck.FiltListContainer;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

/**
 * All the static functions here that consume only GameTestHelper will be registered. The resource location will be the
 * snake case of the original function name. For example, exampleTest -> example_test.
 */
@SuppressWarnings({"removal", "unused"})
public class TestFunctions {

    public static void testClearListButton(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.gameMode.changeGameModeForPlayer(GameType.SURVIVAL);
        FiltListContainer filtListContainer = (FiltListContainer) player;

        // --- setup ---
        filtListContainer.getFiltList().addItem(new ItemStack(Items.DIRT));
        filtListContainer.getFiltList().addItem(new ItemStack(Items.DIAMOND));

        filtListContainer.getFiltListPropertyDelegate()
                .set(FiltPickScreen.WHITELIST_MODE_BUTTON_ID, 1);
        filtListContainer.getFiltListPropertyDelegate()
                .set(FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID, 1);

        // sanity check
        helper.assertTrue(
                filtListContainer.getFiltList().getContainerSize() > 0,
                Component.literal("Filter list should not be empty before clearing")
        );

        // --- act: simulate clear list button ---
        filtListContainer.resetFiltListWithProperties();

        // --- verify (delay to allow sync if needed) ---
        helper.runAfterDelay(1, () -> helper.succeedIf(() -> {
            // list cleared
            helper.assertTrue(
                    filtListContainer.getFiltList().isEmpty(),
                    Component.literal("Filter list should be empty after clear")
            );

            // properties reset
            helper.assertValueEqual(
                    0,
                    filtListContainer.getFiltListPropertyDelegate()
                            .get(FiltPickScreen.WHITELIST_MODE_BUTTON_ID),
                    Component.literal("WHITELIST_MODE")
            );

            helper.assertValueEqual(
                    0,
                    filtListContainer.getFiltListPropertyDelegate()
                            .get(FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID),
                    Component.literal("DESTRUCTION_MODE")
            );
            player.remove(Entity.RemovalReason.DISCARDED);
        }));
    }


    /**
     * Including destruction feature test.
     * @param helper
     */
    public static void testWhitelist(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.gameMode.changeGameModeForPlayer(GameType.SURVIVAL);
        FiltListContainer filtListContainer = ((FiltListContainer) player);

        filtListContainer.getFiltListPropertyDelegate().set(FiltPickScreen.WHITELIST_MODE_BUTTON_ID, 1);

        // Assert initial states
        helper.assertValueEqual(1, filtListContainer.getFiltListPropertyDelegate().get(FiltPickScreen.WHITELIST_MODE_BUTTON_ID), Component.literal("WHITELIST_MODE"));
        helper.assertValueEqual(0, filtListContainer.getFiltListPropertyDelegate().get(FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID), Component.literal("DESTRUCTION_MODE"));

        // Add filt item
        filtListContainer.getFiltList().addItem(new ItemStack(Items.DIAMOND));

        // Generate item entity
        ItemEntity dirtEntity = helper.spawnItem(Items.DIRT, 2.5f, 1, 2.5f);
        ItemEntity diamondEntity = helper.spawnItem(Items.DIAMOND, 2.6f, 1, 2.6f);
        player.teleportTo(2.5f, 1, 2.5f);
        dirtEntity.playerTouch(player);
        diamondEntity.playerTouch(player);

        // Test
        helper.runAfterDelay(10, () -> {
            helper.assertItemEntityPresent(Items.DIRT);
            helper.assertItemEntityNotPresent(Items.DIAMOND);
            filtListContainer.getFiltListPropertyDelegate().set(FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID, 1);
            dirtEntity.playerTouch(player);
        });

        helper.runAfterDelay(20, () -> helper.succeedIf(() -> {
            helper.assertItemEntityNotPresent(Items.DIRT);
            player.remove(Entity.RemovalReason.DISCARDED);
        }));
    }

    /**
     * Including destruction feature test.
     * @param helper
     */
    public static void testBlacklist(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.gameMode.changeGameModeForPlayer(GameType.SURVIVAL);
        FiltListContainer filtListContainer = ((FiltListContainer) player);

        // Assert initial states
        helper.assertValueEqual(0, filtListContainer.getFiltListPropertyDelegate().get(FiltPickScreen.WHITELIST_MODE_BUTTON_ID), Component.literal("WHITELIST_MODE"));
        helper.assertValueEqual(0, filtListContainer.getFiltListPropertyDelegate().get(FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID), Component.literal("DESTRUCTION_MODE"));

        // Add filt item
        filtListContainer.getFiltList().addItem(new ItemStack(Items.DIRT));

        // Generate item entity
        ItemEntity dirtEntity = helper.spawnItem(Items.DIRT, 2.5f, 1, 2.5f);
        ItemEntity diamondEntity = helper.spawnItem(Items.DIAMOND, 2.6f, 1, 2.6f);
        player.teleportTo(2.5f, 1, 2.5f);
        dirtEntity.playerTouch(player);
        diamondEntity.playerTouch(player);

        // Test
        helper.runAfterDelay(10, () -> {
            helper.assertItemEntityPresent(Items.DIRT);
            helper.assertItemEntityNotPresent(Items.DIAMOND);
            filtListContainer.getFiltListPropertyDelegate().set(FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID, 1);
            dirtEntity.playerTouch(player);
        });

        helper.runAfterDelay(20, () -> helper.succeedIf(() -> {
            helper.assertItemEntityNotPresent(Items.DIRT);
            player.remove(Entity.RemovalReason.DISCARDED);
        }));
    }
}