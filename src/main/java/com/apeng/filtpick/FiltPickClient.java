package com.apeng.filtpick;

import com.apeng.filtpick.guis.custom.FiltPickScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FiltPickClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(FiltPick.FilePick_SCREEN_HANDLER, FiltPickScreen::new);
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("syn_listmode"),(client, handler, buf, responseSender) -> client.execute(()-> FiltPickScreen.filtPickIsWhiteListMode=buf.readBoolean()));
    }
}
