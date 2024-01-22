package com.apeng.filtpick;

import com.apeng.filtpick.config.FPConfigManager;
import com.apeng.filtpick.guis.screen.FiltPickScreen;
import com.apeng.filtpick.guis.screen.FiltPickScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public class FiltPickClient implements ClientModInitializer {

    public static final FPConfigManager CONFIG_MANAGER = FPConfigManager.getInstance(FabricLoader.getInstance().getConfigDir());

    @Override
    public void onInitializeClient() {
        registerHandlerScreen();
    }

    private static void registerHandlerScreen() {
        HandledScreens.register(FiltPickScreenHandler.FILTPICK_SCREEN_HANDLER_TYPE, FiltPickScreen::new);
    }

}
