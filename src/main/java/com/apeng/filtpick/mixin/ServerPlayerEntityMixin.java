package com.apeng.filtpick.mixin;


import com.apeng.filtpick.guis.util.ImplementedInventory;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ImplementedInventory, ServerPlayerEntityDuck {
    public boolean filtPickIsWhiteListMode = false;
    public final DefaultedList<ItemStack> filtPickInventory = DefaultedList.ofSize(27, ItemStack.EMPTY);

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile, boolean filtPickIsWhiteListMode) {
        super(world, pos, yaw, profile);
        this.filtPickIsWhiteListMode = filtPickIsWhiteListMode;
    }


    @Inject(method = "readCustomDataFromNbt",at=@At("TAIL"))
    public void readFiltPickInventoryInfoFromNbt(NbtCompound nbt, CallbackInfo callbackInfo){
        Inventories.readNbt(nbt, this.filtPickInventory);
        filtPickIsWhiteListMode = nbt.getBoolean("filtPickWhiteListMode");
    }



    @Inject(method = "writeCustomDataToNbt",at=@At("TAIL"))
    public void writeFiltPickInventoryInfoToNbt(NbtCompound nbt, CallbackInfo callbackInfo){
        Inventories.writeNbt(nbt, this.filtPickInventory);
        nbt.putBoolean("filtPickWhiteListMode",filtPickIsWhiteListMode);
    }


    @Override
    //ImplementedInventory Override
    public DefaultedList<ItemStack> getItems() {
        return this.filtPickInventory;
    }

    @Override
    //Duck Override
    public DefaultedList<ItemStack> getFiltPickInventory() {
        return filtPickInventory;
    }
    //Duck Override
    @Override
    public boolean getFiltPickIsWhiteListMode() {
        return filtPickIsWhiteListMode;
    }
    //Duck Override
    @Override
    public void setFiltPickWhiteListMode(Boolean bool) {
        filtPickIsWhiteListMode=bool;
    }
}
