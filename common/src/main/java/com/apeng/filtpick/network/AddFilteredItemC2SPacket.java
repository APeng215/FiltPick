package com.apeng.filtpick.network;

import com.apeng.filtpick.Common;
import com.apeng.filtpick.mixinduck.FiltListContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public record AddFilteredItemC2SPacket(Item item) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AddFilteredItemC2SPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Common.MOD_ID, "add_filtered_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AddFilteredItemC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(BuiltInRegistries.ITEM.key()),
            AddFilteredItemC2SPacket::item,
            AddFilteredItemC2SPacket::new
    );

    public static void handle(final AddFilteredItemC2SPacket data, Player sender) {
        if (!(sender instanceof FiltListContainer filtListContainer)) {
            return;
        }

        Item itemToAdd = data.item();
        var filtList = filtListContainer.getFiltList();

        // Check if the item is already in the list to avoid duplicates
        for (int i = 0; i < filtList.getContainerSize(); i++) {
            if (filtList.getItem(i).is(itemToAdd)) {
                return;
            }
        }

        // Find empty slot and add item
        for (int i = 0; i < filtList.getContainerSize(); i++) {
            if (filtList.getItem(i).isEmpty()) {
                filtList.setItem(i, itemToAdd.getDefaultInstance());
                return;
            }
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
