package com.apeng.filtpick.network;

import com.apeng.filtpick.FiltPick;
import com.apeng.filtpick.guis.screen.FiltPickScreenHandler;
import com.apeng.filtpick.mixin.ServerPlayerEntityMixin;
import com.apeng.filtpick.mixinduck.ServerPlayerEntityDuck;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record OpenFiltPickScreenC2SPacket() implements FabricPacket, ServerPlayNetworking.PlayPacketHandler<OpenFiltPickScreenC2SPacket>  {

    public static final PacketType<OpenFiltPickScreenC2SPacket> TYPE = PacketType.create(Identifier.of(FiltPick.ID, "open_screen"), OpenFiltPickScreenC2SPacket::new);

    public OpenFiltPickScreenC2SPacket(PacketByteBuf buf) {
        this();
    }

    /**
     * Writes the contents of this packet to the buffer.
     *
     * @param buf the output buffer
     */
    @Override
    public void write(PacketByteBuf buf) {

    }

    /**
     * Returns the packet type of this packet.
     *
     * <p>Implementations should store the packet type instance in a {@code static final}
     * field and return that here, instead of creating a new instance.
     *
     * @return the type of this packet
     */
    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    /**
     * Handles the incoming packet. This is called on the server thread, and can safely
     * manipulate the world.
     *
     * <p>An example usage of this is to create an explosion where the player is looking:
     * <pre>{@code
     * // See FabricPacket for creating the packet
     * ServerPlayNetworking.registerReceiver(BOOM_PACKET_TYPE, (player, packet, responseSender) -> {
     * 	ModPacketHandler.createExplosion(player, packet.fire());
     * });
     * }</pre>
     *
     * <p>The server and the network handler can be accessed via {@link ServerPlayerEntity#server}
     * and {@link ServerPlayerEntity#networkHandler}, respectively.
     *
     * @param packet         the packet
     * @param player         the player that received the packet
     * @param responseSender the packet sender
     * @see FabricPacket
     */
    @Override
    public void receive(OpenFiltPickScreenC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender) {
        player.openHandledScreen(new ExtendedScreenHandlerFactory() {

            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
            }

            @Override
            public Text getDisplayName() {
                return Text.of("FiltPick");
            }

            @Override
            public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new FiltPickScreenHandler(syncId, playerInventory, (Inventory) player, ((ServerPlayerEntityDuck)player).getFiltPickPropertyDelegate());
            }

            @Override
            public boolean shouldCloseCurrentScreen() {
                return false;
            }
        });
    }
}
