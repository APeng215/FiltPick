package com.apeng.filtpick.guis.screen;

import com.apeng.filtpick.FiltPick;
import com.apeng.filtpick.FiltPickClient;
import com.apeng.filtpick.config.FiltPickClientConfig;
import com.apeng.filtpick.guis.widget.ContainerScrollBlock;
import com.apeng.filtpick.guis.widget.LegacyTexturedButtonWidget;
import com.apeng.filtpick.util.IntBoolConvertor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipState;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.time.Duration;

public class FiltPickScreen extends HandledScreen<FiltPickScreenHandler> {

    public static final int WHITELIST_MODE_BUTTON_ID = 0;
    public static final int DESTRUCTION_MODE_BUTTON_ID = 1;
    public static final int CLEAR_BUTTON_ID = 2;
    private static final Style EXPLANATION_STYLE = Style.EMPTY.withColor(Formatting.DARK_GRAY).withFormatting(Formatting.ITALIC);
    private static final Identifier CONTAINER_BACKGROUND = new Identifier("textures/gui/container/generic_54.png"); //
    private static final Identifier FILT_MODE_BUTTON_TEXTURE = Identifier.of(FiltPick.ID, "gui/filtmode_button.png");
    private static final Identifier DESTRUCTION_BUTTON_TEXTURE = Identifier.of(FiltPick.ID, "gui/destruction_button.png");
    private static final Identifier CLEAR_BUTTON_TEXTURE = Identifier.of(FiltPick.ID, "gui/clearlist_button.png");
    private static final Identifier RETURN_BUTTON_TEXTURE = Identifier.of(FiltPick.ID, "gui/return_button.png");

    private FPToggleButton filtModeButton, destructionButton;
    private LegacyTexturedButtonWidget clearButton, returnButton;
    private ContainerScrollBlock scrollBlock;

    public FiltPickScreen(FiltPickScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        initCoordinates();
        addButtons();
        addScrollBlock();
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.getFocused() instanceof ContainerScrollBlock scrollBar && this.isDragging() && pButton == 0) {
            return scrollBlockDragged(pMouseX, pMouseY, pButton, pDragX, pDragY, scrollBar);
        } else {
            return normalDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
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
        if (handler.safeIncreaseDisplayedRowOffsetAndUpdate()) {
            scrollBlock.setRowOffset(handler.getDisplayedRowOffset());
        }
    }

    private void scrollUpListAndSyn() {
        if (handler.safeDecreaseDisplayedRowOffsetAndUpdate()) {
            scrollBlock.setRowOffset(handler.getDisplayedRowOffset());
        }
    }

