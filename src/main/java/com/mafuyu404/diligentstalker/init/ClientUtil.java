package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.event.ChunkLoadTask;
import com.mafuyu404.diligentstalker.event.StalkerControl;
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

public class ClientUtil {
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
        if (new ChunkPos(packet.getX(), packet.getZ()).equals(new ChunkPos(BlockPos.containing(ClientUtil.getCameraPosition())))) return false;
        if (Stalker.hasInstanceOf(player)) {
            ChunkLoadTask.TaskList.add(packet);
            return true;
        }
        return false;
    }
}
