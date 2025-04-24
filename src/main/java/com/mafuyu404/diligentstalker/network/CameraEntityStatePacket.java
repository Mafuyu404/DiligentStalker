package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.event.ServerStalker;
import com.mafuyu404.diligentstalker.init.ChunkLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CameraEntityStatePacket {
    private final int entityId;
    private final boolean state;

    public CameraEntityStatePacket(int entityId, boolean state) {
        this.entityId = entityId;
        this.state = state;
    }

    public static void encode(CameraEntityStatePacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.entityId);
        buffer.writeBoolean(msg.state);
    }

    public static CameraEntityStatePacket decode(FriendlyByteBuf buffer) {
        return new CameraEntityStatePacket(buffer.readInt(), buffer.readBoolean());
    }

    public static void handle(CameraEntityStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            Level level = player.level();
            Entity entity = level.getEntity(msg.entityId);
            if (entity == null) return;
            if (entity instanceof DroneStalkerEntity droneStalker) {
                if (!msg.state) {
                    droneStalker.fakePlayer = null;
                }
            }
            boolean wasLoading = entity.getPersistentData().getBoolean("forceLoadChunks");
//            System.out.print(msg.state+"\n");
            if (wasLoading != msg.state) {
                if (msg.state) {
                    System.out.print("active"+"\n");
                    // 创建新的ChunkLoader并激活
                    ChunkLoader loader = new ChunkLoader((ServerLevel) entity.level(), new ChunkPos(entity.blockPosition()));
                    loader.activate();
                    ServerStalker.chunkLoader.put(msg.entityId, loader);
                } else {

                    // 停用并移除ChunkLoader
                    System.out.print("close"+"\n");
                    ChunkLoader loader = ServerStalker.chunkLoader.get(msg.entityId);
                    if (loader != null) loader.deactivate();
                    ServerStalker.chunkLoader.remove(msg.entityId);
                }
                entity.getPersistentData().putBoolean("forceLoadChunks", msg.state);
            }
            if (msg.state) {
                ChunkLoader loader = ServerStalker.chunkLoader.get(msg.entityId);
                ChunkPos newPos = new ChunkPos(entity.blockPosition());
                if (!loader.center.equals(newPos)) {
                    loader.deactivate();
                    loader.center = newPos;
                    loader.activate();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
