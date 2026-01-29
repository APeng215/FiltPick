package com.apeng.filtpick.hud;

import com.apeng.filtpick.Common;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles HUD rendering of items that were recently blocked by the filter.
 */
public class BlockedItemsHud {

    private static final Identifier BLOCKED_ITEM_SLOT_IDENTIFIER = Identifier.fromNamespaceAndPath(Common.MOD_ID, "hud/blocked_item_slot.png");
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
     * Renders the blocked items on the HUD at the bottom-right.
     * Shows up to 2 items with their slot backgrounds.
     * If there are 3+ items, the 3rd slot displays an ellipsis indicator.
     */
    public static void render(GuiGraphics context, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || blockedItems.isEmpty()) {
            return;
        }

        RenderContext renderContext = new RenderContext(context, mc);
        int currentY = renderContext.getStartY();

        int itemIndex = 0;
        for (Item item : blockedItems) {
            if (itemIndex >= 3) break;

            if (itemIndex < 2) {
                renderItemSlot(renderContext, item, currentY);
            } else {
                renderEllipsisSlot(renderContext, currentY);
            }

            currentY += renderContext.getSlotSize() + renderContext.getSpacing();
            itemIndex++;
        }
    }

    private static void renderItemSlot(RenderContext context, Item item, int slotY) {
        context.renderItem(item, slotY);
        context.renderSlotBackground(slotY, 0, 0);
    }

    private static void renderEllipsisSlot(RenderContext context, int slotY) {
        context.renderSlotBackground(slotY, 32, 0);
    }

    /**
     * Helper class to encapsulate rendering parameters and operations.
     */
    private static class RenderContext {
        private final GuiGraphics guiGraphics;
        private final int baseX;
        private final int baseY;
        private static final int SLOT_SIZE = 23;
        private static final int ITEM_OFFSET = 3;
        private static final int SPACING = 2;

        RenderContext(GuiGraphics guiGraphics, Minecraft mc) {
            this.guiGraphics = guiGraphics;
            this.baseX = mc.getWindow().getGuiScaledWidth() - (SLOT_SIZE + 10);
            this.baseY = mc.getWindow().getGuiScaledHeight() - (SLOT_SIZE + 10);
        }

        int getSlotSize() {
            return SLOT_SIZE;
        }

        int getSpacing() {
            return SPACING;
        }

        int getStartY() {
            int itemCount = Math.min(blockedItems.size(), 3);
            return baseY - (itemCount - 1) * (SLOT_SIZE + SPACING);
        }

        void renderItem(Item item, int slotY) {
            guiGraphics.renderFakeItem(new ItemStack(item), baseX + ITEM_OFFSET, slotY + ITEM_OFFSET);
        }

        void renderSlotBackground(int slotY, int textureU, int textureV) {
            guiGraphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    BLOCKED_ITEM_SLOT_IDENTIFIER,
                    baseX, slotY,
                    textureU, textureV,
                    SLOT_SIZE, SLOT_SIZE,
                    64, 64
            );
        }
    }

    /**
     * Clears tracked items on logout or dimension change.
     */
    public static void clear() {
        blockedItems.clear();
    }

}
