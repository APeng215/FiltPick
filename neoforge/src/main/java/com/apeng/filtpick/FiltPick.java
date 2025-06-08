package com.apeng.filtpick;

import com.apeng.filtpick.config.FiltPickClientConfig;
import com.apeng.filtpick.config.FiltPickServerConfig;
import com.apeng.filtpick.gui.screen.FiltPickMenu;
import com.apeng.filtpick.gui.screen.FiltPickScreen;
import com.mojang.logging.LogUtils;
import fuzs.forgeconfigapiport.neoforge.api.forge.v4.ForgeConfigRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Common.MOD_ID)
public class FiltPick {

    public static final String ID = Common.MOD_ID;
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<MenuType<?>> MENU_TYPE_REGISTER = DeferredRegister.create(Registries.MENU, FiltPick.ID);
    public static final Supplier<MenuType<FiltPickMenu>> MENU_TYPE_SUPPLIER = MENU_TYPE_REGISTER.register("filtlist", () -> new MenuType<>(FiltPickMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public FiltPick(IEventBus modBus) {
        registerMenuType(modBus);
        modBus.addListener(FiltPick::registerScreen);
        modBus.addListener(NetworkHandlerNeoImpl::registerAll);
        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();
        initCommon(clientBuilder, serverBuilder);
        registerConfigBuilders(clientBuilder, serverBuilder);
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

    private static void registerConfigBuilders(ForgeConfigSpec.Builder clientBuilder, ForgeConfigSpec.Builder serverBuilder) {
        registerClientConfigBuilder(clientBuilder);
        registerServerConfigBuilder(serverBuilder);
    }

    private static void registerServerConfigBuilder(ForgeConfigSpec.Builder serverBuilder) {
        ForgeConfigRegistry.INSTANCE.register(ModConfig.Type.SERVER, serverBuilder.build());
    }

    private static void registerClientConfigBuilder(ForgeConfigSpec.Builder clientBuilder) {
        ForgeConfigRegistry.INSTANCE.register(ModConfig.Type.CLIENT, clientBuilder.build());
    }

    private record ConfigBuilders(ForgeConfigSpec.Builder clientBuilder, ForgeConfigSpec.Builder serverBuilder) {
    }

    private static void registerMenuType(IEventBus modBus) {
        MENU_TYPE_REGISTER.register(modBus);
    }

    private static void registerScreen(RegisterMenuScreensEvent event) {
        event.register(MENU_TYPE_SUPPLIER.get(), FiltPickScreen::new);
    }

}
