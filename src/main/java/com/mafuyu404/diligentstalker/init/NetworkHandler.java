package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.network.*;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID)
                .versioned(PROTOCOL_VERSION);

        registrar.playToServer(
                RClickBlockPacket.TYPE,
                RClickBlockPacket.STREAM_CODEC,
                RClickBlockPacket::handle
        );

        registrar.playToServer(
                EntityDataPacket.TYPE,
                EntityDataPacket.STREAM_CODEC,
                EntityDataPacket::handle
        );

        registrar.playToServer(
                StalkerSyncPacket.TYPE,
                StalkerSyncPacket.STREAM_CODEC,
                StalkerSyncPacket::handle
        );

        registrar.playToServer(
                ServerRemoteConnectPacket.TYPE,
                ServerRemoteConnectPacket.STREAM_CODEC,
                ServerRemoteConnectPacket::handle
        );

        registrar.playToClient(
                ClientFuelPacket.TYPE,
                ClientFuelPacket.STREAM_CODEC,
                ClientFuelPacket::handle
        );

        registrar.playToClient(
                ClientStalkerPacket.TYPE,
                ClientStalkerPacket.STREAM_CODEC,
                ClientStalkerPacket::handle
        );
    }

    public static void sendToClient(ServerPlayer player, Packet packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendToServer(Packet packet) {
        PacketDistributor.sendToServer(packet);
    }
}