package com.apeng.filtpick;

import com.apeng.filtpick.network.NetworkHandler;
import com.apeng.filtpick.network.OpenFiltPickScreenC2SPacket;
import com.apeng.filtpick.network.SynMenuFieldC2SPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandlerNeoImpl implements NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static void registerAll(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToServer(
                SynMenuFieldC2SPacket.TYPE,
                SynMenuFieldC2SPacket.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> SynMenuFieldC2SPacket.handle(payload, context.player())
                )).playToServer(
                OpenFiltPickScreenC2SPacket.TYPE,
                OpenFiltPickScreenC2SPacket.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> OpenFiltPickScreenC2SPacket.handle(payload, context.player())
                )
        );
    }

    @Override
    public void sendToServer(CustomPacketPayload packetPayload) {
        PacketDistributor.sendToServer(packetPayload);
    }

    @Override
    public void sendToPlayer(CustomPacketPayload packetPayload, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, packetPayload);
    }
}
