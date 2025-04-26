package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.network.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL = "1.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DiligentStalker.MODID, "sync_data"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    // 注册数据包
    public static void register() {
        int packetId = 0;
        CHANNEL.registerMessage(packetId++, UnlockPacket.class, UnlockPacket::encode, UnlockPacket::decode, UnlockPacket::handle);
        CHANNEL.registerMessage(packetId++, RClickBlockPacket.class, RClickBlockPacket::encode, RClickBlockPacket::decode, RClickBlockPacket::handle);
        CHANNEL.registerMessage(packetId++, EntityDataPacket.class, EntityDataPacket::encode, EntityDataPacket::decode, EntityDataPacket::handle);
        CHANNEL.registerMessage(packetId++, CameraEntityStatePacket.class, CameraEntityStatePacket::encode, CameraEntityStatePacket::decode, CameraEntityStatePacket::handle);
        CHANNEL.registerMessage(packetId++, StalkerSyncMsg.class, StalkerSyncMsg::encode, StalkerSyncMsg::decode, StalkerSyncMsg::handle);

    }

    // 发送数据包到客户端
    public static void sendToClient(ServerPlayer player, Object packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}