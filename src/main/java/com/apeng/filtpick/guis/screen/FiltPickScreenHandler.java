package com.apeng.filtpick.guis.screen;

import com.apeng.filtpick.FiltPick;
import com.apeng.filtpick.mixinduck.ServerPlayerEntityDuck;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

import java.util.Set;

public class FiltPickScreenHandler extends ScreenHandler {

    public static final ScreenHandlerType<FiltPickScreenHandler> FILTPICK_SCREEN_HANDLER_TYPE = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(FiltPick.ID, "filt_screen"),
            new ScreenHandlerType(FiltPickScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
    );
    private final PropertyDelegate propertyDelegate;
    private PlayerInventory playerInventory;
    private Inventory filtList;

    // For client side
    public FiltPickScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(27), new ArrayPropertyDelegate(2));
    }
            
    // For server side        
    public FiltPickScreenHandler(int syncId, PlayerInventory playerInventory, Inventory filtList, PropertyDelegate propertyDelegate) {
        super(FiltPickScreenHandler.FILTPICK_SCREEN_HANDLER_TYPE, syncId);
        this.propertyDelegate = propertyDelegate;
        this.playerInventory = playerInventory;
        this.filtList = filtList;
        checkSize(filtList, propertyDelegate);
        addSlots(playerInventory, filtList);
        addProperties(propertyDelegate);
    }

    private void addSlots(Inventory playerInventory, Inventory filtList) {
        addHotbarSlots(playerInventory);
        addInventorySlots(playerInventory);
        // FiltList must be added at last. Index begins from 36
        addFiltList(filtList);
    }

    private static void checkSize(Inventory filtList, PropertyDelegate propertyDelegate) {
        checkSize(filtList, 27);
        checkDataCount(propertyDelegate, 2);
    }

    /**
     * This is executed on the server as a response to clients sending a {@link ButtonClickC2SPacket}.
     * @param serverPlayer
     * @param buttonId
     * @return return true to notify client screen handler to update state
     */
    @Override
    public boolean onButtonClick(PlayerEntity serverPlayer, int buttonId) {
        switch (buttonId) {
            case FiltPickScreen.WHITELIST_MODE_BUTTON_ID, FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID -> ((ServerPlayerEntityDuck)serverPlayer).switchProperty(buttonId);
            case FiltPickScreen.CLEAR_BUTTON_ID -> ((ServerPlayerEntityDuck)serverPlayer).resetFiltList();
        }
        return true;
    }

    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }


    private void addHotbarSlots(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    private void addInventorySlots(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addFiltList(Inventory filtList) {
        // Add slots for data inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(filtList, i * 9 + j, 8 + j * 18, 18 + i * 18));
            }
        }
    }

    // Shift click
    @Override
    public ItemStack quickMove(PlayerEntity playerIn, int index) {
        ItemStack stack2Move = playerInventory.getStack(index);
        if (stack2Move.isEmpty()) return ItemStack.EMPTY;
        if (isInventorySlotClicked(index)) {
            tryAddItem2FiltList(stack2Move);
        } else {
            deleteItemFromFiltList(index);
        }
        return ItemStack.EMPTY; // To cancel infinite invoking
    }

    private void deleteItemFromFiltList(int index) {
        setFiltStackEmpty(index - 36);
        markSlotDirty(index);
    }

    private void tryAddItem2FiltList(ItemStack stack2Move) {
        if (isFiltListAlreadyContainItem(stack2Move)) return;
        addItem2FiltList(stack2Move);
    }

    private void addItem2FiltList(ItemStack stack2Move) {
        ItemStack singleItemStack2Add = stack2Move.getItem().getDefaultStack();
        for (int i = 0; i < filtList.size(); i++) {
            ItemStack targetStack = filtList.getStack(i);
            if (targetStack.isEmpty()) {
                filtList.setStack(i, singleItemStack2Add);
                markSlotDirty(i + 36);
                return;
            }
        }
    }

    private boolean isFiltListAlreadyContainItem(ItemStack stack2Move) {
        return filtList.containsAny(Set.of(stack2Move.getItem()));
    }

    /**
     * Performs a slot click. This can behave in many different ways depending mainly on the action type.
     * Logic comes from Create Mod.
     * @param slotIndex
     * @param button
     * @param actionType the type of slot click, check the docs for each {@link SlotActionType} value for details
     * @param player
     */
    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (isInventorySlotClicked(slotIndex)) {
            super.onSlotClick(slotIndex, button, actionType, player);
        } else {
            onFiltSlotClicked(slotIndex, actionType);
        }
    }

    private void onFiltSlotClicked(int slotIndex, SlotActionType actionType) {
        int filtSlotIndex = slotIndex - 36;
        switch (actionType) {
            case THROW, QUICK_MOVE -> setFiltStackEmpty(filtSlotIndex);
            case PICKUP, QUICK_CRAFT -> setFiltStackCursorItem(filtSlotIndex);
        }
        markSlotDirty(slotIndex);
    }

    private void setFiltStackCursorItem(int filtSlotIndex) {
        filtList.setStack(filtSlotIndex, getCursorStack().getItem().getDefaultStack());
    }

    private void setFiltStackEmpty(int filtSlotIndex) {
        filtList.setStack(filtSlotIndex, ItemStack.EMPTY);
    }

    private void markSlotDirty(int slotIndex) {
        getSlot(slotIndex).markDirty();
    }

    private static boolean isInventorySlotClicked(int slotIndex) {
        return slotIndex < 36;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    /**
     * @param cursorStack
     * @param pickedSlot
     * @return whether the slot should be extracted when double-click
     */
    @Override
    public boolean canInsertIntoSlot(ItemStack cursorStack, Slot pickedSlot) {
        return pickedSlot.inventory == playerInventory;
    }

}
