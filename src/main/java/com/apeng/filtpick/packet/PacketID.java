package com.apeng.filtpick.packet;

import net.minecraft.util.Identifier;
import org.lwjgl.system.CallbackI;

public class PacketID {
    public static class S2C{
        public static final Identifier SYN_LISTMODE = new Identifier("syn_listmode");
    }
    public static class C2S{
        public static final Identifier OPEN_FILTPICK_SCREEN  = new Identifier("open_filtpick_screen");
        public static final Identifier UPDATE_FILTPICK_MODE  = new Identifier("update_filtpick_mode");
    }
}
