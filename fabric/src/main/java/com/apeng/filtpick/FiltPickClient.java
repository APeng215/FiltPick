package com.apeng.filtpick;

import com.apeng.filtpick.config.FiltPickClientConfig;
import com.apeng.filtpick.gui.screen.FiltPickScreen;
import com.apeng.filtpick.hud.BlockedItemsHud;
import com.apeng.filtpick.keybinding.ScreenKeyBindings;
import com.apeng.filtpick.network.SyncBlockedItemsS2CPacket;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.Identifier;
import net.minecraftforge.common.ForgeConfigSpec;
import net.neoforged.fml.config.ModConfig;

@Environment(EnvType.CLIENT)
public class FiltPickClient implements ClientModInitializer {

    public static FiltPickClientConfig CLIENT_CONFIG;

    @Override
    public void onInitializeClient() {
        registerConfigs();
        registerScreens();
        registerHudElements();
        registerNetworkHandlers();
        registerClientEvents();
        registerScreenKeybindings();
    }

    private static void registerHudElements() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(Common.MOD_ID, "blocked_items_hud"),
                BlockedItemsHud::render
        );
    }

    private static void registerNetworkHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(SyncBlockedItemsS2CPacket.TYPE, (payload, context) -> {
            context.client().execute(() -> SyncBlockedItemsS2CPacket.handle(payload));
        });
    }

    private static void registerClientEvents() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> BlockedItemsHud.clear());
    }

    private static void registerScreenKeybindings() {
        // Register all key mappings once
        for (ScreenKeyBindings keyBinding : ScreenKeyBindings.values()) {
            KeyBindingHelper.registerKeyBinding(keyBinding.keyMapping);
        }

        // Register a single BEFORE_INIT listener and dispatch to all ScreenKeyBindings
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            for (ScreenKeyBindings keyBinding : ScreenKeyBindings.values()) {
                if (keyBinding.targetScreenClass.isInstance(screen)) {
                    KeyMapping keyMapping = keyBinding.keyMapping;
                    ScreenKeyboardEvents.afterKeyPress(screen).register((screen1, context) -> {
                        if (keyMapping.matches(context)) {
                            keyBinding.action.accept(screen1);
                        }
                    });
                }
            }
        });
    }

    private static void registerConfigs() {
        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
        CLIENT_CONFIG = FiltPickClientConfig.getInstance(clientBuilder);
        ConfigRegistry.INSTANCE.register(FiltPick.ID, ModConfig.Type.CLIENT, clientBuilder.build());
    }

    private static void registerScreens() {
        MenuScreens.register(FiltPick.FILTPICK_SCREEN_HANDLER_TYPE, FiltPickScreen::new);
    }


}
