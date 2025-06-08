package com.apeng.filtpick;

import com.apeng.filtpick.config.FiltPickClientConfig;
import com.apeng.filtpick.config.FiltPickServerConfig;
import com.apeng.filtpick.gui.screen.FiltPickMenu;
import com.apeng.filtpick.network.NetworkHandler;
import net.minecraft.world.inventory.MenuType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class Common {

    public static final String MOD_NAME = "FiltPick";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final String MOD_ID = "filtpick";

    // Remember to initialize values below in mod loader specific implementation
    private static NetworkHandler NETWORK_HANDLER;
    private static Supplier<MenuType<FiltPickMenu>> FILTPICK_MENU_TYPE_SUPPLIER;
    private static Supplier<FiltPickClientConfig> CLIENT_CONFIG_SUPPLIER;
    private static Supplier<FiltPickServerConfig> SERVER_CONFIG_SUPPLIER;

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This apeng has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init(NetworkHandler networkHandlerImpl, Supplier<MenuType<FiltPickMenu>> filtPickMenuMenuTypeSuppler, Supplier<FiltPickClientConfig> clientConfigSupplier, Supplier<FiltPickServerConfig> serverConfigSupplier) {
        NETWORK_HANDLER = networkHandlerImpl;
        FILTPICK_MENU_TYPE_SUPPLIER = filtPickMenuMenuTypeSuppler;
        CLIENT_CONFIG_SUPPLIER = clientConfigSupplier;
        SERVER_CONFIG_SUPPLIER = serverConfigSupplier;
    }

    public static NetworkHandler getNetworkHandler() {
        return NETWORK_HANDLER;
    }

    public static MenuType<FiltPickMenu> getFiltpickMenuType() {
        return FILTPICK_MENU_TYPE_SUPPLIER.get();
    }

    public static FiltPickClientConfig getClientConfig() {
        return CLIENT_CONFIG_SUPPLIER.get();
    }

    public static FiltPickServerConfig getServerConfig() {
        return SERVER_CONFIG_SUPPLIER.get();
    }
}
