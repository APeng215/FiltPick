package com.apeng.filtpick.guis.widget;

import net.minecraft.text.Text;

public enum FiltPickMode {
    WHITE_LIST("white_list"),
    BLACE_LIST("black_list");


    private final String name;

    FiltPickMode(String name) {
        this.name = name;
    }

    public Text getTranslatableName() {
        return Text.translatable(this.name);
    }

}
