package com.apeng.filtpick;

import com.apeng.filtpick.guis.custom.FiltPickScreen;
import com.apeng.filtpick.packet.PacketID;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

@Environment(EnvType.CLIENT)
public class FiltPickClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(FiltPick.FilePick_SCREEN_HANDLER, FiltPickScreen::new);
        ClientPlayNetworking.registerGlobalReceiver(PacketID.S2C.SYN_LISTMODE,(client, handler, buf, responseSender) -> client.execute(()-> FiltPickScreen.filtPickIsWhiteListMode=buf.readBoolean()));
    }
}
