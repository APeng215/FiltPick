package com.apeng.filtpick;

import com.apeng.filtpick.config.FiltPickClientConfig;
import com.apeng.filtpick.config.FiltPickServerConfig;
import com.apeng.filtpick.gui.screen.FiltPickMenu;
import com.apeng.filtpick.gui.screen.FiltPickScreen;
import com.apeng.filtpick.hud.BlockedItemsHud;
import com.apeng.filtpick.test.TestFunctionCollector;
import com.apeng.filtpick.test.TestFunctions;
import com.apeng.filtpick.tracker.BlockedItemsTracker;
import fuzs.forgeconfigapiport.neoforge.api.v5.ForgeConfigRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Common.MOD_ID)
public class FiltPick {

    public static final String ID = Common.MOD_ID;

    public static final DeferredRegister<MenuType<?>> MENU_TYPE_REGISTER = DeferredRegister.create(Registries.MENU, ID);
    public static final Supplier<MenuType<FiltPickMenu>> MENU_TYPE_SUPPLIER = MENU_TYPE_REGISTER.register("filtlist", () -> new MenuType<>(FiltPickMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public FiltPick(IEventBus modBus) {
        registerMenuType(modBus);
        modBus.addListener(FiltPick::registerTestFunctionsEvent);
        modBus.addListener(FiltPick::registerScreen);
        modBus.addListener(NetworkHandlerNeoImpl::registerAll);
        modBus.addListener(FiltPick::registerRenderGuiLayerEvent);

        NeoForge.EVENT_BUS.addListener(FiltPick::onPlayerLoggedOut);
        NeoForge.EVENT_BUS.addListener(FiltPick::onClientLogout);

        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();
        initCommon(clientBuilder, serverBuilder);
        registerConfigBuilders(clientBuilder, serverBuilder);
    }

    private static void onClientLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity().level().isClientSide()) {
            BlockedItemsHud.clear();
        }
    }

    private static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            BlockedItemsTracker.onPlayerLogout(serverPlayer);
        }
    }

    private static void initCommon(ForgeConfigSpec.Builder clientBuilder, ForgeConfigSpec.Builder serverBuilder) {
        // Init the configs immediately
        FiltPickClientConfig clientConfig = FiltPickClientConfig.getInstance(clientBuilder);
        FiltPickServerConfig serverConfig = FiltPickServerConfig.getInstance(serverBuilder);

        Common.init(
                new NetworkHandlerNeoImpl(),
                MENU_TYPE_SUPPLIER,
                () -> clientConfig,
                () -> serverConfig
        );
    }

    private static void registerTestFunctionsEvent(RegisterEvent event) {
        registerAllTestFunctions(event, TestFunctions.class);
    }

    private static void registerRenderGuiLayerEvent(RegisterGuiLayersEvent event) {
        event.registerAboveAll(Identifier.fromNamespaceAndPath(Common.MOD_ID, "blocked_items_hud"), BlockedItemsHud::render);
    }

    private static void registerAllTestFunctions(RegisterEvent event, Class<?> testClass) {
        for (TestFunctionCollector.TestFunctionEntry entry :
                TestFunctionCollector.collect(ID, testClass)) {

            event.register(
                    Registries.TEST_FUNCTION,
                    entry.resourceLocation(),
                    entry::function
            );
        }
    }


    private static void registerConfigBuilders(ForgeConfigSpec.Builder clientBuilder, ForgeConfigSpec.Builder serverBuilder) {
        registerClientConfigBuilder(clientBuilder);
        registerServerConfigBuilder(serverBuilder);
    }

    private static void registerServerConfigBuilder(ForgeConfigSpec.Builder serverBuilder) {
        ForgeConfigRegistry.INSTANCE.register(ID,ModConfig.Type.SERVER, serverBuilder.build());
    }

    private static void registerClientConfigBuilder(ForgeConfigSpec.Builder clientBuilder) {
        ForgeConfigRegistry.INSTANCE.register(ID ,ModConfig.Type.CLIENT, clientBuilder.build());
    }

    private static void registerMenuType(IEventBus modBus) {
        MENU_TYPE_REGISTER.register(modBus);
    }

    private static void registerScreen(RegisterMenuScreensEvent event) {
        event.register(MENU_TYPE_SUPPLIER.get(), FiltPickScreen::new);
    }

}
