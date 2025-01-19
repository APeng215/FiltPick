package com.apeng.filtpick.guis.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.awt.event.KeyEvent;

public class ScrollBlock extends ClickableWidget {

    private final int upBoundY;
    private final int scrollSlotHeight; // Include scroll block itself
    private final int scrollSpaceY; // NOT include scroll block itself

    /**
     * ScrollBar constructed by this method is active by default.
     *
     * @param pX               x position of the scroll bar in its parent
     * @param pY               y position of the scroll bar in its parent
     * @param scrollSlotHeight the height of the slot in which the scroll bar scrolls.
     */
    public ScrollBlock(int pX, int pY, int scrollSlotHeight) {
        super(pX, pY, ScrollBlockResource.WIDTH, ScrollBlockResource.HEIGHT, Text.empty());
        this.upBoundY = pY;
        this.scrollSlotHeight = scrollSlotHeight;
        this.scrollSpaceY = scrollSlotHeight - ScrollBlockResource.HEIGHT;
    }

    /**
     * @param pX               x position of the scroll bar in its parent
     * @param pY               y position of the scroll bar in its parent
     * @param scrollSlotHeight the height of the slot in which the scroll bar scrolls.
     * @param active           if scroll block is active
     */
    public ScrollBlock(int pX, int pY, int scrollSlotHeight, boolean active) {
        super(pX, pY, ScrollBlockResource.WIDTH, ScrollBlockResource.HEIGHT, Text.empty());
        this.upBoundY = pY;
        this.scrollSlotHeight = scrollSlotHeight;
        this.scrollSpaceY = scrollSlotHeight - ScrollBlockResource.HEIGHT;
        this.active = active;
    }

    /**
     * Render widget itself, excluding tooltip.
     *
     * @param guiGraphics
     * @param pMouseX
     * @param pMouseY
     * @param pPartialTick
     */
    @Override
    protected void renderWidget(DrawContext guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (active) {
            guiGraphics.drawGuiTexture(
                    ScrollBlockResource.SPRITE_LOCATION,
                    getX(),
                    getY(),
                    ScrollBlockResource.WIDTH,
                    ScrollBlockResource.HEIGHT
            );
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder pNarrationElementOutput) {
    }

    /**
     * Called when the mouse wheel is scrolled within the GUI element.
     *
     * @param pMouseX the X coordinate of the mouse.
     * @param pMouseY the Y coordinate of the mouse.
     * @param pDeltaX the scrolling X delta.
     * @param pDeltaY the scrolling Y delta.
     * @return {@code true} if the scroll bar is active, {@code false} otherwise.
     */
    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDeltaX, double pDeltaY) {
        if (active) {
            setY(MathHelper.clamp(getY() - (int) (2 * pDeltaY), upBoundY, upBoundY + scrollSpaceY));
            return true;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDeltaX, pDeltaY);
    }

    /**
     * Called when a keyboard key is pressed within the GUI element.
     *
     * @param pKeyCode   the key code of the pressed key.
     * @param pScanCode  the scan code of the pressed key.
     * @param pModifiers the keyboard modifiers.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (active) {
            pKeyCode = KeyEvent.VK_UP;
            switch (pKeyCode) {
                case KeyEvent.VK_UP -> {
                    // TODO
                }
                case KeyEvent.VK_DOWN -> {
                    // TODO
                }
            }
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
        setY(MathHelper.clamp((int) pMouseY - ScrollBlockResource.HEIGHT / 2, upBoundY, upBoundY + scrollSpaceY));
    }

    /**
     * @return the height including scroll block itself
     * @see #getScrollSpaceY()
     */
    public int getScrollSlotHeight() {
        return scrollSlotHeight;
    }

    /**
     * @return the space NOT including scroll block itself
     * @see #getScrollSlotHeight()
     */
    public int getScrollSpaceY() {
        return scrollSpaceY;
    }

    /**
     * Returns how far the scroll block has slided from the start by ratio.
     * Will return 0 if the scroll block is at the start
     * or return 1 if the scroll is at the end.
     *
     * @return
     */
    public double getPosRatio() {
        int offsetY = getY() - upBoundY;
        return offsetY / (double) scrollSpaceY;
    }

    /**
     * Set scroll block position by ratio. If the ratio is or below 0, scroll block will be set to the start. Same to at and above 1.
     *
     * @param ratio
     */
    public void setPosByRatio(double ratio) {
        double safeRatio = MathHelper.clamp(ratio, 0, 1);
        int offsetY = (int) (scrollSpaceY * safeRatio);
        setY(upBoundY + offsetY);
    }

    public static class ScrollBlockResource {
        // Use guiGraphics#blitSprite to render the block instead of #blit
        public static final Identifier SPRITE_LOCATION = new Identifier("container/creative_inventory/scroller");
        public static final Identifier SPRITE_LOCATION_DISABLED = new Identifier("container/creative_inventory/scroller_disabled");
        public static final int U = 0;
        public static final int V = 0;
        public static final int WIDTH = 12;
        public static final int HEIGHT = 15;
    }

}
