package com.apeng.filtpick;

import com.apeng.filtpick.guis.custom.FiltPickScreenHandler;
import com.apeng.filtpick.mixinduck.ServerPlayerEntityDuck;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

public class FiltPick implements ModInitializer {
    public static ScreenHandlerType<FiltPickScreenHandler> FilePick_SCREEN_HANDLER= new ScreenHandlerType<>(FiltPickScreenHandler::new);



    public void onInitialize() {

        Registry.register(Registry.SCREEN_HANDLER,new Identifier("filtpick","filtpick_screen"),FilePick_SCREEN_HANDLER);
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("open_filtpick_screen"),((server, player, handler, buf, responseSender) -> server.execute(()->{
            NamedScreenHandlerFactory modScreenFactory = new NamedScreenHandlerFactory() {
                @Override
                public boolean shouldCloseCurrentScreen() {
                    return false;
                }

                @Override
                public Text getDisplayName() {
                    return Text.of("FiltPick");
                }

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new FiltPickScreenHandler(syncId,inv, (Inventory) player);
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
