package com.apeng.filtpick;

import com.apeng.filtpick.config.FiltPickServerConfig;
import com.apeng.filtpick.guis.screen.FiltPickScreenHandler;
import com.apeng.filtpick.network.OpenFiltPickScreenC2SPacket;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class FiltPick implements ModInitializer {

    public static final String ID = "filtpick";
    public static FiltPickServerConfig SERVER_CONFIG;

    public void onInitialize() {
        registerScreenHandler();
        registerServerReceiver();
        registerServerConfig();
    }

    private static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(OpenFiltPickScreenC2SPacket.PACKET_ID, new OpenFiltPickScreenC2SPacket());
    }

    private static void registerScreenHandler() {
        Registry.register(Registries.SCREEN_HANDLER, ID, FiltPickScreenHandler.FILTPICK_SCREEN_HANDLER_TYPE);
    }

    private static void registerServerConfig() {
        ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();
        SERVER_CONFIG = FiltPickServerConfig.getInstance(serverBuilder); // Create singleton instance
        ForgeConfigRegistry.INSTANCE.register(FiltPick.ID, ModConfig.Type.SERVER, serverBuilder.build());
    }


}
