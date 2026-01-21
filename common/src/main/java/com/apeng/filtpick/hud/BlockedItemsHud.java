package com.apeng.filtpick.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles HUD rendering of items that were recently blocked by the filter.
 */
public class BlockedItemsHud {
    private static Set<Item> blockedItems = new HashSet<>();

    /**
     * Updates the HUD with newly blocked items received from the server.
     * Directly overwrites the previous state.
     *
     * @param items Set of items to display
     */
    public static void updateBlockedItems(Set<Item> items) {
        blockedItems = items;
    }

    /**
     * Renders the blocked items on the HUD.
     */
    public static void render(GuiGraphics context, DeltaTracker tickCounter) {
        // TODO: Optimize rendering to avoid performance hits with many items
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || blockedItems.isEmpty()) {
            return;
        }

        int x = 10;
        int y = mc.getWindow().getGuiScaledHeight() / 2 - (blockedItems.size() * 20) / 2;

        for (Item item : blockedItems) {
            context.renderFakeItem(new ItemStack(item), x, y);
            y += 20;
        }
    }

    /**
     * Clears tracked items on logout or dimension change.
     */
    public static void clear() {
        blockedItems.clear();
    }

}
