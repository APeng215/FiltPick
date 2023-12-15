package com.apeng.filtpick.util;

import com.apeng.filtpick.guis.screen.FiltPickScreen;
import com.apeng.filtpick.util.IntBoolConvertor;
import net.minecraft.screen.PropertyDelegate;

public class FiltPickPropertyDelegate implements PropertyDelegate {
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
        for (int i = 0; i < size(); i++) {
            set(i, 0);
        }
    }

    @Override
    public int size() {
        return 2;
    }

}
