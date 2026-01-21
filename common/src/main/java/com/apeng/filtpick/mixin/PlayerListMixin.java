package com.apeng.filtpick.mixin;

import com.apeng.filtpick.tracker.BlockedItemsTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "remove", at = @At("HEAD"))
    private void onPlayerLogout(ServerPlayer player, CallbackInfo ci) {
        BlockedItemsTracker.onPlayerLogout(player);
    }
}

