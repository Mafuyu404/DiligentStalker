package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.init.ClientStalkerUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientStalkerPacket {
    private final int entityId;

    public ClientStalkerPacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(ClientStalkerPacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.entityId);
    }

    public static ClientStalkerPacket decode(FriendlyByteBuf buffer) {
        return new ClientStalkerPacket(buffer.readInt());
    }

    public static void handle(ClientStalkerPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientStalkerUtil.clientConnect(msg.entityId);
        });
        ctx.get().setPacketHandled(true);
    }
}
