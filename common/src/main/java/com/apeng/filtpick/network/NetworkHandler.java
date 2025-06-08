package com.apeng.filtpick.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface NetworkHandler {

//    Code for neoforge
//    private static final String PROTOCOL_VERSION = "1";
//
//    public static void registerAll(final RegisterPayloadHandlersEvent event) {
//        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
//        registrar.playToServer(
//                SynMenuFieldC2SPacket.TYPE,
//                SynMenuFieldC2SPacket.STREAM_CODEC,
//                SynMenuFieldC2SPacket::handle
//        ).playToServer(
//                OpenFiltPickScreenC2SPacket.TYPE,
//                OpenFiltPickScreenC2SPacket.STREAM_CODEC,
//                OpenFiltPickScreenC2SPacket::handle
//        );
//    }

    void sendToServer(CustomPacketPayload packetPayload);

    void sendToPlayer(CustomPacketPayload packetPayload, ServerPlayer player);
}
