package com.apeng.filtpick.mixinduck;

import com.apeng.filtpick.property.FiltListPropertyDelegate;
import com.apeng.filtpick.util.PlayerContainer;
import net.minecraft.world.SimpleContainer;

public interface FiltListContainer {

    PlayerContainer getFiltList();

    FiltListPropertyDelegate getFiltListPropertyDelegate();

    void resetFiltListWithProperties();

}
