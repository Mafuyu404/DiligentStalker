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
    public static final ResourceLocation RCLICK_BLOCK_PACKET = new ResourceLocation(DiligentStalker.MODID, "rclick_block");
    public static final ResourceLocation ENTITY_DATA_PACKET = new ResourceLocation(DiligentStalker.MODID, "entity_data");
    public static final ResourceLocation STALKER_SYNC_PACKET = new ResourceLocation(DiligentStalker.MODID, "stalker_sync");
    public static final ResourceLocation CLIENT_FUEL_PACKET = new ResourceLocation(DiligentStalker.MODID, "client_fuel");
    public static final ResourceLocation CLIENT_STALKER_PACKET = new ResourceLocation(DiligentStalker.MODID, "client_stalker");
    public static final ResourceLocation SERVER_REMOTE_CONNECT_PACKET = new ResourceLocation(DiligentStalker.MODID, "server_remote_connect");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(RCLICK_BLOCK_PACKET, (server, player, handler, buf, responseSender) -> {
            var msg = RClickBlockPacket.decode(buf);
            RClickBlockPacket.handle(server, player, msg);
        });
        ServerPlayNetworking.registerGlobalReceiver(ENTITY_DATA_PACKET, (server, player, handler, buf, responseSender) -> {
            var msg = EntityDataPacket.decode(buf);
            EntityDataPacket.handle(server, player, msg);
        });
        ServerPlayNetworking.registerGlobalReceiver(STALKER_SYNC_PACKET, (server, player, handler, buf, responseSender) -> {
            var msg = StalkerSyncPacket.decode(buf);
            StalkerSyncPacket.handle(server, player, msg);
        });
        ServerPlayNetworking.registerGlobalReceiver(SERVER_REMOTE_CONNECT_PACKET, (server, player, handler, buf, responseSender) -> {
            var msg = ServerRemoteConnectPacket.decode(buf);
            ServerRemoteConnectPacket.handle(server, player, msg);
        });

        ClientPlayNetworking.registerGlobalReceiver(CLIENT_FUEL_PACKET, (client, handler, buf, responseSender) -> {
            var msg = ClientFuelPacket.decode(buf);
            ClientFuelPacket.handle(msg, client);
        });
        ClientPlayNetworking.registerGlobalReceiver(CLIENT_STALKER_PACKET, (client, handler, buf, responseSender) -> {
            var msg = ClientStalkerPacket.decode(buf);
            ClientStalkerPacket.handle(msg, client);
        });

    }

    public static void sendToClient(ServerPlayer player, ResourceLocation id, Packet msg) {
        FriendlyByteBuf buf = new FriendlyByteBuf(PacketByteBufs.create());
        msg.encode(buf);
        ServerPlayNetworking.send(player, id, buf);
    }

    public static void sendToServer(ResourceLocation id, Packet msg) {
        FriendlyByteBuf buf = new FriendlyByteBuf(PacketByteBufs.create());
        msg.encode(buf);
        ClientPlayNetworking.send(id, buf);
    }
}