package com.mafuyu404.diligentstalker.trash;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TagPacket {
    private final int entityId;
    private final String tag;
    private final boolean state;

    public TagPacket(int entityId, String tag, boolean state) {
        this.entityId = entityId;
        this.tag = tag;
        this.state = state;
    }

    public static void encode(TagPacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.entityId);
        buffer.writeUtf(msg.tag);
        buffer.writeBoolean(msg.state);
    }

    public static TagPacket decode(FriendlyByteBuf buffer) {
        return new TagPacket(buffer.readInt(), buffer.readUtf(), buffer.readBoolean());
    }

    public static void handle(TagPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            Level level = player.level();
            Entity entity = level.getEntity(msg.entityId);
            if (entity == null) return;
            if (msg.state) {
                if (!entity.getTags().contains(msg.tag)) {
                    entity.addTag(msg.tag);
                }
            }
            else entity.removeTag(msg.tag);
        });
        ctx.get().setPacketHandled(true);
    }
}
