package com.apeng.filtpick.mixin;


import com.apeng.filtpick.Common;
import com.apeng.filtpick.gui.screen.FiltPickScreen;
import com.apeng.filtpick.mixinduck.BlockedItemsContainer;
import com.apeng.filtpick.mixinduck.FiltListContainer;
import com.apeng.filtpick.property.FiltListPropertyDelegate;
import com.apeng.filtpick.tracker.BlockedItemsTracker;
import com.apeng.filtpick.util.PlayerContainer;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;


@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player implements FiltListContainer, BlockedItemsContainer {

    @Unique
    private static final String TAG_FILT_LIST = "FiltList";

    @Unique
    private static final String TAG_IS_WHITELIST_MODE_ON = "isWhiteListModeOn";

    @Unique
    private static final String TAG_IS_DESTRUCTION_MODE_ON = "isDestructionModeOn";

    @Shadow
    public abstract void closeContainer();

    @Shadow
    public abstract void doCloseContainer();

    @Unique
    private PlayerContainer filtList = new PlayerContainer(Common.getServerConfig().CONTAINER_ROW_COUNT.get() * 9);

    @Unique
    private FiltListPropertyDelegate filtListPropertyDelegate = new FiltListPropertyDelegate();

    @Unique
    private final Set<Item> currentlyBlockedItems = new HashSet<>();

    public ServerPlayerEntityMixin(Level world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readFiltPickInventoryInfoFromNbt(ValueInput input, CallbackInfo callbackInfo) {
        readFiltList(input);
        readPropertyDelegate(input);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void writeFiltPickInventoryInfoToNbt(ValueOutput output, CallbackInfo callbackInfo) {
        writeFiltList(output);
        writePropertyDelegate(output);
    }

    // To keep list after death
    @Inject(method = "restoreFrom", at = @At("TAIL"))
    public void copyFilePickInventory(ServerPlayer oldPlayer, boolean keepEverything, CallbackInfo ci) {
        copyFiltList((FiltListContainer) oldPlayer);
        copyPropertyDelegate((FiltListContainer) oldPlayer);
    }

    @Unique
    private void readFiltList(ValueInput input) {
        this.filtList.fromSlots(input.listOrEmpty(TAG_FILT_LIST, ItemStackWithSlot.CODEC));
    }

    /**
     * @param output The output to write data to.
     */
    @Unique
    private void writeFiltList(ValueOutput output) {
        this.filtList.storeAsSlots(output.list(TAG_FILT_LIST, ItemStackWithSlot.CODEC));
    }

    @Unique
    private void readPropertyDelegate(ValueInput input) {
        filtListPropertyDelegate.set(FiltPickScreen.WHITELIST_MODE_BUTTON_ID, input.getIntOr(TAG_IS_WHITELIST_MODE_ON, 0));
        filtListPropertyDelegate.set(FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID, input.getIntOr(TAG_IS_DESTRUCTION_MODE_ON, 0));
    }

    @Unique
    private void writePropertyDelegate(ValueOutput output) {
        output.putInt(TAG_IS_WHITELIST_MODE_ON, filtListPropertyDelegate.get(FiltPickScreen.WHITELIST_MODE_BUTTON_ID));
        output.putInt(TAG_IS_DESTRUCTION_MODE_ON, filtListPropertyDelegate.get(FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID));
    }

    @Unique
    private void copyPropertyDelegate(FiltListContainer oldPlayer) {
        this.filtListPropertyDelegate = oldPlayer.getFiltListPropertyDelegate();
    }

    @Unique
    private void copyFiltList(FiltListContainer oldPlayer) {
        this.filtList = oldPlayer.getFiltList();
    }

    @Override
    public PlayerContainer getFiltList() {
        return this.filtList;
    }

    @Override
    public FiltListPropertyDelegate getFiltListPropertyDelegate() {
        return this.filtListPropertyDelegate;
    }

    @Override
    public void resetFiltListWithProperties() {
        this.getFiltList().clearContent();
        this.getFiltListPropertyDelegate().reset();
    }

    @Override
    public void markItemAsBlocked(Item item) {
        currentlyBlockedItems.add(item);
    }

    @Override
    public Set<Item> getCurrentlyBlockedItems() {
        return currentlyBlockedItems;
    }

    @Override
    public void clearCurrentlyBlockedItems() {
        currentlyBlockedItems.clear();
    }

    /**
     * Every tick, we process items that were blocked during this tick and sync to client if needed.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void onPlayerTick(CallbackInfo ci) {
        BlockedItemsTracker.onPlayerTick((ServerPlayer) (Object) this);
    }
}

