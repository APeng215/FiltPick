package com.apeng.filtpick.guis.screen;
import com.apeng.filtpick.FiltPick;
import com.apeng.filtpick.guis.widget.LegacyTexturedButtonWidget;
import com.apeng.filtpick.util.IntBoolConvertor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class FiltPickScreen extends HandledScreen<FiltPickScreenHandler> {

    public static final int WHITELIST_MODE_BUTTON_ID = 0;
    public static final int DESTRUCTION_MODE_BUTTON_ID = 1;
    public static final int CLEAR_BUTTON_ID = 2;

    private static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/container/shulker_box.png");
    private static final Identifier FILT_MODE_BUTTON_TEXTURE = Identifier.of(FiltPick.ID, "gui/filtmode_button.png");
    private static final Identifier DESTRUCTION_BUTTON_TEXTURE = Identifier.of(FiltPick.ID, "gui/destruction_button.png");
    private static final Identifier CLEAR_BUTTON_TEXTURE = Identifier.of(FiltPick.ID, "gui/clearlist_button.png");
    private static final Identifier RETURN_BUTTON_TEXTURE = Identifier.of(FiltPick.ID, "gui/return_button.png");

    private FPToggleButton filtModeButton, destructionButton;
    private LegacyTexturedButtonWidget clearButton, returnButton;

    public FiltPickScreen(FiltPickScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        setTitlePosition();
        addButtons();
    }

    private void addButtons() {
        addFiltModeButton();
        addDestructionButton();
        addClearButton();
        addReturnButton();
    }

    private void addFiltModeButton() {
        filtModeButton = new FPToggleButton(this.x + 10, this.y + 4, 12, 11, FILT_MODE_BUTTON_TEXTURE, WHITELIST_MODE_BUTTON_ID);
        filtModeButton.setTooltips(Text.translatable("whitelist_mode").append("\n").formatted(Formatting.DARK_GREEN).append(Text.translatable("whitelist_mode_explanation").formatted(Formatting.DARK_GRAY)), Text.translatable("blacklist_mode").append("\n").formatted(Formatting.DARK_RED).append(Text.translatable("blacklist_mode_explanation").formatted(Formatting.DARK_GRAY)));
        addDrawableChild(filtModeButton);
    }

    private void addDestructionButton() {
        destructionButton = new FPToggleButton(this.x + 10 + 2 + 12, this.y + 4, 12, 11, DESTRUCTION_BUTTON_TEXTURE, DESTRUCTION_MODE_BUTTON_ID);
        destructionButton.setTooltips(Text.translatable("destruction_mode_on").formatted(Formatting.DARK_RED), Text.translatable("destruction_mode_off").formatted(Formatting.DARK_GRAY));
        addDrawableChild(destructionButton);
    }

    private void addClearButton() {
        clearButton = new LegacyTexturedButtonWidget(this.x + 154 - 14, this.y + 4, 12, 11, 0, 0, 12, CLEAR_BUTTON_TEXTURE, button -> {
            sendButtonClickC2SPacket(CLEAR_BUTTON_ID);
        });
        addDrawableChild(clearButton);
    }

    private void addReturnButton() {
        returnButton = new LegacyTexturedButtonWidget(this.x + 154, this.y + 4, 12, 11, 0, 0, 12, RETURN_BUTTON_TEXTURE, 12, 11 * 2 + 1,button -> {
            client.setScreen(new InventoryScreen(client.player));
        });
        addDrawableChild(returnButton);

    }

    private void setTitlePosition() {
        this.titleX = 72;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(BACKGROUND_TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    private void sendButtonClickC2SPacket(int buttonId) {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new ButtonClickC2SPacket(handler.syncId, buttonId));
    }

    class FPToggleButton extends ClickableWidget {
        private final PropertyDelegate propertyDelegate = handler.getPropertyDelegate();
        private final int buttonId;
        private final Identifier texture;
        private Tooltip tureTooltip, falseTooltip;

        public FPToggleButton(int x, int y, int width, int height, Identifier texture, int buttonId) {
            this(x, y, width, height, Text.empty(), texture, buttonId);
        }

        public FPToggleButton(int x, int y, int width, int height, Text message, Identifier texture, int buttonId) {
            super(x, y, width, height, message);
            this.texture = texture;
            this.buttonId = buttonId;
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            if (!visible) {
                return;
            }
            renderTexture(context);
            applyTooltip();
        }

        private void applyTooltip() {
            Screen screen;
            boolean bl;
            if (this.tureTooltip == null || this.falseTooltip == null) {
                return;
            }
            boolean bl2 = bl = this.hovered || this.isFocused() && MinecraftClient.getInstance().getNavigationType().isKeyboard();
            if (bl != this.wasHovered) {
                if (bl) {
                    this.lastHoveredTime = Util.getMeasuringTimeMs();
                }
                this.wasHovered = bl;
            }
            if (bl && Util.getMeasuringTimeMs() - this.lastHoveredTime > (long)this.tooltipDelay && (screen = MinecraftClient.getInstance().currentScreen) != null) {
                screen.setTooltip(correspondPropertyTrue() ? this.tureTooltip : this.falseTooltip, this.getTooltipPositioner(), this.isFocused());
            }
        }


        private void renderTexture(DrawContext context) {
            int u = 0, v = 0;
            v = setVerticalOffset(v);
            u = setHorizontalOffset(u);
            context.drawTexture(texture, this.getX(), this.getY(), u, v, width, height, 2 * width + 1, 2 * height + 1);
        }

        private int setHorizontalOffset(int u) {
            if (!correspondPropertyTrue()) u += width + 1;
            return u;
        }

        private int setVerticalOffset(int v) {
            if (hovered) v += height + 1;
            return v;
        }

        private boolean correspondPropertyTrue() {
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

        public void setTooltips(Text tureTooltip, Text falseTooltip) {
            this.tureTooltip = Tooltip.of(tureTooltip);
            this.falseTooltip = Tooltip.of(falseTooltip);
        }
    }


}
