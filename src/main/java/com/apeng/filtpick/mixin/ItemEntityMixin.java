package com.apeng.filtpick.mixin;


import com.apeng.filtpick.mixinduck.ServerPlayerEntityDuck;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Shadow public abstract ItemStack getStack();

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onPlayerCollision",at=@At("HEAD"),cancellable = true)
    public void filtPickFilter(PlayerEntity player, CallbackInfo callbackInfo){
        if(!player.world.isClient){
            Item item = this.getStack().getItem();
            DefaultedList<ItemStack> filtPickInventory = ((ServerPlayerEntityDuck)player).getFiltPickInventory();
            filtPick((ServerPlayerEntityDuck) player, callbackInfo, item, filtPickInventory);
        }


    }

    private static void filtPick(ServerPlayerEntityDuck player, CallbackInfo callbackInfo, Item item, DefaultedList<ItemStack> filtPickInventory) {
        if(player.getFiltPickIsWhiteListMode()){
            boolean match = false;
            for(ItemStack it: filtPickInventory){
                if(it.getItem().equals(item)){
                    match =true;
                    break;
                }
            }
            if(!match) callbackInfo.cancel();
        }
        else {
            for(ItemStack it: filtPickInventory){
                if(it.getItem().equals(item)) callbackInfo.cancel();
            }
        }
    }
}
