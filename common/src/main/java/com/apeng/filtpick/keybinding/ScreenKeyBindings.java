package com.apeng.filtpick.keybinding;

import com.apeng.filtpick.Common;
import com.apeng.filtpick.network.AddFilteredItemC2SPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;


public enum ScreenKeyBindings {
    ADD_FILTERED_ITEM(
            new KeyMapping(
                    "key.filtpick.add_filtered_item", // The translation key for the key mapping.
                    InputConstants.Type.KEYSYM, // // The type of the keybinding; KEYSYM for keyboard, MOUSE for mouse.
                    GLFW.GLFW_KEY_I, // The GLFW keycode of the key.
                    KeyBindManager.CATEGORY // The category of the mapping.
            ),
            AbstractContainerScreen.class,
            screen -> {
                if (screen instanceof AbstractContainerScreen currentScreen && currentScreen.hoveredSlot != null && !currentScreen.hoveredSlot.getItem().isEmpty()) {
                    Common.getNetworkHandler().sendToServer(new AddFilteredItemC2SPacket(currentScreen.hoveredSlot.getItem().getItem()));
                }
            });

    public final KeyMapping keyMapping;
    public final Class<? extends Screen> targetScreenClass;
    public final Consumer<Screen> action;

    /**
     * Constructor for ScreenKeyBindings enum.
     * @param keyMapping mapping
     * @param targetScreenClass target screen class
     * @param action the action to execute
     */
    ScreenKeyBindings(KeyMapping keyMapping, Class<? extends Screen> targetScreenClass, Consumer<Screen> action) {
        this.keyMapping = keyMapping;
        this.targetScreenClass = targetScreenClass;
        this.action = action;
    }


}

