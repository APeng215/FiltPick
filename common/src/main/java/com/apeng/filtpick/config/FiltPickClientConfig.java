package com.apeng.filtpick.config;


import net.minecraftforge.common.ForgeConfigSpec;

import java.util.EnumMap;

/**
 * A singleton
 */
public class FiltPickClientConfig {

    // Private static instance of the class (Singleton pattern)
    private static FiltPickClientConfig instance;

    public final EnumMap<ButtonName, ButtonOffset> buttonOffsets = new EnumMap<>(ButtonName.class);


    // Private constructor to prevent external instantiation
    private FiltPickClientConfig(ForgeConfigSpec.Builder builder) {
        buildButtonsSection(builder);
    }


    private void buildButtonsSection(ForgeConfigSpec.Builder builder) {
        builder.comment("Button offset configuration").push("buttons");

        for (ButtonName key : ButtonName.values()) {
            buttonOffsets.put(key, defineButtonOffset(builder, key));
        }

        builder.pop();
    }

    // Public static method to get the singleton instance
    public static FiltPickClientConfig getInstance(ForgeConfigSpec.Builder builder) {
        if (instance == null) {
            instance = new FiltPickClientConfig(builder);
        }
        return instance;
    }

    // Define a button offset with horizontal and vertical values
    private ButtonOffset defineButtonOffset(ForgeConfigSpec.Builder builder, ButtonName key) {
        builder.push(key.name());

        ForgeConfigSpec.ConfigValue<Integer> horizontalOffset = builder
                .define("horizontal offset", 0);

        ForgeConfigSpec.ConfigValue<Integer> verticalOffset = builder
                .define("vertical offset", 0);

        builder.pop();

        return new ButtonOffset(horizontalOffset, verticalOffset);
    }

    // Enum to define button names
    public enum ButtonName {
        RECIPE_BUTTON,
        ENTRY_BUTTON,
        FILT_MODE_BUTTON,
        DESTRUCTION_MODE_BUTTON,
        CLEAR_BUTTON,
        RETURN_BUTTON
    }

    // Record class to store horizontal and vertical offsets
    public record ButtonOffset(ForgeConfigSpec.ConfigValue<Integer> horizontalOffset,
                               ForgeConfigSpec.ConfigValue<Integer> verticalOffset) {
    }
}
