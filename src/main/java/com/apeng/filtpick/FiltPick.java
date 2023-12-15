package com.apeng.filtpick;

import com.apeng.filtpick.guis.screen.FiltPickScreenHandler;
import com.apeng.filtpick.network.OpenFiltPickScreenC2SPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class FiltPick implements ModInitializer {
    public static final String ID = "filtpick";

    public void onInitialize() {
        registerScreenHandler();
        registerServerReceiver();
    }

    private static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(OpenFiltPickScreenC2SPacket.TYPE, new OpenFiltPickScreenC2SPacket());
    }

    private static void registerScreenHandler() {
        Registry.register(Registries.SCREEN_HANDLER, ID, FiltPickScreenHandler.FILTPICK_SCREEN_HANDLER_TYPE);
    }


}
