package com.apeng.filtpick.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * A singleton
 */
public class FiltPickServerConfig {

    // Private static instance of the class (Singleton pattern)
    private static FiltPickServerConfig instance;

    // Configuration
    public final ForgeConfigSpec.IntValue CONTAINER_ROW_COUNT;

    // Private constructor to prevent external instantiation
    private FiltPickServerConfig(ForgeConfigSpec.Builder builder) {
        CONTAINER_ROW_COUNT = builder.comment("The actual(not displayed) row count the filtpick list every player has."
                , "The world must be restarted before the config value can be changed.")
                .worldRestart().defineInRange("container row count", 20 , 0, 100);
    }

    // Public static method to get the singleton instance
    public static FiltPickServerConfig getInstance(ForgeConfigSpec.Builder builder) {
        if (instance == null) {
            instance = new FiltPickServerConfig(builder);
        }
        return instance;
    }
}
