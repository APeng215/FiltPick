package com.apeng.filtpick.tracker;

import com.apeng.filtpick.Common;
import com.apeng.filtpick.mixinduck.BlockedItemsContainer;
import com.apeng.filtpick.network.SyncBlockedItemsS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.*;

/**
 * Handles server-side tracking and syncing of blocked items to clients for HUD rendering.
 */
public class BlockedItemsTracker {

    /** Tracks the last set of items sent to each player to avoid redundant updates */
    private static final Map<UUID, Set<Item>> lastSentItems = new HashMap<>();
    /** Accumulates blocked items throughout the sync interval */
    private static final Map<UUID, Set<Item>> accumulatedBlockedItems = new HashMap<>();

    /** The interval at which the server reports blocked items to clients */
    public static final int SYNC_INTERVAL_TICKS = 2;

    /**
     * Entry point for per-player tick logic. Processes and periodically synchronizes
     * blocked items to the client.
     */
    public static void onPlayerTick(ServerPlayer player) {
        UUID uuid = player.getUUID();
        BlockedItemsContainer container = (BlockedItemsContainer) player;
        Set<Item> currentTickBlocked = container.getCurrentlyBlockedItems();

        if (!currentTickBlocked.isEmpty()) {
            accumulatedBlockedItems.computeIfAbsent(uuid, k -> new HashSet<>()).addAll(currentTickBlocked);
        }

        if (isSyncDue(player)) {
            performSync(player, uuid);
        }

        container.clearCurrentlyBlockedItems();
    }

    private static boolean isSyncDue(ServerPlayer player) {
        return player.tickCount % SYNC_INTERVAL_TICKS == 0;
    }

    private static void performSync(ServerPlayer player, UUID uuid) {
        Set<Item> currentBlocked = accumulatedBlockedItems.getOrDefault(uuid, Collections.emptySet());
        Set<Item> lastBlocked = lastSentItems.getOrDefault(uuid, Collections.emptySet());

        // Sync only if the blocked items set has changed since the last sync
        if (!currentBlocked.equals(lastBlocked)) {
            sendUpdatePacket(player, currentBlocked);

            if (currentBlocked.isEmpty()) {
                lastSentItems.remove(uuid);
            } else {
                lastSentItems.put(uuid, new HashSet<>(currentBlocked));
            }
        }

        accumulatedBlockedItems.remove(uuid);
    }

    private static void sendUpdatePacket(ServerPlayer player, Set<Item> blockedItems) {
        Common.getNetworkHandler().sendToPlayer(new SyncBlockedItemsS2CPacket(blockedItems), player);
    }

    /**
     * Cleanup player data on logout to prevent memory leaks.
     */
    public static void onPlayerLogout(ServerPlayer player) {
        UUID uuid = player.getUUID();
        lastSentItems.remove(uuid);
        accumulatedBlockedItems.remove(uuid);
    }

    /**
     * Clear all tracking data (e.g. on server stop).
     */
    public static void clear() {
        lastSentItems.clear();
        accumulatedBlockedItems.clear();
    }
}