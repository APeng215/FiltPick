package com.apeng.filtpick.mixinduck;

import net.minecraft.world.item.Item;

import java.util.Set;

public interface BlockedItemsContainer {
    /**
     * Marks an item that was prevented from being picked up by the filter.
     */
    void markItemAsBlocked(Item item);

    /**
     * Returns the set of items blocked during the current tick.
     */
    Set<Item> getCurrentlyBlockedItems();

    /**
     * Clears the current list of blocked items.
     */
    void clearCurrentlyBlockedItems();
}

