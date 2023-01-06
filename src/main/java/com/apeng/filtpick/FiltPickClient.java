package com.apeng.filtpick;

import com.apeng.filtpick.guis.custom.FiltPickGuiDescription;
import com.apeng.filtpick.guis.custom.FiltPickScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public class FiltPickClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.<FiltPickGuiDescription, FiltPickScreen>register(FiltPick.FILTPICK_SCREEN_HANDLER_TYPE, (gui, inventory, title) -> new FiltPickScreen(gui, inventory.player, title));
        ClientPlayNetworking.registerGlobalReceiver(NetWorkingIDs.SYN_LISTMODE_S2C,(client, handler, buf, responseSender) -> client.execute(()-> FiltPickScreen.filtPickIsWhiteListMode=buf.readBoolean()));
        ClientPlayNetworking.registerGlobalReceiver(NetWorkingIDs.SYN_DESTRUCTION_MODE_S2C,(client, handler, buf, responseSender) -> client.execute(()-> FiltPickScreen.filtPickIsDestructionMode = buf.readBoolean()));
    }
}
