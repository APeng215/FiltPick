package com.apeng.filtpick.gui.screen;


import com.apeng.filtpick.Common;
import com.apeng.filtpick.config.FiltPickClientConfig;
import com.apeng.filtpick.gui.widget.ContainerScrollBlock;
import com.apeng.filtpick.gui.widget.LegacyTexturedButton;
import com.apeng.filtpick.util.IntBoolConvertor;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;

import java.time.Duration;

public class FiltPickScreen extends AbstractContainerScreen<FiltPickMenu> {

    public static final int WHITELIST_MODE_BUTTON_ID = 0;
    public static final int DESTRUCTION_MODE_BUTTON_ID = 1;
    public static final int CLEAR_BUTTON_ID = 2;
    private static final Style EXPLANATION_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).applyFormats(ChatFormatting.ITALIC);
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.tryParse("textures/gui/container/generic_54.png"); // This image includes both container window and inventory window
    private static final ResourceLocation FILT_MODE_BUTTON_TEXTURE = ResourceLocation.tryBuild(Common.MOD_ID, "gui/filtmode_button.png");
    private static final ResourceLocation DESTRUCTION_BUTTON_TEXTURE = ResourceLocation.tryBuild(Common.MOD_ID, "gui/destruction_button.png");
    private static final ResourceLocation CLEAR_BUTTON_TEXTURE = ResourceLocation.tryBuild(Common.MOD_ID, "gui/clearlist_button.png");
    private static final ResourceLocation RETURN_BUTTON_TEXTURE = ResourceLocation.tryBuild(Common.MOD_ID, "gui/return_button.png");

    private FPToggleButton filtModeButton, destructionButton;
    private LegacyTexturedButton clearButton, returnButton;
    private ContainerScrollBlock scrollBlock;

    public FiltPickScreen(FiltPickMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        initCoordinates();
        addButtons();
        addScrollBlock();
    }

    private void addScrollBlock() {
        scrollBlock = new ContainerScrollBlock(
                leftPos + imageWidth + 1,
                topPos + 17,
                Common.getServerConfig().FILTLIST_DISPLAYED_ROW_COUNT.get() * 18,
                Common.getServerConfig().FILTLIST_DISPLAYED_ROW_COUNT.get(),
                Common.getServerConfig().CONTAINER_ROW_COUNT.get()
        );
        this.addRenderableWidget(scrollBlock);
    }

    /**
     * @param pMouseX
     * @param pMouseY
     * @param pDeltaX
     * @param pDeltaY >0 means scrolling up; <0 means scrolling down
     * @return
     */
    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDeltaX, double pDeltaY) {
        if (!super.mouseScrolled(pMouseX, pMouseY, pDeltaX, pDeltaY)) {
            scrollMenu(pDeltaY);
        }
        return true;
    }

    private void scrollMenu(double pDeltaY) {
        if (pDeltaY > 0) {
            scrollUpListAndSyn();
        } else {
            scrollDownListAndSyn();
        }
    }

    private void scrollDownListAndSyn() {
        if (menu.safeIncreaseDisplayedRowOffsetAndUpdate()) {
            scrollBlock.setRowOffset(menu.getDisplayedRowOffset());
        }
    }

    private void scrollUpListAndSyn() {
        if (menu.safeDecreaseDisplayedRowOffsetAndUpdate()) {
            scrollBlock.setRowOffset(menu.getDisplayedRowOffset());
        }
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.getFocused() instanceof ContainerScrollBlock scrollBar && this.isDragging() && pButton == 0) {
            return scrollBlockDragged(pMouseX, pMouseY, pButton, pDragX, pDragY, scrollBar);
        } else {
            return normalDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        this.setFocused(null);
        this.setFocused(false);
        return super.mouseReleased($$0, $$1, $$2);
    }

    private boolean scrollBlockDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY, ContainerScrollBlock scrollBlock) {
        boolean flag = scrollBlock.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        menu.setDisplayedRowOffsetAndUpdate(scrollBlock.getDisplayedRowOffset());
        return flag;
    }

    private boolean normalDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.getFocused() != null && this.isDragging() && pButton == 0) {
            return this.getFocused().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }


    private void initCoordinates() {
        this.imageHeight = 114 + Common.getServerConfig().FILTLIST_DISPLAYED_ROW_COUNT.get() * 18;
        this.inventoryLabelY = this.imageHeight - 94;
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.titleLabelX = 72;
    }

    private void addButtons() {
        addFiltModeButton();
        addDestructionButton();
        addClearButton();
        addReturnButton();
    }

    private void addFiltModeButton() {
        filtModeButton = new FPToggleButton(
                this.leftPos + 10 + Common.getClientConfig().buttonOffsets.get(FiltPickClientConfig.ButtonName.FILT_MODE_BUTTON).horizontalOffset().get(),
                this.topPos + 4 + Common.getClientConfig().buttonOffsets.get(FiltPickClientConfig.ButtonName.FILT_MODE_BUTTON).verticalOffset().get(),
                12,
                11,
                FILT_MODE_BUTTON_TEXTURE,
                WHITELIST_MODE_BUTTON_ID
        );
        filtModeButton.setTooltips(Component.translatable("whitelist_mode").append("\n").withStyle(ChatFormatting.DARK_GREEN).append(Component.translatable("whitelist_mode_explanation").withStyle(EXPLANATION_STYLE)), Component.translatable("blacklist_mode").append("\n").withStyle(ChatFormatting.DARK_RED).append(Component.translatable("blacklist_mode_explanation").withStyle(EXPLANATION_STYLE)));
        addRenderableWidget(filtModeButton);
    }

    private void addDestructionButton() {
        destructionButton = new FPToggleButton(
                this.leftPos + 10 + 2 + 12 + Common.getClientConfig().buttonOffsets.get(FiltPickClientConfig.ButtonName.DESTRUCTION_MODE_BUTTON).horizontalOffset().get(),
                this.topPos + 4 + Common.getClientConfig().buttonOffsets.get(FiltPickClientConfig.ButtonName.DESTRUCTION_MODE_BUTTON).verticalOffset().get(),
                12,
                11,
                DESTRUCTION_BUTTON_TEXTURE,
                DESTRUCTION_MODE_BUTTON_ID
        );
        destructionButton.setTooltips(Component.translatable("destruction_mode_on").withStyle(ChatFormatting.DARK_RED).append("\n").append(Component.translatable("destruction_mode_on_explanation").withStyle(EXPLANATION_STYLE)), Component.translatable("destruction_mode_off").withStyle(ChatFormatting.DARK_GRAY));
        addRenderableWidget(destructionButton);
    }

    private void addClearButton() {
        clearButton = new LegacyTexturedButton(
                this.leftPos + 154 - 14 + Common.getClientConfig().buttonOffsets.get(FiltPickClientConfig.ButtonName.CLEAR_BUTTON).horizontalOffset().get(),
                this.topPos + 4 + Common.getClientConfig().buttonOffsets.get(FiltPickClientConfig.ButtonName.CLEAR_BUTTON).verticalOffset().get(),
                12,
                11,
                0,
                0,
                12,
                CLEAR_BUTTON_TEXTURE,
                button -> sendButtonClickC2SPacket(CLEAR_BUTTON_ID)
        );
        setTooltip2ClearButton();
        addRenderableWidget(clearButton);
    }

    private void setTooltip2ClearButton() {
        clearButton.setTooltip(Tooltip.create(Component.translatable("reset_explanation").withStyle(EXPLANATION_STYLE)));
        clearButton.setTooltipDelay(Duration.ofMillis(500));
    }

    private void addReturnButton() {
        returnButton = new LegacyTexturedButton(
                this.leftPos + 154 + Common.getClientConfig().buttonOffsets.get(FiltPickClientConfig.ButtonName.RETURN_BUTTON).horizontalOffset().get(),
                this.topPos + 4 + Common.getClientConfig().buttonOffsets.get(FiltPickClientConfig.ButtonName.RETURN_BUTTON).verticalOffset().get(),
                12,
                11,
                0,
                0,
                12,
                RETURN_BUTTON_TEXTURE,
                12,
                11 * 2 + 1,
                button -> {
                    this.onClose();
                    minecraft.setScreen(new InventoryScreen(minecraft.player));
                }
        );
        addRenderableWidget(returnButton);

    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderTitle(context, font, Component.translatable("filtpick_screen_name"), 72, topPos + 4, width - 72, topPos + 14, 0x404040);
        this.renderTooltip(context, mouseX, mouseY);
    }

    /**
     * This approach to render can roll the title if it's too long
     *
     * @param context
     * @param textRenderer
     * @param text
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param color
     */
    protected void renderTitle(GuiGraphics context, Font textRenderer, Component text, int startX, int startY, int endX, int endY, int color) {
        int centerX = (startX + endX) / 2;
        int i = textRenderer.width(text);
        int j = (startY + endY - textRenderer.lineHeight) / 2 + 2;
        int k = endX - startX;
        if (i > k) {
            int l = i - k;
            double d = (double) Util.getMillis() / 1000.0;
            double e = Math.max((double) l * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(Math.PI * 2 * d / e)) / 2.0 + 0.5;
            double g = Mth.lerp(f, 0.0, l);
            context.enableScissor(startX, startY, endX, endY);
            context.drawString(textRenderer, text, startX - (int) g, j, color, false);
            context.disableScissor();
        } else {
            int l = Mth.clamp(centerX, startX + i / 2, endX - i / 2);
            FormattedCharSequence orderedText = text.getVisualOrderText();
            context.drawString(textRenderer, orderedText, centerX - textRenderer.width(orderedText) / 2, j, color, false);
        }
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        renderFiltPickContainer(context);
        renderInventory(context);
    }

    private void renderInventory(GuiGraphics context) {
        context.blit(
                RenderType::guiTextured,
                CONTAINER_BACKGROUND,
                leftPos,
                topPos + Common.getServerConfig().FILTLIST_DISPLAYED_ROW_COUNT.get() * 18 + 17,
                0,
                126,
                imageWidth,
                96,
                256,
                256
        );
    }

    private void renderFiltPickContainer(GuiGraphics context) {
        context.blit(
                RenderType::guiTextured,
                CONTAINER_BACKGROUND,
                leftPos,
                topPos,
                0,
                0,
                imageWidth,
                Common.getServerConfig().FILTLIST_DISPLAYED_ROW_COUNT.get() * 18 + 17,
                256,
                256
        );
    }

    private void sendButtonClickC2SPacket(int buttonId) {
        Minecraft.getInstance().getConnection().send(new ServerboundContainerButtonClickPacket(menu.containerId, buttonId));
    }

    class FPToggleButton extends AbstractWidget {
        private final ContainerData propertyDelegate = menu.getPropertyDelegate();
        private final int buttonId;
        private final ResourceLocation texture;
        private final WidgetTooltipHolder tureTooltip = new WidgetTooltipHolder();
        private final WidgetTooltipHolder falseTooltip = new WidgetTooltipHolder();

        public FPToggleButton(int x, int y, int width, int height, ResourceLocation texture, int buttonId) {
            this(x, y, width, height, Component.empty(), texture, buttonId);
        }

        public FPToggleButton(int x, int y, int width, int height, Component message, ResourceLocation texture, int buttonId) {
            super(x, y, width, height, message);
            this.texture = texture;
            this.buttonId = buttonId;
        }

        @Override
        protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
            if (!visible) {
                return;
            }
            renderTexture(context);
            renderTooltip();
        }

        private void renderTooltip() {
            if (correspondPropertyTrue() && tureTooltip != null) {
                tureTooltip.refreshTooltipForNextRenderPass(isHovered(), isFocused(), getRectangle());
            }
            if (!correspondPropertyTrue() && falseTooltip != null) {
                falseTooltip.refreshTooltipForNextRenderPass(isHovered(), isFocused(), getRectangle());
            }
        }


        private void renderTexture(GuiGraphics context) {
            int u = 0, v = 0;
            v = setVerticalOffset(v);
            u = setHorizontalOffset(u);
            context.blit(
                    RenderType::guiTextured,
                    texture,
                    this.getX(),
                    this.getY(),
                    u,
                    v,
                    width,
                    height,
                    2 * width + 1,
                    2 * height + 1
            );
        }

        private int setHorizontalOffset(int u) {
            if (!correspondPropertyTrue()) u += width + 1;
            return u;
        }

        private int setVerticalOffset(int v) {
            if (isHovered) v += height + 1;
            return v;
        }

        private boolean correspondPropertyTrue() {
            return IntBoolConvertor.toBool(propertyDelegate.get(buttonId));
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput builder) {
        }

        /**
         * Callback for when a mouse button scroll event
         * has been captured.
         *
         * @param mouseX           the X coordinate of the mouse
         * @param mouseY           the Y coordinate of the mouse
         * @param horizontalAmount the horizontal scroll amount
         * @param verticalAmount   the vertical scroll amount
         * @return {@code true} to indicate that the event handling is successful/valid
         */
        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            if (this.visible && this.isHovered) {
                sendButtonClickC2SPacket(buttonId);
                return true;
            }
            return false;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            sendButtonClickC2SPacket(buttonId);
        }

        public void setTooltips(Component tureTooltip, Component falseTooltip) {
            this.tureTooltip.set(Tooltip.create(tureTooltip));
            this.falseTooltip.set(Tooltip.create(falseTooltip));
        }
    }

}
