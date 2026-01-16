package com.apeng.filtpick.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * A container which can store empty stacks information. {@link SimpleContainer} will ignore empty stacks when saving its stacks.
 */
public class PlayerContainer extends SimpleContainer {

    public PlayerContainer(int pSize) {
        super(pSize);
    }

    /**
     * Copied from {@link net.minecraft.world.inventory.PlayerEnderChestContainer#fromSlots(ValueInput.TypedInputList)}
     */
    public void fromSlots(ValueInput.TypedInputList<ItemStackWithSlot> input) {
        for(int i = 0; i < this.getContainerSize(); ++i) {
            this.setItem(i, ItemStack.EMPTY);
        }

        for(ItemStackWithSlot itemstackwithslot : input) {
            if (itemstackwithslot.isValidInContainer(this.getContainerSize())) {
                this.setItem(itemstackwithslot.slot(), itemstackwithslot.stack());
            }
        }

    }

    /**
     * Copied from {@link net.minecraft.world.inventory.PlayerEnderChestContainer#storeAsSlots(ValueOutput.TypedOutputList)}
     */
    public void storeAsSlots(ValueOutput.TypedOutputList<ItemStackWithSlot> output) {
        for(int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemstack = this.getItem(i);
            if (!itemstack.isEmpty()) {
                output.add(new ItemStackWithSlot(i, itemstack));
            }
        }

    }

}
