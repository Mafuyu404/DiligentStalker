package com.mafuyu404.diligentstalker.utils;

import com.mafuyu404.diligentstalker.event.ChunkLoadTask;
import com.mafuyu404.diligentstalker.event.StalkerControl;
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
    public static boolean isKeyPressed(int glfwKeyCode) {
        Minecraft minecraft = Minecraft.getInstance();
        long windowHandle = minecraft.getWindow().getWindow();
        return GLFW.glfwGetKey(windowHandle, glfwKeyCode) == GLFW.GLFW_PRESS;
    }

    public static void updateFuel(int id, int fuel) {
        ClientLevel level = Minecraft.getInstance().level;
        Entity entity = level.getEntity(id);
        ControllableUtils.setFuel(entity, fuel);
    }
    public static void updateStorage() {

    }

    public static void clientConnect(int id) {
        ClientLevel level = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;
        Entity entity = level.getEntity(id);
        Stalker.connect(player, entity);
    }

    public static Vec3 getCameraPosition() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
    }

    public static ChunkPos getRemoteChunkCenter() {
        Entity stalker = getLocalStalker();
        if (stalker != null) {
            return stalker.chunkPosition();
        }
        BlockPos visualCenter = getVisualCenter();
        if (visualCenter != null) {
            return new ChunkPos(visualCenter);
        }
        return null;
    }

    public static boolean hasRemoteChunkCenter() {
        return getRemoteChunkCenter() != null;
    }

    public static void applyRemoteChunkCenter() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        ChunkPos center = getRemoteChunkCenter();
        if (level != null && center != null) {
            level.getChunkSource().updateViewCenter(center.x, center.z);
        }
    }

    public static void applyPlayerChunkCenter() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        LocalPlayer player = minecraft.player;
        if (level != null && player != null) {
            ChunkPos center = player.chunkPosition();
            level.getChunkSource().updateViewCenter(center.x, center.z);
        }
    }

    public static boolean handleChunkPacket(ClientboundLevelChunkWithLightPacket packet) {
        return queueRemoteChunkPacket(packet);
    }

    public static boolean queueRemoteChunkPacket(ClientboundLevelChunkWithLightPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (new ChunkPos(packet.getX(), packet.getZ()).equals(new ChunkPos(BlockPos.containing(ClientStalkerUtil.getCameraPosition())))) return false;
        if (Stalker.hasInstanceOf(player) || hasRemoteChunkCenter()) {
            ChunkLoadTask.add(packet);
            return true;
        }
        return false;
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
        if (Stalker.hasInstanceOf(Minecraft.getInstance().player)) return;
        VisualCenter = blockPos;
    }
    public static void clearVisualCenter() {
        VisualCenter = null;
    }
    public static BlockPos getVisualCenter() {
        if (VisualCenter == null) return null;
        return VisualCenter.equals(BlockPos.ZERO) ? null : VisualCenter;
    }

    public static void tryRemoteConnect(BlockPos center, Predicate<Entity> predicate) {
        setVisualCenter(center);
        NetworkHandler.CHANNEL.sendToServer(new ServerRemoteConnectPacket(center));
        setConnectingTarget(predicate);
    }
    public static void cancelRemoteConnect() {
        clearVisualCenter();
        applyPlayerChunkCenter();
        NetworkHandler.CHANNEL.sendToServer(new ServerRemoteConnectPacket(BlockPos.ZERO));
        setConnectingTarget(null);
        ChunkLoadTask.clear();
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
