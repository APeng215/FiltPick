package com.apeng.filtpick;

import com.apeng.filtpick.guis.custom.FiltPickGuiDescription;
import com.apeng.filtpick.mixinduck.ServerPlayerEntityDuck;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FiltPick implements ModInitializer {
    public void onInitialize() {
        Registry.register(Registries.SCREEN_HANDLER, "filtpickscreen", FILTPICK_SCREEN_HANDLER_TYPE);
        registerC2SReceiver();
    }    public static ScreenHandlerType<FiltPickGuiDescription> FILTPICK_SCREEN_HANDLER_TYPE = new ScreenHandlerType<>(FiltPickGuiDescription::new, FeatureSet.empty());

    private void registerC2SReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(NetWorkingIDs.OPEN_FILTPICK_SCREEN_C2S, ((server, player, handler, buf, responseSender) -> server.execute(() -> {
            NamedScreenHandlerFactory modScreenFactory = new NamedScreenHandlerFactory() {
                @Override
                public boolean shouldCloseCurrentScreen() {
                    return false;
                }

                @Override
                public Text getDisplayName() {
                    return Text.of("");
                }

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new FiltPickGuiDescription(syncId, inv, (Inventory) player, null);
                }
            };
            player.openHandledScreen(modScreenFactory);
        })));
        ServerPlayNetworking.registerGlobalReceiver(NetWorkingIDs.UPDATE_FILTPICK_MODE_C2S, (server, player, handler, buf, responseSender) -> {
            boolean updateInfo = buf.readBoolean();
            server.execute(() -> ((ServerPlayerEntityDuck) player).setFiltPickWhiteListMode(updateInfo));
        });
        ServerPlayNetworking.registerGlobalReceiver(NetWorkingIDs.UPDATE_FILTPICK_DESTRUCTION_MODE_C2S, (server, player, handler, buf, responseSender) -> {
            boolean updateInfo = buf.readBoolean();
            server.execute(() -> ((ServerPlayerEntityDuck) player).setFiltPickDestructionMode(updateInfo));
        });
        ServerPlayNetworking.registerGlobalReceiver(NetWorkingIDs.SET_ITEMSTACK_C2S, ((server, player, handler, buf, responseSender) -> {
            final Map<Integer, ItemStack> itemStack = buf.readMap(PacketByteBuf::readInt, PacketByteBuf::readItemStack);
            server.execute(() -> {
                for (Integer index : itemStack.keySet()) {
                    ((ServerPlayerEntityDuck) player).getFiltPickInventory().set(index, itemStack.get(index).getItem().getDefaultStack());
                }
            });
        }));
        ServerPlayNetworking.registerGlobalReceiver(NetWorkingIDs.CLEAR_LIST_C2S, ((server, player, handler, buf, responseSender) -> server.execute(() -> ((ServerPlayerEntityDuck) player).getFiltPickInventory().clear())));
        ServerPlayNetworking.registerGlobalReceiver(NetWorkingIDs.REQUIRE_SYN_C2S, ((server, player, handler, buf, responseSender) -> server.execute(() -> {
            responseSender.sendPacket(NetWorkingIDs.SYN_PICKMODE_S2C, new PacketByteBuf(PacketByteBufs.create().writeBoolean(((ServerPlayerEntityDuck) player).getFiltPickIsWhiteListMode())));
            responseSender.sendPacket(NetWorkingIDs.SYN_DESTRUCTION_MODE_S2C, new PacketByteBuf(PacketByteBufs.create().writeBoolean(((ServerPlayerEntityDuck) player).getFiltPickIsDestructionMode())));
        })));
    }


}
