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
    public ForgeConfigSpec.IntValue FILTLIST_DISPLAYED_ROW_COUNT;

    // Private constructor to prevent external instantiation
    private FiltPickClientConfig(ForgeConfigSpec.Builder builder) {
        buildButtonsSection(builder);
        buildScreenSection(builder);
    }

    private void buildScreenSection(ForgeConfigSpec.Builder builder) {
        builder.comment("FiltPick screen configuration").push("filtscreen");

        FILTLIST_DISPLAYED_ROW_COUNT = builder.comment("The number of slot rows displayed in filtpick screen.",
                "Smaller number means you need to scroll more to reach the bottom.")
                .defineInRange("displayed row count", 6, 1, 6);

        builder.pop();
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
    public record ButtonOffset(ForgeConfigSpec.ConfigValue<Integer> horizontalOffset, ForgeConfigSpec.ConfigValue<Integer> verticalOffset) {
    }
}
