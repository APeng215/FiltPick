package com.apeng.filtpick.mixinduck;

import com.apeng.filtpick.property.FiltListPropertyDelegate;
import net.minecraft.world.SimpleContainer;

public interface FiltListContainer {

    SimpleContainer getFiltList();

    FiltListPropertyDelegate getFiltListPropertyDelegate();

    void resetFiltListWithProperties();

}
