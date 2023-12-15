package com.apeng.filtpick.mixin;


import com.apeng.filtpick.guis.screen.FiltPickScreen;
import com.apeng.filtpick.mixinduck.ServerPlayerEntityDuck;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    public void filtPickLogic(PlayerEntity player, CallbackInfo callbackInfo) {

        if (isClient() || !checkGameMode((ServerPlayerEntity) player)) {
            return;
        }

        filtPick((ServerPlayerEntityDuck) player, callbackInfo, getCollisionItem(), getFiltList((ServerPlayerEntityDuck) player));

    }

    private static DefaultedList<ItemStack> getFiltList(ServerPlayerEntityDuck player) {
        return player.getFiltList();
    }

    private boolean isClient() {
        return this.getWorld().isClient;
    }

    private Item getCollisionItem() {
        return this.getStack().getItem();
    }

    @Shadow
    public abstract ItemStack getStack();

    private static boolean checkGameMode(ServerPlayerEntity player) {
        return isSurvivalMode(player) || isAdventureMode(player);
    }

    private static boolean isAdventureMode(ServerPlayerEntity player) {
        return player.interactionManager.getGameMode() == GameMode.ADVENTURE;
    }

    private static boolean isSurvivalMode(ServerPlayerEntity player) {
        return player.interactionManager.getGameMode() == GameMode.SURVIVAL;
    }

    private void filtPick(ServerPlayerEntityDuck player, CallbackInfo callbackInfo, Item pickedItem, DefaultedList<ItemStack> filtList) {
        if (isWhiteListMode(player)) {
            applyWhiteListMode(player, callbackInfo, pickedItem, filtList);
        } else {
            applyBlackListMode(player, callbackInfo, pickedItem, filtList);
        }
    }

    private void applyBlackListMode(ServerPlayerEntityDuck player, CallbackInfo callbackInfo, Item pickedItem, DefaultedList<ItemStack> filtList) {
        if (listContainsItem(pickedItem, filtList)) {
            dontPick(callbackInfo);
            if (isDestructionMode(player)) {
                this.discard();
            }
        }
    }

    private void applyWhiteListMode(ServerPlayerEntityDuck player, CallbackInfo callbackInfo, Item pickedItem, DefaultedList<ItemStack> filtList) {
        if (listContainsItem(pickedItem, filtList)) {
            return;
        }
        dontPick(callbackInfo);
        checkDestruction(player);
    }

    private void checkDestruction(ServerPlayerEntityDuck player) {
        if (isDestructionMode(player)) {
            this.discard();
        }
    }

    private static boolean isDestructionMode(ServerPlayerEntityDuck player) {
        return player.getProperty(FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID) == 1;
    }

    private static void dontPick(CallbackInfo callbackInfo) {
        callbackInfo.cancel();
    }

    private static boolean listContainsItem(Item pickedItem, DefaultedList<ItemStack> filtList) {
        return filtList.stream().anyMatch((itemStack -> itemStack.getItem().equals(pickedItem)));
    }

    private static boolean isWhiteListMode(ServerPlayerEntityDuck player) {
        return player.getProperty(FiltPickScreen.WHITELIST_MODE_BUTTON_ID) == 1;
    }
}
