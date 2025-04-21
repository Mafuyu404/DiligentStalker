package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.event.ServerEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MovePacket {
    private final int entityId;
    private final CompoundTag input;

    public MovePacket(int entityId, CompoundTag input) {
        this.entityId = entityId;
        this.input = input;
    }

    public static void encode(MovePacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.entityId);
        buffer.writeNbt(msg.input);
    }

    public static MovePacket decode(FriendlyByteBuf buffer) {
        return new MovePacket(buffer.readInt(), buffer.readNbt());
    }

    public static void handle(MovePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerEvent.entityId = msg.entityId;
            ServerEvent.xRot = msg.input.getFloat("xRot");
            ServerEvent.yRot = msg.input.getFloat("yRot");
            if (ServerEvent.input != null) {
                String[] boostKey = new String[]{"Up", "Down", "Left", "Right", "Jump", "Shift"};
                for (String key : boostKey) {
                    boolean oldValue = ServerEvent.input.contains(key) && ServerEvent.input.getBoolean(key);
                    if (!oldValue && msg.input.getBoolean(key)) {
                        if (!ServerEvent.boostKey.contains(key)) ServerEvent.boostKey.add(key);
                    }
                }
            }
            ServerEvent.input = msg.input;
        });
        ctx.get().setPacketHandled(true);
    }
}
