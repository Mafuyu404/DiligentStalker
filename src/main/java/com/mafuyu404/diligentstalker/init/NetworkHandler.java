package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.network.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class NetworkHandler {
    // 定义数据包ID
    public static final ResourceLocation RCLICK_BLOCK_PACKET = new ResourceLocation(DiligentStalker.MODID, "rclick_block");
    public static final ResourceLocation ENTITY_DATA_PACKET = new ResourceLocation(DiligentStalker.MODID, "entity_data");
    public static final ResourceLocation STALKER_SYNC_PACKET = new ResourceLocation(DiligentStalker.MODID, "stalker_sync");
    public static final ResourceLocation CLIENT_FUEL_PACKET = new ResourceLocation(DiligentStalker.MODID, "client_fuel");
    public static final ResourceLocation CLIENT_STALKER_PACKET = new ResourceLocation(DiligentStalker.MODID, "client_stalker");

    // 注册数据包
    public static void register() {
        // 服务器端接收的数据包
        ServerPlayNetworking.registerGlobalReceiver(RCLICK_BLOCK_PACKET, RClickBlockPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ENTITY_DATA_PACKET, EntityDataPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(STALKER_SYNC_PACKET, StalkerSyncPacket::handle);

        // 客户端接收的数据包
        ClientPlayNetworking.registerGlobalReceiver(CLIENT_FUEL_PACKET, ClientFuelPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(CLIENT_STALKER_PACKET, ClientStalkerPacket::handle);
    }

    // 发送数据包到客户端
    public static void sendToClient(ServerPlayer player, Object packet) {
        FriendlyByteBuf buf = PacketByteBufs.create();

        if (packet instanceof ClientFuelPacket clientFuelPacket) {
            ClientFuelPacket.encode(clientFuelPacket, buf);
            ServerPlayNetworking.send(player, CLIENT_FUEL_PACKET, buf);
        } else if (packet instanceof ClientStalkerPacket clientStalkerPacket) {
            ClientStalkerPacket.encode(clientStalkerPacket, buf);
            ServerPlayNetworking.send(player, CLIENT_STALKER_PACKET, buf);
        }
    }

    // 发送数据包到服务器
    public static void sendToServer(Object packet) {
        FriendlyByteBuf buf = PacketByteBufs.create();

        if (packet instanceof StalkerSyncPacket stalkerSyncPacket) {
            StalkerSyncPacket.encode(stalkerSyncPacket, buf);
            ClientPlayNetworking.send(STALKER_SYNC_PACKET, buf);
        } else if (packet instanceof RClickBlockPacket rClickBlockPacket) {
            RClickBlockPacket.encode(rClickBlockPacket, buf);
            ClientPlayNetworking.send(RCLICK_BLOCK_PACKET, buf);
        } else if (packet instanceof EntityDataPacket entityDataPacket) {
            EntityDataPacket.encode(entityDataPacket, buf);
            ClientPlayNetworking.send(ENTITY_DATA_PACKET, buf);
        }
    }
}