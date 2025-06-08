package com.apeng.filtpick.network;

import com.apeng.filtpick.Common;
import com.apeng.filtpick.gui.screen.FiltPickMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record SynMenuFieldC2SPacket(int displayedRowStartIndex) implements CustomPacketPayload {

    public static final Type<SynMenuFieldC2SPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Common.MOD_ID, "syn_menu_field"));

    public static final StreamCodec<ByteBuf, SynMenuFieldC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SynMenuFieldC2SPacket::displayedRowStartIndex,
            SynMenuFieldC2SPacket::new
    );

    public static void handle(final SynMenuFieldC2SPacket data, final Player sender) {
        if (sender.containerMenu instanceof FiltPickMenu filtPickMenu) {
            filtPickMenu.setDisplayedRowOffsetAndUpdate(data.displayedRowStartIndex);
            filtPickMenu.broadcastFullState(); // Respond is important, making sure everything is synchronized.
        } else {
            Common.LOG.warn("FiltPick menu is not opening when receives a SynMenuFieldC2SPacket!");
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
