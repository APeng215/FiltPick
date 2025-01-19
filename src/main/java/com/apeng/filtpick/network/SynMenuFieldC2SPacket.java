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

public record SynMenuFieldC2SPacket(int displayedRowStartIndex) implements CustomPayload, ServerPlayNetworking.PlayPayloadHandler<SynMenuFieldC2SPacket>   {

    public static final CustomPayload.Id<SynMenuFieldC2SPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(FiltPick.ID, "syn_menu_fields"));
    public static final PacketCodec<PacketByteBuf, SynMenuFieldC2SPacket> CODEC = PacketCodec.of(SynMenuFieldC2SPacket::write, SynMenuFieldC2SPacket::new);
    public static final CustomPayload.Type PAYLOAD_TYPE = PayloadTypeRegistry.playC2S().register(PACKET_ID, CODEC);

    public SynMenuFieldC2SPacket(PacketByteBuf packetByteBuf) {
        this(packetByteBuf.readInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(this.displayedRowStartIndex);
    }

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
    public void receive(SynMenuFieldC2SPacket payload, ServerPlayNetworking.Context context) {
        if (context.player().currentScreenHandler instanceof FiltPickScreenHandler filtPickMenu) {
            filtPickMenu.setDisplayedRowOffsetAndUpdate(payload.displayedRowStartIndex);
            filtPickMenu.updateToClient(); // Respond is important, making sure everything is synchronized.
        } else {
            // TODO: Logger something
        }

    }

}
