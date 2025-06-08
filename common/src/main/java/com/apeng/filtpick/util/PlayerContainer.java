package com.apeng.filtpick.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

/**
 * A container which can store empty stacks information. {@link SimpleContainer} will ignore empty stacks when saving its stacks.
 */
public class PlayerContainer extends SimpleContainer {

    public PlayerContainer(int pSize) {
        super(pSize);
    }

    /**
     * Copied from {@link net.minecraft.world.inventory.PlayerEnderChestContainer#createTag(HolderLookup.Provider)}
     *
     * @param pLevelRegistry
     * @return
     */
    @Override
    public ListTag createTag(HolderLookup.Provider pLevelRegistry) {
        ListTag listtag = new ListTag();

        for (int i = 0; i < this.getContainerSize(); i++) {
            ItemStack itemstack = this.getItem(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte) i);
                listtag.add(itemstack.save(pLevelRegistry, compoundtag));
            }
        }

        return listtag;
    }

    /**
     * Copied from {@link net.minecraft.world.inventory.PlayerEnderChestContainer#fromTag(ListTag, HolderLookup.Provider)}
     *
     * @param pTag
     * @param pLevelRegistry
     */
    @Override
    public void fromTag(ListTag pTag, HolderLookup.Provider pLevelRegistry) {
        for (int i = 0; i < this.getContainerSize(); i++) {
            this.setItem(i, ItemStack.EMPTY);
        }

        for (int k = 0; k < pTag.size(); k++) {
            CompoundTag compoundtag = pTag.getCompound(k).orElseThrow();
            int j = compoundtag.getByte("Slot").orElseThrow() & 255;
            if (j >= 0 && j < this.getContainerSize()) {
                this.setItem(j, ItemStack.parse(pLevelRegistry, compoundtag).orElse(ItemStack.EMPTY));
            }
        }
    }

}
