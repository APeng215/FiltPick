package com.apeng.filtpick.property;


import com.apeng.filtpick.gui.screen.FiltPickScreen;
import net.minecraft.world.inventory.ContainerData;

public class FiltListPropertyDelegate implements ContainerData {

    private int isWhiteListModeOn = 0;
    private int isDestructionModeOn = 0;

    @Override
    public int get(int index) {
        switch (index) {
            case FiltPickScreen.WHITELIST_MODE_BUTTON_ID -> {
                return isWhiteListModeOn;
            }
            case FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID -> {
                return isDestructionModeOn;
            }
        }
        return index;
    }

    @Override
    public void set(int index, int value) {
        switch (index) {
            case FiltPickScreen.WHITELIST_MODE_BUTTON_ID -> {
                isWhiteListModeOn = value;
            }
            case FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID -> {
                isDestructionModeOn = value;
            }
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void switchState(int index) {
        switch (index) {
            case FiltPickScreen.WHITELIST_MODE_BUTTON_ID -> {
                if (isWhiteListModeOn == 0) {
                    isWhiteListModeOn = 1;
                } else {
                    isWhiteListModeOn = 0;
                }
            }
            case FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID -> {
                if (isDestructionModeOn == 0) {
                    isDestructionModeOn = 1;
                } else {
                    isDestructionModeOn = 0;
                }
            }
        }
    }

    public void reset() {
        for (int i = 0; i < this.getCount(); i++) {
            this.set(i, 0);
        }
    }


}
