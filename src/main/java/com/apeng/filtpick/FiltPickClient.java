package com.apeng.filtpick;

import com.apeng.filtpick.guis.screen.FiltPickScreen;
import com.apeng.filtpick.guis.screen.FiltPickScreenHandler;
import com.apeng.filtpick.util.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public class FiltPickClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        loadConfig();
        registerHandlerScreen();
    }

    private static void loadConfig() {
        ClientLifecycleEvents.CLIENT_STARTED.register(Config::tryLoadConfigFile);
    }

    private static void registerHandlerScreen() {
        HandledScreens.register(FiltPickScreenHandler.FILTPICK_SCREEN_HANDLER_TYPE, FiltPickScreen::new);
    }


}
