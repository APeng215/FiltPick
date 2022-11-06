package com.apeng.filtpick.guis.custom;


import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class FiltPickScreen extends HandledScreen<FiltPickScreenHandler> {
    static TexturedButtonWidget whiteModeButton,blackModeButton;
    public static boolean filtPickIsWhiteListMode = false;
    private static final Identifier FILTPICK_RETURN_BUTTON_TEXTURE = new Identifier("filtpick","gui/filtpick_return_button.png");
    //A path to the gui texture. In this example we use the texture from the dispenser
    private static final Identifier FILTPICK_SCREEN_TEXTURE = new Identifier("filtpick", "gui/filtpick_screen.png");

    private static final Identifier WHITELIST_BUTTON_TEXTURE = new Identifier("filtpick", "gui/filtpick_whitelist_button.png");

    private static final Identifier BLACKLIST_BUTTON_TEXTURE = new Identifier("filtpick", "gui/filtpick_blacklist_button.png");

    public FiltPickScreen(FiltPickScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, FILTPICK_SCREEN_TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        setTitleCoordination();
        addChilds();
    }

    private void addChilds() {

        initModeButton();

        this.addDrawableChild(createReturnButton());
        //this.addDrawableChild(createModeSwitchWidget("mode"));

    }

    private void initModeButton() {
        whiteModeButton = new TexturedButtonWidget(this.x + 10, this.y + 4, 12, 11, 0, 0, 12, WHITELIST_BUTTON_TEXTURE, 256, 256, button -> {
            filtPickIsWhiteListMode = !filtPickIsWhiteListMode;//Switch
            sendC2SPacketToSetWhiteMode(false);
            this.remove(whiteModeButton);
            this.addDrawableChild(blackModeButton);
        }, (button, matrices, mouseX, mouseY) -> FiltPickScreen.this.renderTooltip(matrices,Text.translatable("whitelist_mode_explanation"),whiteModeButton.x, whiteModeButton.y),Text.of("whitelist_mode_explanation"));
        blackModeButton = new TexturedButtonWidget(this.x + 10, this.y + 4, 12, 11, 0, 0, 12, BLACKLIST_BUTTON_TEXTURE,256,256,button -> {
            filtPickIsWhiteListMode = !filtPickIsWhiteListMode;//Switch
            sendC2SPacketToSetWhiteMode(true);
            this.remove(blackModeButton);
            this.addDrawableChild(whiteModeButton);
        }, (button, matrices, mouseX, mouseY) -> FiltPickScreen.this.renderTooltip(matrices,Text.translatable("blacklist_mode_explanation"),whiteModeButton.x, whiteModeButton.y),Text.of("blacklist_mode_explanation"));
        if(filtPickIsWhiteListMode){
            this.addDrawableChild(whiteModeButton);
        }
        else {
            this.addDrawableChild(blackModeButton);
        }
    }

    @NotNull
    private TexturedButtonWidget createReturnButton() {
        return new TexturedButtonWidget(this.x + 154, this.y + 4, 12, 11, 0, 0, 12, FILTPICK_RETURN_BUTTON_TEXTURE, button
                -> {
            if (client != null && client.player != null) {
                client.setScreen(new InventoryScreen(client.player));
            }
        });
    }

    private void setTitleCoordination() {
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }



    private static void sendC2SPacketToSetWhiteMode(boolean bool) {
        PacketByteBuf filtUpdataBuf = new PacketByteBuf(PacketByteBufs.create().writeBoolean(bool));
        ClientPlayNetworking.send(new Identifier("update_filtpick_mode"),filtUpdataBuf);
    }




}
