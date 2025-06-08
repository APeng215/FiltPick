package com.apeng.filtpick;

import com.apeng.filtpick.network.NetworkHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class NetworkHandlerFabricImpl implements NetworkHandler {
    @Override
    public void sendToServer(CustomPacketPayload packetPayload) {
        ClientPlayNetworking.send(packetPayload);
    }

    @Override
    public void sendToPlayer(CustomPacketPayload packetPayload, ServerPlayer player) {
        ServerPlayNetworking.send(player, packetPayload);
    }
}
