package com.apeng.filtpick.network;

import com.apeng.filtpick.Common;
import com.apeng.filtpick.hud.BlockedItemsHud;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.HashSet;
import java.util.Set;

public record SyncBlockedItemsS2CPacket(Set<Item> items) implements CustomPacketPayload {

    public static final Type<SyncBlockedItemsS2CPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Common.MOD_ID, "sync_blocked_items"));

    // We use a HashSet specifically to match the collection codec and ensure uniqueness
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBlockedItemsS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.registry(BuiltInRegistries.ITEM.key())),
            packet -> new HashSet<>(packet.items()),
            SyncBlockedItemsS2CPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * Client-side handler for the packet.
     */
    public static void handle(final SyncBlockedItemsS2CPacket data) {
        BlockedItemsHud.updateBlockedItems(data.items());
    }
}
