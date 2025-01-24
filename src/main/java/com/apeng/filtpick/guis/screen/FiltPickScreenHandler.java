package com.apeng.filtpick.guis.screen;

import com.apeng.filtpick.FiltPick;
import com.apeng.filtpick.FiltPickClient;
import com.apeng.filtpick.mixinduck.ServerPlayerEntityDuck;
import com.apeng.filtpick.network.OpenFiltPickScreenC2SPacket;
import com.apeng.filtpick.network.SynMenuFieldC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Set;

public class FiltPickScreenHandler extends ScreenHandler {

    private final PropertyDelegate propertyDelegate;
    private PlayerInventory playerInventory;
    private Inventory filtList;
    private int displayedRowOffset = 0;
    private final int MAX_DISPLAYED_ROW_OFFSET;

    // For client side
    public FiltPickScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(FiltPick.SERVER_CONFIG.CONTAINER_ROW_COUNT.get() * 9), new ArrayPropertyDelegate(2));
    }
            
    // For server side        
    public FiltPickScreenHandler(int syncId, PlayerInventory playerInventory, Inventory filtList, PropertyDelegate propertyDelegate) {
        super(FiltPick.FILTPICK_SCREEN_HANDLER_TYPE, syncId);
        this.propertyDelegate = propertyDelegate;
        this.playerInventory = playerInventory;
        this.filtList = filtList;
        this.MAX_DISPLAYED_ROW_OFFSET = Math.max(0, getActualRowNum() - FiltPick.SERVER_CONFIG.FILTLIST_DISPLAYED_ROW_COUNT.get());
        addAllSlots(playerInventory, filtList);
        addProperties(propertyDelegate);
    }

    private int getActualRowNum() {
        return (int) Math.ceil(filtList.size() / 9.0);
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

    private void addFiltList(Inventory filtList) {
        for (int row = 0; row < FiltPick.SERVER_CONFIG.FILTLIST_DISPLAYED_ROW_COUNT.get(); row++) {
            for (int col = 0; col < 9; col++) {
                int index = row * 9 + col + displayedRowOffset * 9;
                if (index >= filtList.size()) {
                    return;
                }
                this.addSlot(new Slot(filtList, index, 8 + col * 18, 18 + row * 18));
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
        int filtSlotIndex = slotIndex - 36 + 9 * displayedRowOffset;
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

    /**
     * Safe means the row offset will never be out of the bound.
     * @return whether displayedRowOffset has been changed
     */
    public boolean safeIncreaseDisplayedRowOffsetAndUpdate() {
        int oldDisplayedRowOffset = displayedRowOffset;
        safeIncreaseDisplayedRowOffset();
        synDisplayedRowOffsetWithServer();
        updateSlots();
        return oldDisplayedRowOffset != displayedRowOffset;
    }

    private void updateSlots() {
        clearAllSlots();
        addAllSlots(playerInventory, filtList);
    }

    private void addAllSlots(Inventory playerInventory, Inventory filtList) {
        int pixelOffset = (FiltPick.SERVER_CONFIG.FILTLIST_DISPLAYED_ROW_COUNT.get() - 4) * 18;
        addHotBarSlots(playerInventory, pixelOffset);
        addInventorySlot(playerInventory, pixelOffset);
        // FiltList must be added at last for #inventorySlotClicked working properly.
        addFiltList(filtList);
    }

    private void addHotBarSlots(Inventory playerInventory, int pixelOffset) {
        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + pixelOffset));
        }
    }

    private void addInventorySlot(Inventory playerInventory, int pixelOffset) {
        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + pixelOffset));
            }
        }
    }

    private void clearAllSlots() {
        this.slots.clear();
        this.trackedStacks.clear();
        this.previousTrackedStacks.clear();
    }

    private void safeIncreaseDisplayedRowOffset() {
        displayedRowOffset = Math.min(displayedRowOffset + 1, MAX_DISPLAYED_ROW_OFFSET);
    }

    /**
     * Will only be executed on the client side.
     * @return SynMenuFieldC2SPacket has been sent
     */
    private boolean synDisplayedRowOffsetWithServer() {
        if(isClientSide()) {
            ClientPlayNetworking.send(new SynMenuFieldC2SPacket(displayedRowOffset));
            return true;
        }
        return false;
    }

    private boolean isClientSide() {
        return !(this.playerInventory.player instanceof ServerPlayerEntity);
    }

    public int getDisplayedRowOffset() {
        return displayedRowOffset;
    }

    /**
     * Safe means the row offset will never be out of the bound.
     * @return whether displayedRowOffset has been changed
     */
    public boolean safeDecreaseDisplayedRowOffsetAndUpdate() {
        int oldDisplayedRowOffset = displayedRowOffset;
        safeDecreaseDisplayedRowOffset();
        synDisplayedRowOffsetWithServer();
        updateSlots();
        return oldDisplayedRowOffset != displayedRowOffset;
    }

    private void safeDecreaseDisplayedRowOffset() {
        displayedRowOffset = Math.max(displayedRowOffset - 1, 0);
    }

    /**
     * Be careful of that it is possible that the offset be out of the bound.
     * @param displayedRowOffset
     */
    public void setDisplayedRowOffsetAndUpdate(int displayedRowOffset) {
        this.displayedRowOffset = displayedRowOffset;
        synDisplayedRowOffsetWithServer();
        updateSlots();
    }
}
