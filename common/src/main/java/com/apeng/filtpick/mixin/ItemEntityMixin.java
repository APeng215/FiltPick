package com.apeng.filtpick.mixin;


import com.apeng.filtpick.gui.screen.FiltPickScreen;
import com.apeng.filtpick.mixinduck.FiltListContainer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;


@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    public void filtPickLogic(Player player, CallbackInfo callbackInfo) {
        if (isClient() || !checkGameMode((ServerPlayer) player)) {
            return;
        }
        filtPick((FiltListContainer) player, callbackInfo, getCollisionItem());
    }


    @Unique
    private boolean isClient() {
        return this.level().isClientSide;
    }

    @Unique
    private Item getCollisionItem() {
        return this.getItem().getItem();
    }

    @Shadow
    public abstract ItemStack getItem();

    @Unique
    private static boolean checkGameMode(ServerPlayer player) {
        return isSurvivalMode(player) || isAdventureMode(player);
    }

    @Unique
    private static boolean isAdventureMode(ServerPlayer player) {
        return player.gameMode.getGameModeForPlayer() == GameType.ADVENTURE;
    }

    @Unique
    private static boolean isSurvivalMode(ServerPlayer player) {
        return player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL;
    }

    @Unique
    private void filtPick(FiltListContainer player, CallbackInfo callbackInfo, Item pickedItem) {
        Container filtList = player.getFiltList();
        if (isWhiteListMode(player)) {
            applyWhiteListMode(player, callbackInfo, pickedItem, filtList);
        } else {
            applyBlackListMode(player, callbackInfo, pickedItem, filtList);
        }
    }

    @Unique
    private void applyBlackListMode(FiltListContainer player, CallbackInfo callbackInfo, Item pickedItem, Container filtList) {
        if (listContainsItem(pickedItem, filtList)) {
            dontPick(callbackInfo);
            if (isDestructionMode(player)) {
                this.discard();
            }
        }
    }

    @Unique
    private void applyWhiteListMode(FiltListContainer player, CallbackInfo callbackInfo, Item pickedItem, Container filtList) {
        if (listContainsItem(pickedItem, filtList)) {
            return;
        }
        dontPick(callbackInfo);
        checkDestruction(player);
    }

    @Unique
    private void checkDestruction(FiltListContainer player) {
        if (isDestructionMode(player)) {
            this.discard();
        }
    }

    @Unique
    private static boolean isDestructionMode(FiltListContainer player) {
        return player.getFiltListPropertyDelegate().get(FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID) == 1;
    }

    @Unique
    private static void dontPick(CallbackInfo callbackInfo) {
        callbackInfo.cancel();
    }

    @Unique
    private static boolean listContainsItem(Item pickedItem, Container filtList) {
        return filtList.hasAnyOf(Collections.singleton(pickedItem));
    }

    @Unique
    private static boolean isWhiteListMode(FiltListContainer player) {
        return player.getFiltListPropertyDelegate().get(FiltPickScreen.WHITELIST_MODE_BUTTON_ID) == 1;
    }
}
