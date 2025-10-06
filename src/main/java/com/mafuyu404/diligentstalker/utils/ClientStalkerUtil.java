package com.mafuyu404.diligentstalker.utils;

import com.mafuyu404.diligentstalker.event.handler.ChunkLoadTask;
import com.mafuyu404.diligentstalker.event.handler.StalkerControl;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.network.ServerRemoteConnectPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import java.util.function.Predicate;

public class ClientStalkerUtil {
    private static int pendingConnectEntityId = -1;
    private static int connectRetryCount = 0;
    private static final int MAX_CONNECT_RETRIES = 100; // 最多重试100次（约5秒）
    
    public static boolean isKeyPressed(int glfwKeyCode) {
        long window = Minecraft.getInstance().getWindow().getWindow();
        return GLFW.glfwGetKey(window, glfwKeyCode) == GLFW.GLFW_PRESS;
    }

    public static void updateFuel(int id, int fuel) {
        ClientLevel level = Minecraft.getInstance().level;
        Entity entity = level.getEntity(id);
        ControllableUtils.setFuel(entity, fuel);
    }

    public static void updateStorage() {

    }

    //TODO 临时解决，待修复
    public static void clientConnect(int id) {
        ClientLevel level = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;
        
        if (level == null || player == null) return;
        
        Entity entity = level.getEntity(id);
        if (entity != null) {
            // 实体存在，直接连接
            Stalker.connect(player, entity);
            // 清除待连接状态
            pendingConnectEntityId = -1;
        } else {
            // 实体不存在，设置为待连接状态
            pendingConnectEntityId = id;
        }
        connectRetryCount = 0;
    }
    
    // 在客户端tick中检查待连接的实体
    public static void checkPendingConnect() {
        if (pendingConnectEntityId == -1) return;
        
        ClientLevel level = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;
        
        if (level == null || player == null) return;
        
        Entity entity = level.getEntity(pendingConnectEntityId);
        if (entity != null) {
            // 实体现在存在了，进行连接
            Stalker.connect(player, entity);
            pendingConnectEntityId = -1;
            connectRetryCount = 0;
        } else {
            connectRetryCount++;
            if (connectRetryCount >= MAX_CONNECT_RETRIES) {
                // 超过最大重试次数，放弃连接
                pendingConnectEntityId = -1;
                connectRetryCount = 0;
            }
        }
    }

    public static Vec3 getCameraPosition() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
    }

    public static boolean handleChunkPacket(ClientboundLevelChunkWithLightPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;
        if (!Stalker.hasInstanceOf(player)) return false;
        Entity stalker = Stalker.getInstanceOf(player).getStalker();
        if (stalker == null) return false;
        ChunkLoadTask.TASK_LIST.add(packet);
        return true;
    }

    private static Predicate<Entity> ConnectingTarget;

    public static void setConnectingTarget(Predicate<Entity> predicate) {
        ConnectingTarget = predicate;
    }

    public static boolean matchConnectingTarget(Entity entity) {
        if (ConnectingTarget != null) {
            return ConnectingTarget.test(entity);
        }
        return false;
    }

    private static BlockPos VisualCenter;

    public static void setVisualCenter(BlockPos blockPos) {
        VisualCenter = blockPos;
    }

    public static BlockPos getVisualCenter() {
        return VisualCenter;
    }

    public static void tryRemoteConnect(BlockPos center, Predicate<Entity> predicate) {
        setVisualCenter(center);
        NetworkHandler.sendToServer(new ServerRemoteConnectPacket(center));
        setConnectingTarget(predicate);
    }

    public static void cancelRemoteConnect() {
        setVisualCenter(BlockPos.ZERO);
        NetworkHandler.sendToServer(new ServerRemoteConnectPacket(BlockPos.ZERO));
        setConnectingTarget(null);
    }

    public static Entity getLocalStalker() {
        Stalker instance = Stalker.getInstanceOf(Minecraft.getInstance().player);
        if (instance != null) return instance.getStalker();
        return null;
    }

    public static void setLocalViewXRot(float value) {
        StalkerControl.xRot = value;
    }

    public static void setLocalViewYRot(float value) {
        StalkerControl.yRot = value;
    }

    public static float getCameraXRot() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getXRot();
    }

    public static float getCameraYRot() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
    }
}
