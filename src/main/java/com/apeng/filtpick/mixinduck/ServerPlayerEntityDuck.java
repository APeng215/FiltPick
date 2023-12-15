package com.apeng.filtpick.mixinduck;

import com.apeng.filtpick.util.FiltPickPropertyDelegate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface ServerPlayerEntityDuck {

    DefaultedList<ItemStack> getFiltList();

    public FiltPickPropertyDelegate getFiltPickPropertyDelegate();

    int getProperty(int index);

    void setProperty(int index ,int bool);

    void switchProperty(int index);

    void resetFiltList();

}
