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
        CHANNEL.registerMessage(packetId++, RClickBlockPacket.class, RClickBlockPacket::encode, RClickBlockPacket::decode, RClickBlockPacket::handle);
        CHANNEL.registerMessage(packetId++, EntityDataPacket.class, EntityDataPacket::encode, EntityDataPacket::decode, EntityDataPacket::handle);
        CHANNEL.registerMessage(packetId++, StalkerSyncPacket.class, StalkerSyncPacket::encode, StalkerSyncPacket::decode, StalkerSyncPacket::handle);
        CHANNEL.registerMessage(packetId++, ClientFuelPacket.class, ClientFuelPacket::encode, ClientFuelPacket::decode, ClientFuelPacket::handle);
        CHANNEL.registerMessage(packetId++, ClientStalkerPacket.class, ClientStalkerPacket::encode, ClientStalkerPacket::decode, ClientStalkerPacket::handle);
        CHANNEL.registerMessage(packetId++, ServerRemoteConnectPacket.class, ServerRemoteConnectPacket::encode, ServerRemoteConnectPacket::decode, ServerRemoteConnectPacket::handle);

    }

    // 发送数据包到客户端
    public static void sendToClient(ServerPlayer player, Object packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}