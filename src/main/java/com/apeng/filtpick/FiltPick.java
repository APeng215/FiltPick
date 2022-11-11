package com.apeng.filtpick;

import com.apeng.filtpick.guis.custom.FiltPickScreenHandler;
import com.apeng.filtpick.guis.util.ImplementedInventory;
import com.apeng.filtpick.mixinduck.ServerPlayerEntityDuck;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class FiltPick implements ModInitializer {
    public static ScreenHandlerType<FiltPickScreenHandler> FilePick_SCREEN_HANDLER= ScreenHandlerRegistry.registerSimple(new Identifier("filtpick_screen"), FiltPickScreenHandler::new);



    public void onInitialize() {

        ServerPlayNetworking.registerGlobalReceiver(new Identifier("open_filtpick_screen"),((server, player, handler, buf, responseSender) -> server.execute(()->{
            NamedScreenHandlerFactory modScreenFactory = new NamedScreenHandlerFactory() {

                public boolean shouldCloseCurrentScreen() {
                    return false;
                }//Function missing

                @Override
                public Text getDisplayName() {
                    return new TranslatableText("title");
                }

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new FiltPickScreenHandler(syncId,inv, (ImplementedInventory)player);
                }
            };
            player.openHandledScreen(modScreenFactory);
        })));
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("update_filtpick_mode"),(server, player, handler, buf, responseSender) -> {
            boolean updataInfo = buf.readBoolean();
            server.execute(()-> ((ServerPlayerEntityDuck)player).setFiltPickWhiteListMode(updataInfo));
        });
    }
}
