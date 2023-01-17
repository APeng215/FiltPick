package com.apeng.filtpick.guis.custom;

import com.apeng.filtpick.NetWorkingIDs;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FiltPickScreen extends CottonInventoryScreen<FiltPickGuiDescription> {
    static TexturedButtonWidget whiteModeButton,blackModeButton;
    static TexturedButtonWidget destructionModeOnButton,destructionModeOffButton;
    public static boolean filtPickIsWhiteListMode = false;
    public static boolean filtPickIsDestructionMode = false;
    private final List<Text> tooltipOfWhiteMode = new ArrayList<>();
    private final List<Text> tooltipOfBlackMode = new ArrayList<>();
    private final List<Text> tooltipOfDestructionOn = new ArrayList<>();
    private final List<Text> tooltipOfDestructionOff = new ArrayList<>();
    private final List<Text> tooltipOfReset = new ArrayList<>();
    private static final Identifier FILTPICK_RETURN_BUTTON_TEXTURE = new Identifier("filtpick","gui/filtpick_return_button.png");
    private static final Identifier WHITELIST_BUTTON_TEXTURE = new Identifier("filtpick", "gui/filtpick_whitelist_button.png");

    private static final Identifier BLACKLIST_BUTTON_TEXTURE = new Identifier("filtpick", "gui/filtpick_blacklist_button.png");
    private static final Identifier DESTRUCTION_ON_BUTTON_TEXTURE = new Identifier("filtpick","gui/filtpick_destruction_on_button.png");
    private static final Identifier DESTRUCTION_OFF_BUTTON_TEXTURE = new Identifier("filtpick","gui/filtpick_destruction_off_button.png");
    private static final Identifier CLEAR_LIST_BUTTON_TEXTURE = new Identifier("filtpick","gui/filtpick_clearlist_button.png");
    public FiltPickScreen(FiltPickGuiDescription description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
    public void init() {
        super.init();
        addChilds();
    }
    private void addChilds() {
        initModeButton();
        initDestructionModeButton();
        this.addDrawableChild(createReturnButton());
        this.addDrawableChild(createResetButton());
    }
    private void initDestructionModeButton() {
        destructionModeOnButton = new TexturedButtonWidget(this.x + 10 + 2 + 12, this.y + 4, 12, 11, 0, 0, 12, DESTRUCTION_ON_BUTTON_TEXTURE, 256, 256, button -> {
            filtPickIsDestructionMode = !filtPickIsDestructionMode;//Switch
            sendC2SPacketToSetDestructionMode(false);
            this.remove(destructionModeOnButton);
            this.addDrawableChild(destructionModeOffButton);
        }, (button, matrices, mouseX, mouseY) -> this.renderTooltip(matrices, tooltipOfDestructionOn, destructionModeOnButton.x, destructionModeOnButton.y),Text.of("destruction_mode_on_explanation"));
        destructionModeOffButton = new TexturedButtonWidget(this.x + 10 + 2 + 12, this.y + 4, 12, 11, 0, 0, 12, DESTRUCTION_OFF_BUTTON_TEXTURE,256,256,button -> {
            filtPickIsDestructionMode = !filtPickIsDestructionMode;//Switch
            sendC2SPacketToSetDestructionMode(true);
            this.remove(destructionModeOffButton);
            this.addDrawableChild(destructionModeOnButton);
        }, (button, matrices, mouseX, mouseY) -> this.renderTooltip(matrices, tooltipOfDestructionOff, destructionModeOffButton.x, destructionModeOffButton.y),Text.of("destruction_mode_off_explanation"));
        if(filtPickIsDestructionMode){
            this.addDrawableChild(destructionModeOnButton);
        }
        else {
            this.addDrawableChild(destructionModeOffButton);
        }
    }
    private void initModeButton() {
        initTooltipsOfModeButton();
        initTooltipsOfDestructionButton();
        whiteModeButton = new TexturedButtonWidget(this.x + 10, this.y + 4, 12, 11, 0, 0, 12, WHITELIST_BUTTON_TEXTURE, 256, 256, button -> {
            filtPickIsWhiteListMode = !filtPickIsWhiteListMode;//Switch
            sendC2SPacketToSetWhiteMode(false);
            this.remove(whiteModeButton);
            this.addDrawableChild(blackModeButton);
        }, (button, matrices, mouseX, mouseY) -> this.renderTooltip(matrices,tooltipOfWhiteMode,whiteModeButton.x, whiteModeButton.y),Text.of("whitelist_mode_explanation"));
        blackModeButton = new TexturedButtonWidget(this.x + 10, this.y + 4, 12, 11, 0, 0, 12, BLACKLIST_BUTTON_TEXTURE,256,256,button -> {
            filtPickIsWhiteListMode = !filtPickIsWhiteListMode;//Switch
            sendC2SPacketToSetWhiteMode(true);
            this.remove(blackModeButton);
            this.addDrawableChild(whiteModeButton);
        }, (button, matrices, mouseX, mouseY) -> this.renderTooltip(matrices,tooltipOfBlackMode,blackModeButton.x, blackModeButton.y),Text.of("blacklist_mode_explanation"));
        if(filtPickIsWhiteListMode){
            this.addDrawableChild(whiteModeButton);
        }
        else {
            this.addDrawableChild(blackModeButton);
        }
    }
    private void initTooltipsOfModeButton(){
        tooltipOfWhiteMode.add(Text.translatable("whitelist_mode").formatted(Formatting.GREEN));
        tooltipOfWhiteMode.add(Text.translatable("whitelist_mode_explanation").formatted(Formatting.DARK_GRAY,Formatting.ITALIC));
        tooltipOfBlackMode.add(Text.translatable("blacklist_mode").formatted(Formatting.RED));
        tooltipOfBlackMode.add(Text.translatable("blacklist_mode_explanation").formatted(Formatting.DARK_GRAY,Formatting.ITALIC));
    }
    private void initTooltipsOfDestructionButton(){
        tooltipOfDestructionOff.add(Text.translatable("destruction_mode_off").formatted(Formatting.GRAY));
        tooltipOfDestructionOn.add(Text.translatable("destruction_mode_on").formatted(Formatting.RED));
        tooltipOfDestructionOn.add(Text.translatable("destruction_mode_on_explanation").formatted(Formatting.DARK_GRAY,Formatting.ITALIC));
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
    private TexturedButtonWidget createResetButton() {
        //Init tooltip
        tooltipOfReset.add(Text.translatable("reset_explanation").formatted(Formatting.DARK_GRAY,Formatting.ITALIC));
        //Create reset button
        return new TexturedButtonWidget(this.x + 154 - 14, this.y + 4, 12, 11, 0, 0, 12, CLEAR_LIST_BUTTON_TEXTURE,256, 256, button
                -> {
            //Change pick-mode
            if(filtPickIsWhiteListMode){
                filtPickIsWhiteListMode = false;
                sendC2SPacketToSetWhiteMode(false);
                this.remove(whiteModeButton);
                this.addDrawableChild(blackModeButton);
            }
            //Change destruction mode
            if(filtPickIsDestructionMode){
                filtPickIsDestructionMode = false;
                sendC2SPacketToSetDestructionMode(false);
                this.remove(destructionModeOnButton);
                this.addDrawableChild(destructionModeOffButton);
            }
            //Clear filtpick inventory
            ClientPlayNetworking.send(NetWorkingIDs.CLEAR_LIST_C2S, PacketByteBufs.empty());
        },(button, matrices, mouseX, mouseY) -> this.renderTooltip(matrices,tooltipOfReset,button.x, button.y),Text.of("reset_explanation")
        );
    }

    private void sendC2SPacketToSetWhiteMode(boolean bool) {
        PacketByteBuf filtUpdataBuf = new PacketByteBuf(PacketByteBufs.create().writeBoolean(bool));
        ClientPlayNetworking.send(new Identifier("update_filtpick_mode"),filtUpdataBuf);
    }
    private void sendC2SPacketToSetDestructionMode(boolean bool){
        PacketByteBuf filtUpdataBuf = new PacketByteBuf(PacketByteBufs.create().writeBoolean(bool));
        ClientPlayNetworking.send(new Identifier("update_filtpick_destruction_mode"),filtUpdataBuf);
    }
}
