package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.network.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class NetworkHandler {

    public static void register() {
        PayloadTypeRegistry.playC2S().register(RClickBlockPacket.TYPE, RClickBlockPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(EntityDataPacket.TYPE, EntityDataPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(StalkerSyncPacket.TYPE, StalkerSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerRemoteConnectPacket.TYPE, ServerRemoteConnectPacket.STREAM_CODEC);

        PayloadTypeRegistry.playS2C().register(ClientFuelPacket.TYPE, ClientFuelPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientStalkerPacket.TYPE, ClientStalkerPacket.STREAM_CODEC);


        ServerPlayNetworking.registerGlobalReceiver(RClickBlockPacket.TYPE, (payload, context) -> {
            RClickBlockPacket.handle(context.server(), context.player(), payload);
        });

        ServerPlayNetworking.registerGlobalReceiver(EntityDataPacket.TYPE, (payload, context) -> {
            EntityDataPacket.handle(context.server(), context.player(), payload);
        });

        ServerPlayNetworking.registerGlobalReceiver(StalkerSyncPacket.TYPE, (payload, context) -> {
            StalkerSyncPacket.handle(context.server(), context.player(), payload);
        });

        ServerPlayNetworking.registerGlobalReceiver(ServerRemoteConnectPacket.TYPE, (payload, context) -> {
            ServerRemoteConnectPacket.handle(context.server(), context.player(), payload);
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientFuelPacket.TYPE, (payload, context) -> {
            ClientFuelPacket.handle(payload, context.client());
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientStalkerPacket.TYPE, (payload, context) -> {
            ClientStalkerPacket.handle(payload, context.client());
        });
    }

    public static void sendToClient(ServerPlayer player, Packet packet) {
        ServerPlayNetworking.send(player, packet);
    }

    public static void sendToServer(Packet packet) {
        ClientPlayNetworking.send(packet);
    }
}