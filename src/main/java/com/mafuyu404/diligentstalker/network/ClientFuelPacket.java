package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.event.StalkerControl;
import com.mafuyu404.diligentstalker.init.ClientUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientFuelPacket {
    private final int entityId;
    private final int fuel;

    public ClientFuelPacket(int entityId, int fuel) {
        this.entityId = entityId;
        this.fuel = fuel;
    }

    public static void encode(ClientFuelPacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.entityId);
        buffer.writeInt(msg.fuel);
    }

    public static ClientFuelPacket decode(FriendlyByteBuf buffer) {
        return new ClientFuelPacket(buffer.readInt(), buffer.readInt());
    }

    public static void handle(ClientFuelPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientUtil.updateFuel(msg.entityId, msg.fuel);
        });
        ctx.get().setPacketHandled(true);
    }
}
