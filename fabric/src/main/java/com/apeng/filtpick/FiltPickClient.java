package com.apeng.filtpick;

import com.apeng.filtpick.config.FiltPickClientConfig;
import com.apeng.filtpick.gui.screen.FiltPickScreen;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

@Environment(EnvType.CLIENT)
public class FiltPickClient implements ClientModInitializer {

    public static FiltPickClientConfig CLIENT_CONFIG;

    @Override
    public void onInitializeClient() {
        registerHandlerScreen();
        registerClientConfig();
    }

    private static void registerClientConfig() {
        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
        CLIENT_CONFIG = FiltPickClientConfig.getInstance(clientBuilder); // Create singleton instance
        ForgeConfigRegistry.INSTANCE.register(FiltPick.ID, ModConfig.Type.CLIENT, clientBuilder.build());
    }

    private static void registerHandlerScreen() {
        MenuScreens.register(FiltPick.FILTPICK_SCREEN_HANDLER_TYPE, FiltPickScreen::new);
    }


}
