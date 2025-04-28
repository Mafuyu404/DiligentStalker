package com.mafuyu404.diligentstalker.trash;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;

public class VirtualPlayerCreator {

    public static ServerPlayer createVirtualPlayer(ServerLevel level, String name, double x, double y, double z) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        // 生成唯一UUID和用户名
        UUID uuid = UUID.randomUUID();
        GameProfile gameProfile = new GameProfile(uuid, name);

        // 创建虚拟玩家实例，使用FakePlayer或自定义类
        ServerPlayer virtualPlayer = new ServerPlayer(server, level, gameProfile) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return false;
            }
        };

        // 设置玩家初始位置和状态
        virtualPlayer.setPos(x, y, z);
        virtualPlayer.setHealth(20.0f);

        // 禁用网络同步（关键步骤）
        virtualPlayer.connection = new ServerGamePacketListenerImpl(server, new FakeConnection(), virtualPlayer) {
            @Override
            public void send(Packet<?> packet) {
                // 阻止发送玩家列表更新包
                if (!(packet instanceof ClientboundPlayerInfoUpdatePacket)) {
                    super.send(packet);
                }
            }
        };

        // 将玩家添加到服务器世界
        level.addFreshEntity(virtualPlayer);

        return virtualPlayer;
    }

    // 自定义虚拟网络连接
    private static class FakeConnection extends Connection {
        public FakeConnection() {
            super(PacketFlow.SERVERBOUND);
        }

        @Override
        public void send(Packet<?> packet) {
            // 阻止发送任何数据包
        }
    }
}
