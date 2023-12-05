package com.apeng.filtpick.guis.custom;

import com.apeng.filtpick.FiltPick;
import com.apeng.filtpick.NetWorkingIDs;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
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
    private static final ButtonTextures BLACKLIST_BUTTON_TEXTURE = new ButtonTextures(Identifier.of(FiltPick.NAME_SPACE, "gui/filtpick_blacklist_button.png"), Identifier.of(FiltPick.NAME_SPACE, "gui/filtpick_blacklist_button_highlighted.png"));
    private static final ButtonTextures CLEARLIST_BUTTON_TEXTURE = new ButtonTextures(Identifier.of(FiltPick.NAME_SPACE, "gui/filtpick_clearlist_button.png"), Identifier.of(FiltPick.NAME_SPACE, "gui/filtpick_clearlist_button_highlighted.png"));
    private static final ButtonTextures DESTRUCTION_OFF_BUTTON_TEXTURE = new ButtonTextures(Identifier.of(FiltPick.NAME_SPACE, "gui/filtpick_destruction_off_button.png"), Identifier.of(FiltPick.NAME_SPACE, "gui/filtpick_destruction_off_button_highlighted.png"));
    private static final ButtonTextures DESTRUCTION_ON_BUTTON_TEXTURE = new ButtonTextures(Identifier.of(FiltPick.NAME_SPACE, "gui/filtpick_destruction_on_button.png"), Identifier.of(FiltPick.NAME_SPACE, "gui/filtpick_destruction_on_button_highlighted.png"));
    private static final ButtonTextures RETURN_BUTTON_TEXTURE = new ButtonTextures(Identifier.of(FiltPick.NAME_SPACE, "gui/filtpick_return_button.png"), Identifier.of(FiltPick.NAME_SPACE, "gui/filtpick_return_button_highlighted.png"));
    private static final ButtonTextures WHITELIST_BUTTON_TEXTURE = new ButtonTextures(Identifier.of(FiltPick.NAME_SPACE, "gui/filtpick_whitelist_button.png"), Identifier.of(FiltPick.NAME_SPACE, "gui/filtpick_whitelist_button_highlighted.png"));
    public static boolean filtPickIsWhiteListMode = false;
    public static boolean filtPickIsDestructionMode = false;
    static TexturedButtonWidget whiteModeButton, blackModeButton;
    static TexturedButtonWidget destructionModeOnButton, destructionModeOffButton;
    private final List<Text> tooltipOfWhiteMode = new ArrayList<>();
    private final List<Text> tooltipOfBlackMode = new ArrayList<>();
    private final List<Text> tooltipOfDestructionOn = new ArrayList<>();
    private final List<Text> tooltipOfDestructionOff = new ArrayList<>();
    private final List<Text> tooltipOfReset = new ArrayList<>();

    public FiltPickScreen(FiltPickGuiDescription description, PlayerEntity player, Text title) {
        super(description, player, title);
    }

    public void init() {
        super.init();
        addChildren();
    }

    private void addChildren() {
        initModeButton();
        initDestructionModeButton();
        this.addDrawableChild(createReturnButton());
        this.addDrawableChild(createResetButton());
    }

    private void initDestructionModeButton() {
        //Create button
        destructionModeOnButton = new TexturedButtonWidget(this.x + 10 + 2 + 12, this.y + 4, 12, 11, DESTRUCTION_ON_BUTTON_TEXTURE, button -> {
            filtPickIsDestructionMode = !filtPickIsDestructionMode;//Switch
            sendC2SPacketToSetDestructionMode(false);
            this.remove(destructionModeOnButton);
            this.addDrawableChild(destructionModeOffButton);
        }, Text.of("destruction_mode_on_explanation"));
        //Set tooltip
        destructionModeOnButton.setTooltip(Tooltip.of(Text.translatable("destruction_mode_on").append("\n").formatted(Formatting.DARK_RED).append(Text.translatable("destruction_mode_on_explanation").formatted(Formatting.DARK_GRAY))));
        destructionModeOnButton.setTooltipDelay(0);
        //Create button
        destructionModeOffButton = new TexturedButtonWidget(this.x + 10 + 2 + 12, this.y + 4, 12, 11, DESTRUCTION_OFF_BUTTON_TEXTURE, button -> {
            filtPickIsDestructionMode = !filtPickIsDestructionMode;//Switch
            sendC2SPacketToSetDestructionMode(true);
            this.remove(destructionModeOffButton);
            this.addDrawableChild(destructionModeOnButton);
        }, Text.of("destruction_mode_off_explanation"));
        //Set tooltip
        destructionModeOffButton.setTooltip(Tooltip.of(Text.translatable("destruction_mode_off").formatted(Formatting.DARK_GRAY)));
        destructionModeOffButton.setTooltipDelay(0);

        if (filtPickIsDestructionMode) {
            this.addDrawableChild(destructionModeOnButton);
        } else {
            this.addDrawableChild(destructionModeOffButton);
        }
    }

    private void initModeButton() {
        initTooltipsOfModeButton();
        initTooltipsOfDestructionButton();
        //Create button
        whiteModeButton = new TexturedButtonWidget(this.x + 10, this.y + 4, 12, 11, WHITELIST_BUTTON_TEXTURE, button -> {
            filtPickIsWhiteListMode = !filtPickIsWhiteListMode;//Switch
            sendC2SPacketToSetWhiteMode(false);
            this.remove(whiteModeButton);
            this.addDrawableChild(blackModeButton);
        }, Text.of("whitelist_mode_explanation"));
        //Set tooltip
        whiteModeButton.setTooltip(Tooltip.of(Text.translatable("whitelist_mode").append("\n").formatted(Formatting.DARK_GREEN).append(Text.translatable("whitelist_mode_explanation").formatted(Formatting.DARK_GRAY))));
        whiteModeButton.setTooltipDelay(0);
        //Create button
        blackModeButton = new TexturedButtonWidget(this.x + 10, this.y + 4, 12, 11, BLACKLIST_BUTTON_TEXTURE, button -> {
            filtPickIsWhiteListMode = !filtPickIsWhiteListMode;//Switch
            sendC2SPacketToSetWhiteMode(true);
            this.remove(blackModeButton);
            this.addDrawableChild(whiteModeButton);
        }, Text.of("blacklist_mode_explanation"));
        //Set tooltip
        blackModeButton.setTooltip(Tooltip.of(Text.translatable("blacklist_mode").append("\n").formatted(Formatting.DARK_RED).append(Text.translatable("blacklist_mode_explanation").formatted(Formatting.DARK_GRAY))));
        blackModeButton.setTooltipDelay(0);

        if (filtPickIsWhiteListMode) {
            this.addDrawableChild(whiteModeButton);
        } else {
            this.addDrawableChild(blackModeButton);
        }
    }

    private void initTooltipsOfModeButton() {
        tooltipOfWhiteMode.add(Text.translatable("whitelist_mode").formatted(Formatting.GREEN));
        tooltipOfWhiteMode.add(Text.translatable("whitelist_mode_explanation").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        tooltipOfBlackMode.add(Text.translatable("blacklist_mode").formatted(Formatting.RED));
        tooltipOfBlackMode.add(Text.translatable("blacklist_mode_explanation").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
    }

    private void initTooltipsOfDestructionButton() {
        tooltipOfDestructionOff.add(Text.translatable("destruction_mode_off").formatted(Formatting.GRAY));
        tooltipOfDestructionOn.add(Text.translatable("destruction_mode_on").formatted(Formatting.RED));
        tooltipOfDestructionOn.add(Text.translatable("destruction_mode_on_explanation").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
    }

    @NotNull
    private TexturedButtonWidget createReturnButton() {
        return new TexturedButtonWidget(this.x + 154, this.y + 4, 12, 11, RETURN_BUTTON_TEXTURE, button
                -> {
            if (client != null && client.player != null) {
                client.setScreen(new InventoryScreen(client.player));
            }
        });
    }

    private TexturedButtonWidget createResetButton() {
        //Init tooltip
        tooltipOfReset.add(Text.translatable("reset_explanation").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        //Create reset button
        return new TexturedButtonWidget(this.x + 154 - 14, this.y + 4, 12, 11, CLEARLIST_BUTTON_TEXTURE, button
                -> {
            //Change pick-mode
            if (filtPickIsWhiteListMode) {
                filtPickIsWhiteListMode = false;
                sendC2SPacketToSetWhiteMode(false);
                this.remove(whiteModeButton);
                this.addDrawableChild(blackModeButton);
            }
            //Change destruction mode
            if (filtPickIsDestructionMode) {
                filtPickIsDestructionMode = false;
                sendC2SPacketToSetDestructionMode(false);
                this.remove(destructionModeOnButton);
                this.addDrawableChild(destructionModeOffButton);
            }
            //Clear filtpick inventory
            ClientPlayNetworking.send(NetWorkingIDs.CLEAR_LIST_C2S, PacketByteBufs.empty());
        }, Text.of("reset_explanation")
        );
    }

    private void sendC2SPacketToSetWhiteMode(boolean bool) {
        PacketByteBuf filtUpdataBuf = new PacketByteBuf(PacketByteBufs.create().writeBoolean(bool));
        ClientPlayNetworking.send(new Identifier("update_filtpick_mode"), filtUpdataBuf);
    }

    private void sendC2SPacketToSetDestructionMode(boolean bool) {
        PacketByteBuf filtUpdataBuf = new PacketByteBuf(PacketByteBufs.create().writeBoolean(bool));
        ClientPlayNetworking.send(new Identifier("update_filtpick_destruction_mode"), filtUpdataBuf);
    }
}
