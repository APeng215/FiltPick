package com.apeng.filtpick;

import com.apeng.filtpick.config.FiltPickServerConfig;
import com.apeng.filtpick.gui.screen.FiltPickMenu;
import com.apeng.filtpick.network.OpenFiltPickScreenC2SPacket;
import com.apeng.filtpick.network.SynMenuFieldC2SPacket;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class FiltPick implements ModInitializer {

    public static final String ID = "filtpick";
    // SCREEN_HANDLER_TYPE MUST be put in mod main entry (ModInitializer)
    public static final MenuType<FiltPickMenu> FILTPICK_SCREEN_HANDLER_TYPE = Registry.register(
            BuiltInRegistries.MENU,
            ResourceLocation.fromNamespaceAndPath(FiltPick.ID, "filtpick_screen"),
            new MenuType<>(FiltPickMenu::new, FeatureFlagSet.of())
    );
    public static FiltPickServerConfig SERVER_CONFIG;

    @Override
    public void onInitialize() {
        registerNetwork();
        registerServerConfig();
        Common.init(
                new NetworkHandlerFabricImpl(),
                () -> FILTPICK_SCREEN_HANDLER_TYPE,
                () -> FiltPickClient.CLIENT_CONFIG,
                () -> SERVER_CONFIG
        );
    }

    private static void registerNetwork() {
        PayloadTypeRegistry.playC2S().register(OpenFiltPickScreenC2SPacket.TYPE, OpenFiltPickScreenC2SPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(OpenFiltPickScreenC2SPacket.TYPE, (payload, context) -> {
            OpenFiltPickScreenC2SPacket.handle(payload, context.player());
        });

        PayloadTypeRegistry.playC2S().register(SynMenuFieldC2SPacket.TYPE, SynMenuFieldC2SPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SynMenuFieldC2SPacket.TYPE, (payload, context) -> {
            SynMenuFieldC2SPacket.handle(payload, context.player());
        });
    }

    private static void registerServerConfig() {
        ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();
        SERVER_CONFIG = FiltPickServerConfig.getInstance(serverBuilder); // Create singleton instance
        ForgeConfigRegistry.INSTANCE.register(FiltPick.ID, ModConfig.Type.SERVER, serverBuilder.build());
    }


}