    private boolean normalDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.getFocused() != null && this.isDragging() && pButton == 0) {
            return this.getFocused().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    private boolean scrollBlockDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY, ContainerScrollBlock scrollBlock) {
        boolean flag = scrollBlock.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        handler.setDisplayedRowOffsetAndUpdate(scrollBlock.getDisplayedRowOffset());
        return flag;
    }

    private void addScrollBlock() {
        scrollBlock = new ContainerScrollBlock(x + backgroundWidth + 1, y + 17, FiltPickClient.CLIENT_CONFIG.FILTLIST_DISPLAYED_ROW_COUNT.get() * 18, FiltPickClient.CLIENT_CONFIG.FILTLIST_DISPLAYED_ROW_COUNT.get(), FiltPick.SERVER_CONFIG.CONTAINER_ROW_COUNT.get());
        this.addDrawableChild(scrollBlock);
    }

    private void initCoordinates() {
        this.backgroundHeight = 114 + FiltPickClient.CLIENT_CONFIG.FILTLIST_DISPLAYED_ROW_COUNT.get() * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
        this.titleX = 72;
    }

    private void addButtons() {
        addFiltModeButton();
        addDestructionButton();
        addClearButton();
        addReturnButton();
    }

    private void addFiltModeButton() {
        filtModeButton = new FPToggleButton(
                this.x + 10 + FiltPickClient.CLIENT_CONFIG.buttonOffsets.get(FiltPickClientConfig.ButtonName.FILT_MODE_BUTTON).horizontalOffset().get(),
                this.y + 4 + FiltPickClient.CLIENT_CONFIG.buttonOffsets.get(FiltPickClientConfig.ButtonName.FILT_MODE_BUTTON).verticalOffset().get(),
                12,
                11,
                FILT_MODE_BUTTON_TEXTURE,
                WHITELIST_MODE_BUTTON_ID
        );
        filtModeButton.setTooltips(Text.translatable("whitelist_mode").append("\n").formatted(Formatting.DARK_GREEN).append(Text.translatable("whitelist_mode_explanation").fillStyle(EXPLANATION_STYLE)), Text.translatable("blacklist_mode").append("\n").formatted(Formatting.DARK_RED).append(Text.translatable("blacklist_mode_explanation").fillStyle(EXPLANATION_STYLE)));
        addDrawableChild(filtModeButton);
    }

    private void addDestructionButton() {
        destructionButton = new FPToggleButton(
                this.x + 10 + 2 + 12 + FiltPickClient.CLIENT_CONFIG.buttonOffsets.get(FiltPickClientConfig.ButtonName.DESTRUCTION_MODE_BUTTON).horizontalOffset().get(),
                this.y + 4 + FiltPickClient.CLIENT_CONFIG.buttonOffsets.get(FiltPickClientConfig.ButtonName.DESTRUCTION_MODE_BUTTON).verticalOffset().get(),
                12,
                11,
                DESTRUCTION_BUTTON_TEXTURE,
                DESTRUCTION_MODE_BUTTON_ID
        );
        destructionButton.setTooltips(Text.translatable("destruction_mode_on").formatted(Formatting.DARK_RED).append("\n").append(Text.translatable("destruction_mode_on_explanation").fillStyle(EXPLANATION_STYLE)), Text.translatable("destruction_mode_off").formatted(Formatting.DARK_GRAY));
        addDrawableChild(destructionButton);
    }

    private void addClearButton() {
        clearButton = new LegacyTexturedButtonWidget(
                this.x + 154 - 14 + FiltPickClient.CLIENT_CONFIG.buttonOffsets.get(FiltPickClientConfig.ButtonName.CLEAR_BUTTON).horizontalOffset().get(),
                this.y + 4 + FiltPickClient.CLIENT_CONFIG.buttonOffsets.get(FiltPickClientConfig.ButtonName.CLEAR_BUTTON).verticalOffset().get(),
                12,
                11,
                0,
                0,
                12,
                CLEAR_BUTTON_TEXTURE,
                button -> sendButtonClickC2SPacket(CLEAR_BUTTON_ID)
        );
        setTooltip2ClearButton();
        addDrawableChild(clearButton);
    }

    private void setTooltip2ClearButton() {
        clearButton.setTooltip(Tooltip.of(Text.translatable("reset_explanation").fillStyle(EXPLANATION_STYLE)));
        clearButton.setTooltipDelay(Duration.ofMillis(500));
    }

    private void addReturnButton() {
        returnButton = new LegacyTexturedButtonWidget(
                this.x + 154 + FiltPickClient.CLIENT_CONFIG.buttonOffsets.get(FiltPickClientConfig.ButtonName.RETURN_BUTTON).horizontalOffset().get(),
                this.y + 4 + FiltPickClient.CLIENT_CONFIG.buttonOffsets.get(FiltPickClientConfig.ButtonName.RETURN_BUTTON).verticalOffset().get(),
                12,
                11,
                0,
                0,
                12,
                RETURN_BUTTON_TEXTURE,
                12,
                11 * 2 + 1,
                button -> {
                    this.close();
                    client.setScreen(new InventoryScreen(client.player));
                }
        );
        addDrawableChild(returnButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderTitle(context, textRenderer, Text.translatable("filtpick_screen_name"), 72, y + 4, width - 72, y + 14, 0x404040);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        renderFiltPickContainer(context);
        renderInventory(context);
    }

    private void renderFiltPickContainer(DrawContext context) {
        context.drawTexture(CONTAINER_BACKGROUND, x, y, 0, 0, backgroundWidth, FiltPickClient.CLIENT_CONFIG.FILTLIST_DISPLAYED_ROW_COUNT.get() * 18 + 17);
    }

    private void renderInventory(DrawContext context) {
        context.drawTexture(CONTAINER_BACKGROUND, x, y + FiltPickClient.CLIENT_CONFIG.FILTLIST_DISPLAYED_ROW_COUNT.get() * 18 + 17, 0, 126, backgroundWidth, 96);
    }

    protected void renderTitle(DrawContext context, TextRenderer textRenderer, Text text, int startX, int startY, int endX, int endY, int color) {
        int centerX = (startX + endX) / 2;
        int i = textRenderer.getWidth(text);
        int j = (startY + endY - textRenderer.fontHeight) / 2 + 1;
        int k = endX - startX;
        if (i > k) {
            int l = i - k;
            double d = (double) Util.getMeasuringTimeMs() / 1000.0;
            double e = Math.max((double) l * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(Math.PI * 2 * d / e)) / 2.0 + 0.5;
            double g = MathHelper.lerp(f, 0.0, l);
            context.enableScissor(startX, startY, endX, endY);
            context.drawText(textRenderer, text, startX - (int) g, j, color, false);
            context.disableScissor();
        } else {
            int l = MathHelper.clamp(centerX, startX + i / 2, endX - i / 2);
            OrderedText orderedText = text.asOrderedText();
            context.drawText(textRenderer, orderedText, centerX - textRenderer.getWidth(orderedText) / 2, j, color, false);
        }
    }

    private void sendButtonClickC2SPacket(int buttonId) {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new ButtonClickC2SPacket(handler.syncId, buttonId));
    }

    private class FPToggleButton extends ClickableWidget {

        private final PropertyDelegate propertyDelegate = handler.getPropertyDelegate();
        private final int buttonId;
        private final Identifier texture;
        private final TooltipState tureTooltip = new TooltipState();
        private final TooltipState falseTooltip = new TooltipState();

        public FPToggleButton(int x, int y, int width, int height, Identifier texture, int buttonId) {
            this(x, y, width, height, Text.empty(), texture, buttonId);
        }

        public FPToggleButton(int x, int y, int width, int height, Text message, Identifier texture, int buttonId) {
            super(x, y, width, height, message);
            this.texture = texture;
            this.buttonId = buttonId;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            if (!visible) {
                return;
            }
            renderTexture(context);
            renderTooltip();
        }

        private void renderTooltip() {
            if (isCorrespondPropertyTrue() && tureTooltip != null) {
                tureTooltip.render(isHovered(), isFocused(), getNavigationFocus());
            }
            if (!isCorrespondPropertyTrue() && falseTooltip != null) {
                falseTooltip.render(isHovered(), isFocused(), getNavigationFocus());
            }
        }


        private void renderTexture(DrawContext context) {
            int u = 0, v = 0;
            v = setVerticalOffset(v);
            u = setHorizontalOffset(u);
            context.drawTexture(texture, this.getX(), this.getY(), u, v, width, height, 2 * width + 1, 2 * height + 1);
        }

        private int setHorizontalOffset(int u) {
            if (!isCorrespondPropertyTrue()) u += width + 1;
            return u;
        }

        private int setVerticalOffset(int v) {
            if (hovered) v += height + 1;
            return v;
        }

        private boolean isCorrespondPropertyTrue() {
            return IntBoolConvertor.toBool(propertyDelegate.get(buttonId));
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
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
            if (this.visible && this.hovered) {
                sendButtonClickC2SPacket(buttonId);
                return true;
            }
            return false;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            sendButtonClickC2SPacket(buttonId);
        }

        public void setTooltips(Text tureTooltipText, Text falseTooltipText) {
            tureTooltip.setTooltip(Tooltip.of(tureTooltipText));
            falseTooltip.setTooltip(Tooltip.of(falseTooltipText));
        }
    }

}
