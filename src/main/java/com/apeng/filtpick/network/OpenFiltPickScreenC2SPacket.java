package com.apeng.filtpick.network;

import com.apeng.filtpick.FiltPick;
import com.apeng.filtpick.guis.screen.FiltPickScreenHandler;
import com.apeng.filtpick.mixinduck.ServerPlayerEntityDuck;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record OpenFiltPickScreenC2SPacket() implements CustomPayload, ServerPlayNetworking.PlayPayloadHandler<OpenFiltPickScreenC2SPacket>   {

    public static final CustomPayload.Id<OpenFiltPickScreenC2SPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(FiltPick.ID, "open_screen"));
    public static final PacketCodec<PacketByteBuf, OpenFiltPickScreenC2SPacket> CODEC = PacketCodec.of(OpenFiltPickScreenC2SPacket::write, OpenFiltPickScreenC2SPacket::new);
    public static final CustomPayload.Type PAYLOAD_TYPE = PayloadTypeRegistry.playC2S().register(PACKET_ID, CODEC);

    public OpenFiltPickScreenC2SPacket(PacketByteBuf buf) {
        this();
    }

    public void write(PacketByteBuf buf) {}

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    /**
     * Handles the incoming packet. This is called on the server thread, and can safely
     * manipulate the world.
     *
     * <p>An example usage of this is to create an explosion where the player is looking:
     * <pre>{@code
     * // use PayloadTypeRegistry for registering the payload
     * ServerPlayNetworking.registerReceiver(BoomPayload.ID, (payload, player, responseSender) -> {
     * 	ModPacketHandler.createExplosion(player, payload.fire());
     * });
     * }</pre>
     *
     * <p>The server and the network handler can be accessed via {@link ServerPlayerEntity#server}
     * and {@link ServerPlayerEntity#networkHandler}, respectively.
     *
     * @param payload the packet payload
     * @param context the play networking context
     * @see CustomPayload
     */
    @Override
    public void receive(OpenFiltPickScreenC2SPacket payload, ServerPlayNetworking.Context context) {
        context.player().openHandledScreen(new NamedScreenHandlerFactory() {

            @Override
            public Text getDisplayName() {
                return Text.empty();
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
