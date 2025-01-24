package com.apeng.filtpick;

import com.apeng.filtpick.config.FiltPickServerConfig;
import com.apeng.filtpick.guis.screen.FiltPickScreenHandler;
import com.apeng.filtpick.network.OpenFiltPickScreenC2SPacket;
import com.apeng.filtpick.network.SynMenuFieldC2SPacket;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class FiltPick implements ModInitializer {

    public static final String ID = "filtpick";
    // SCREEN_HANDLER_TYPE MUST be put in mod main entry (ModInitializer)
    public static final ScreenHandlerType<FiltPickScreenHandler> FILTPICK_SCREEN_HANDLER_TYPE = Registry.register(
            Registries.SCREEN_HANDLER,
            new Identifier(FiltPick.ID, "filtpick_screen"),
            new ScreenHandlerType<>(FiltPickScreenHandler::new, FeatureSet.empty())
    );
    public static FiltPickServerConfig SERVER_CONFIG;

    @Override
    public void onInitialize() {
        registerServerReceiver();
        registerServerConfig();
    }

    private static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(OpenFiltPickScreenC2SPacket.PACKET_ID, new OpenFiltPickScreenC2SPacket());
        ServerPlayNetworking.registerGlobalReceiver(SynMenuFieldC2SPacket.PACKET_ID, new SynMenuFieldC2SPacket(-1));
    }

    private static void registerServerConfig() {
        ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();
        SERVER_CONFIG = FiltPickServerConfig.getInstance(serverBuilder); // Create singleton instance
        ForgeConfigRegistry.INSTANCE.register(FiltPick.ID, ModConfig.Type.SERVER, serverBuilder.build());
    }


}
