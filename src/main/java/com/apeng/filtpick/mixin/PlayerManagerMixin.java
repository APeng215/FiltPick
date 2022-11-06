package com.apeng.filtpick.mixin;

import com.apeng.filtpick.mixinduck.ServerPlayerEntityDuck;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Inject(method = "respawnPlayer", at = @At("TAIL"),locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void synchronizeListModeWithClient(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir, BlockPos blockPos, float f, boolean bl, ServerWorld serverWorld, Optional optional, ServerWorld serverWorld2, ServerPlayerEntity serverPlayerEntity){
        PacketByteBuf modeInfo = new PacketByteBuf(PacketByteBufs.create().writeBoolean(((ServerPlayerEntityDuck)player).getFiltPickIsWhiteListMode()));
        ServerPlayNetworking.send(player,new Identifier("syn_listmode"), modeInfo);
    }

}
