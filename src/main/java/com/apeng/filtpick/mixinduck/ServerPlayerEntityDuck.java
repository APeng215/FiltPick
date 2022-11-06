package com.apeng.filtpick.mixinduck;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface ServerPlayerEntityDuck {
    DefaultedList<ItemStack> getFiltPickInventory();
    boolean getFiltPickIsWhiteListMode();
    void setFiltPickWhiteListMode(Boolean bool);
}
