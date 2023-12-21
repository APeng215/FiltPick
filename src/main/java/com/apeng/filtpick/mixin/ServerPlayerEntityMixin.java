package com.apeng.filtpick.mixin;


import com.apeng.filtpick.guis.screen.FiltPickScreen;
import com.apeng.filtpick.util.FiltPickPropertyDelegate;
import com.apeng.filtpick.util.ImplementedInventory;
import com.apeng.filtpick.mixinduck.ServerPlayerEntityDuck;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ImplementedInventory, ServerPlayerEntityDuck {

    @Unique
    private FiltPickPropertyDelegate filtPickPropertyDelegate = new FiltPickPropertyDelegate();

    @Unique
    private DefaultedList<ItemStack> filtList = DefaultedList.ofSize(27, ItemStack.EMPTY);

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readFiltPickInventoryInfoFromNbt(NbtCompound nbt, CallbackInfo callbackInfo) {
        readFiltList(nbt);
        readPropertyDelegate(nbt);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeFiltPickInventoryInfoToNbt(NbtCompound nbt, CallbackInfo callbackInfo) {
        writeFiltList(nbt);
        writePropertyDelegate(nbt);
    }

    // To keep list after death
    @Inject(method = "copyFrom", at = @At("TAIL"))
    public void copyFilePickInventory(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        copyFiltList((ServerPlayerEntityDuck) oldPlayer);
        copyPropertyDelegate((ServerPlayerEntityDuck) oldPlayer);
    }

    @Unique
    private void readFiltList(NbtCompound nbt) {
        Inventories.readNbt(nbt, this.filtList);
    }

    @Unique
    private void readPropertyDelegate(NbtCompound nbt) {
        filtPickPropertyDelegate.set(FiltPickScreen.WHITELIST_MODE_BUTTON_ID, nbt.getInt("isWhiteListModeOn"));
        filtPickPropertyDelegate.set(FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID, nbt.getInt("isDestructionModeOn"));
    }

    @Unique
    private void writeFiltList(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.filtList);
    }

    @Unique
    private void writePropertyDelegate(NbtCompound nbt) {
        nbt.putInt("isWhiteListModeOn", filtPickPropertyDelegate.get(FiltPickScreen.WHITELIST_MODE_BUTTON_ID));
        nbt.putInt("isDestructionModeOn", filtPickPropertyDelegate.get(FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID));
    }

    @Unique
    private void copyPropertyDelegate(ServerPlayerEntityDuck oldPlayer) {
        this.filtPickPropertyDelegate = oldPlayer.getFiltPickPropertyDelegate();
    }

    @Unique
    private void copyFiltList(ServerPlayerEntityDuck oldPlayer) {
        this.filtList = oldPlayer.getFiltList();
    }

    @Override
    //ImplementedInventory Override
    public DefaultedList<ItemStack> getItems() {
        return this.filtList;
    }

    @Override
    //Duck Override
    public DefaultedList<ItemStack> getFiltList() {
        return filtList;
    }

    @Override
    public FiltPickPropertyDelegate getFiltPickPropertyDelegate() {
        return filtPickPropertyDelegate;
    }

    @Override
    public int getProperty(int index) {
        return filtPickPropertyDelegate.get(index);
    }

    @Override
    public void setProperty(int index, int value) {
        filtPickPropertyDelegate.set(index, value);
    }

    @Override
    public void switchProperty(int index) {
        filtPickPropertyDelegate.switchState(index);
    }

    @Override
    public void resetFiltList() {
        filtPickPropertyDelegate.reset();
        filtList.clear();
    }


}
