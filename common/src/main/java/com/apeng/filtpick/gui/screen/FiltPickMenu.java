package com.apeng.filtpick.gui.screen;

import com.apeng.filtpick.Common;
import com.apeng.filtpick.mixinduck.FiltListContainer;
import com.apeng.filtpick.network.SynMenuFieldC2SPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class FiltPickMenu extends AbstractContainerMenu {

//    Register sentences for neoforge
//    public static final DeferredRegister<MenuType<?>> MENU_TYPE_REGISTER = DeferredRegister.create(Registries.MENU, FiltPick.ID);
//    public static final Supplier<MenuType<FiltPickMenu>> MENU_TYPE_SUPPLIER = MENU_TYPE_REGISTER.register("filtlist", () -> new MenuType<>(FiltPickMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private final ContainerData propertyDelegate;
    private final Inventory playerInventory;
    private final Container filtList;
    private int displayedRowOffset = 0;
    private final int MAX_DISPLAYED_ROW_OFFSET;

    private int getActualRowNum() {
        return (int) Math.ceil(filtList.getContainerSize() / 9.0);
    }

    // For client side
    public FiltPickMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(Common.getServerConfig().CONTAINER_ROW_COUNT.get() * 9), new SimpleContainerData(2));
    }

    // For server side
    public FiltPickMenu(int syncId, Inventory playerInventory, Container filtList, ContainerData propertyDelegate) {
        super(Common.getFiltpickMenuType(), syncId);
        this.propertyDelegate = propertyDelegate;
        this.playerInventory = playerInventory;
        this.filtList = filtList;
        this.MAX_DISPLAYED_ROW_OFFSET = Math.max(0, getActualRowNum() - Common.getServerConfig().FILTLIST_DISPLAYED_ROW_COUNT.get());
        addAllSlots(playerInventory, filtList);
        addDataSlots(propertyDelegate);
    }

    /**
     * Inventory slots is added to the menu at first. See {@link #addAllSlots} for more details.
     *
     * @param indexOfMenu
     * @return whether this slot of the menu belongs to inventory.
     */
    private static boolean inventorySlotClicked(int indexOfMenu) {
        return indexOfMenu < 36;
    }

    private void addAllSlots(Inventory playerInventory, Container filtList) {
        int pixelOffset = (Common.getServerConfig().FILTLIST_DISPLAYED_ROW_COUNT.get() - 4) * 18;
        addHotBarSlots(playerInventory, pixelOffset);
        addInventorySlot(playerInventory, pixelOffset);
        // FiltList must be added at last for #inventorySlotClicked working properly.
        addFiltList(filtList);
    }

    private void addInventorySlot(Inventory playerInventory, int pixelOffset) {
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + pixelOffset));
            }
        }
    }

    private void addHotBarSlots(Inventory playerInventory, int pixelOffset) {
        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + pixelOffset));
        }
    }

    /**
     * This is executed on the server as a response to clients sending a {@link ServerboundContainerButtonClickPacket}.
     *
     * @param serverPlayer
     * @param buttonId
     * @return return true to notify client screen handler to update state
     */
    @Override
    public boolean clickMenuButton(Player serverPlayer, int buttonId) {
        switch (buttonId) {
            case FiltPickScreen.WHITELIST_MODE_BUTTON_ID, FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID ->
                    ((FiltListContainer) serverPlayer).getFiltListPropertyDelegate().switchState(buttonId);
            case FiltPickScreen.CLEAR_BUTTON_ID -> ((FiltListContainer) serverPlayer).resetFiltListWithProperties();
        }
        return true;
    }

    public ContainerData getPropertyDelegate() {
        return propertyDelegate;
    }

    private void addFiltList(Container filtList) {
        for (int row = 0; row < Common.getServerConfig().FILTLIST_DISPLAYED_ROW_COUNT.get(); row++) {
            for (int col = 0; col < 9; col++) {
                int index = row * 9 + col + displayedRowOffset * 9;
                if (index >= filtList.getContainerSize()) {
                    return;
                }
                this.addSlot(new Slot(filtList, index, 8 + col * 18, 18 + row * 18));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack stack2Move = playerInventory.getItem(index);
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

    private static boolean isInventorySlotClicked(int slotIndex) {
        return slotIndex < 36;
    }

    private void tryAddItem2FiltList(ItemStack stack2Move) {
        if (isFiltListAlreadyContainItem(stack2Move)) return;
        addItem2FiltList(stack2Move);
    }

    private boolean isFiltListAlreadyContainItem(ItemStack stack2Move) {
        return filtList.hasAnyOf(Set.of(stack2Move.getItem()));
    }

    private void addItem2FiltList(ItemStack stack2Move) {
        ItemStack singleItemStack2Add = stack2Move.getItem().getDefaultInstance();
        for (int i = 0; i < filtList.getContainerSize(); i++) {
            ItemStack targetStack = filtList.getItem(i);
            if (targetStack.isEmpty()) {
                filtList.setItem(i, singleItemStack2Add);
                markSlotDirty(i + 36);
                return;
            }
        }
    }

    /**
     * Performs a slot click. This can behave in many different ways depending mainly on the action type.
     * Logic comes from Create Mod.
     *
     * @param indexOfMenu
     * @param button
     * @param actionType  the type of slot click, check the docs for each {@link ClickType} value for details
     * @param player
     */
    @Override
    public void clicked(int indexOfMenu, int button, ClickType actionType, Player player) {
        if (inventorySlotClicked(indexOfMenu)) {
            super.clicked(indexOfMenu, button, actionType, player);
        } else {
            onFiltSlotClicked(indexOfMenu, actionType);
        }
    }

    private void onFiltSlotClicked(int slotIndex, ClickType actionType) {
        int filtSlotIndex = slotIndex - 36 + 9 * displayedRowOffset;
        switch (actionType) {
            case THROW, QUICK_MOVE -> setFiltStackEmpty(filtSlotIndex);
            case PICKUP, QUICK_CRAFT -> setFiltStackCursorItem(filtSlotIndex);
        }
        markSlotDirty(slotIndex);
    }

    private void setFiltStackCursorItem(int filtSlotIndex) {
        filtList.setItem(filtSlotIndex, getCarried().getItem().getDefaultInstance());
    }

    private void setFiltStackEmpty(int filtSlotIndex) {
        filtList.setItem(filtSlotIndex, ItemStack.EMPTY);
    }

    private void markSlotDirty(int slotIndex) {
        getSlot(slotIndex).setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /**
     * @param cursorStack
     * @param pickedSlot
     * @return whether the slot should be extracted when double-click
     */
    @Override
    public boolean canTakeItemForPickAll(ItemStack cursorStack, Slot pickedSlot) {
        return pickedSlot.container == playerInventory;
    }

    public int getDisplayedRowOffset() {
        return displayedRowOffset;
    }

    /**
     * Safe means the row offset will never be out of the bound.
     *
     * @return whether displayedRowOffset has been changed
     */
    public boolean safeIncreaseDisplayedRowOffsetAndUpdate() {
        int oldDisplayedRowOffset = displayedRowOffset;
        safeIncreaseDisplayedRowOffset();
        synDisplayedRowOffsetWithServer();
        updateSlots();
        return oldDisplayedRowOffset != displayedRowOffset;
    }

    private void safeIncreaseDisplayedRowOffset() {
        displayedRowOffset = Math.min(displayedRowOffset + 1, MAX_DISPLAYED_ROW_OFFSET);
    }

    /**
     * Safe means the row offset will never be out of the bound.
     *
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

    private void updateSlots() {
        clearAllSlots();
        addAllSlots(playerInventory, filtList);
    }

    private void clearAllSlots() {
        this.slots.clear();
        this.lastSlots.clear();
        this.remoteSlots.clear();
    }

    /**
     * Be careful of that it is possible that the offset be out of the bound.
     *
     * @param displayedRowOffset
     */
    public void setDisplayedRowOffsetAndUpdate(int displayedRowOffset) {
        this.displayedRowOffset = displayedRowOffset;
        synDisplayedRowOffsetWithServer();
        updateSlots();
    }

    /**
     * Will only be executed on the client side.
     *
     * @return SynMenuFieldC2SPacket has been sent
     */
    private boolean synDisplayedRowOffsetWithServer() {
        if (isClientSide()) {
            Common.getNetworkHandler().sendToServer(new SynMenuFieldC2SPacket(displayedRowOffset));
            return true;
        }
        return false;
    }

    private boolean isClientSide() {
        return !(this.playerInventory.player instanceof ServerPlayer);
    }

}
