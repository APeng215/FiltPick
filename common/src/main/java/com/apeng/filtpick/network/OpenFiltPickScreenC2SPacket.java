package com.apeng.filtpick.network;

import com.apeng.filtpick.Common;
import com.apeng.filtpick.gui.screen.FiltPickMenu;
import com.apeng.filtpick.gui.util.ExtendedMenuProvider;
import com.apeng.filtpick.mixinduck.FiltListContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record OpenFiltPickScreenC2SPacket() implements CustomPacketPayload {

    public static final Type<OpenFiltPickScreenC2SPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Common.MOD_ID, "open_filtpick_screen"));

    public static final StreamCodec<ByteBuf, OpenFiltPickScreenC2SPacket> STREAM_CODEC = StreamCodec.unit(new OpenFiltPickScreenC2SPacket());

    public static void handle(final OpenFiltPickScreenC2SPacket data, Player sender) {
        sender.openMenu(new ExtendedMenuProvider() {

            @Override
            public boolean shouldClose() {
                return false;
            }

            @Override
            public Component getDisplayName() {
                return Component.empty();
            }

            @Override
            public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
                return new FiltPickMenu(pContainerId, pPlayerInventory, ((FiltListContainer)pPlayer).getFiltList(), ((FiltListContainer)pPlayer).getFiltListPropertyDelegate());
            }

        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
