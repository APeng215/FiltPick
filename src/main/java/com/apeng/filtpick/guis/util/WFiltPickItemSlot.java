package com.apeng.filtpick.guis.util;

import com.apeng.filtpick.NetWorkingIDs;
import com.apeng.filtpick.guis.custom.FiltPickGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;

public class WFiltPickItemSlot extends WItemSlot {
    private final FiltPickGuiDescription filtPickGuiDescription;
    private final int slotIndex;

    public WFiltPickItemSlot(Inventory inventory, int startIndex, int slotsWide, int slotsHigh, boolean big, FiltPickGuiDescription filtPickGuiDescription) {
        super(inventory, startIndex, slotsWide, slotsHigh, big);
        this.filtPickGuiDescription = filtPickGuiDescription;
        this.slotIndex = startIndex;
    }


    @Override
    public InputResult onMouseDown(int x, int y, int button) {
        HashMap<Integer, ItemStack> itemStack = new HashMap<>();
        itemStack.put(this.slotIndex, filtPickGuiDescription.getCursorStack());
        PacketByteBuf packet = PacketByteBufs.create();
        packet.writeMap(itemStack, PacketByteBuf::writeInt, PacketByteBuf::writeItemStack);
        ClientPlayNetworking.send(NetWorkingIDs.SET_ITEMSTACK_C2S, packet);
        return InputResult.PROCESSED;
    }

    @Override
    public InputResult onKeyPressed(int ch, int key, int modifiers) {
        return null;
    }


}
