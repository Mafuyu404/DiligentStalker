package com.mafuyu404.diligentstalker.utils;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.event.ChunkLoadTask;
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
        if (entity instanceof DroneStalkerEntity droneStalker) {
            droneStalker.setFuel(fuel);
        }
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

    public static boolean handleChunkPacket(ClientboundLevelChunkWithLightPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (new ChunkPos(packet.getX(), packet.getZ()).equals(new ChunkPos(BlockPos.containing(ClientStalkerUtil.getCameraPosition())))) return false;
        if (Stalker.hasInstanceOf(player)) {
            ChunkLoadTask.TaskList.add(packet);
            return true;
        }
        return false;
    }

    private static Predicate<Entity> ConnectingTarget;
    public static void setConnectingTarget(Predicate<Entity> predicate) {
        ConnectingTarget = predicate;
    }
    public static Predicate<Entity> getConnectingTarget() {
        return ConnectingTarget;
    }

    private static BlockPos VisualCenter;
    public static void setVisualCenter(BlockPos blockPos) {
        if (Stalker.hasInstanceOf(Minecraft.getInstance().player)) return;
        VisualCenter = blockPos;
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
        setVisualCenter(BlockPos.ZERO);
        NetworkHandler.CHANNEL.sendToServer(new ServerRemoteConnectPacket(BlockPos.ZERO));
        setConnectingTarget(null);
    }
}
