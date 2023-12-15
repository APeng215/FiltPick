package com.apeng.filtpick.guis.screen;

import com.apeng.filtpick.mixinduck.ServerPlayerEntityDuck;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.NotNull;

public class FiltPickScreenHandler extends ScreenHandler {

    public static final ExtendedScreenHandlerType<FiltPickScreenHandler> FILTPICK_SCREEN_HANDLER_TYPE = new ExtendedScreenHandlerType<>(((syncId, playerInventory, buf) -> new FiltPickScreenHandler(syncId, playerInventory)));
    private final PropertyDelegate propertyDelegate;
    private PlayerInventory playerInventory;
    private Inventory filtList;

    // For client side
    public FiltPickScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(27), new ArrayPropertyDelegate(2));
    }
            
    // For server side        
    public FiltPickScreenHandler(int syncId, PlayerInventory playerInventory, Inventory filtList ,PropertyDelegate propertyDelegate) {
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
        // FiltList must be added at last
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

    @Override
    public ItemStack quickMove(PlayerEntity playerIn, int index) {
        if (index < 36) {
            ItemStack stackToInsert = playerInventory.getStack(index);
            for (int i = 0; i < filtList.size(); i++) {
                ItemStack stack = filtList.getStack(i);
                if (!allowRepeats() && canItemStacksStack(stack, stackToInsert))
                    break;
                if (stack.isEmpty()) {
                    ItemStack copy = stackToInsert.copy();
                    copy.setCount(1);
                    filtList.setStack(i, copy);
                    getSlot(i + 36).markDirty();
                    break;
                }
            }
        } else {
            filtList.setStack(index - 36, ItemStack.EMPTY);
            getSlot(index).markDirty();
        }
        return ItemStack.EMPTY;
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
        if (slotIndex == playerInventory.selectedSlot && actionType != SlotActionType.THROW)
            return;

        if (slotIndex < 36) {
            super.onSlotClick(slotIndex, button, actionType, player);
            return;
        }
        if (actionType == SlotActionType.THROW)
            return;

        ItemStack held = getCursorStack();
        int slot = slotIndex - 36;
        if (actionType == SlotActionType.CLONE) {
            if (player.isCreative() && held.isEmpty()) {
                ItemStack stackInSlot = filtList.getStack(slot)
                        .copy();
                stackInSlot.setCount(stackInSlot.getMaxCount());
                setCursorStack(stackInSlot);
                return;
            }
            return;
        }

        ItemStack insert;
        if (held.isEmpty()) {
            insert = ItemStack.EMPTY;
        } else {
            insert = held.copy();
            insert.setCount(1);
        }
        filtList.setStack(slot, insert);
        getSlot(slotIndex).markDirty();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    protected boolean allowRepeats() {
        return false;
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

    public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b)
    {
        if (a.isEmpty() || !ItemStack.areItemsEqual(a, b) || a.hasNbt() != b.hasNbt())
            return false;

        return (!a.hasNbt() || a.getNbt().equals(b.getNbt()));
    }

}
