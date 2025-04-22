package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Tools;
import com.mafuyu404.diligentstalker.network.EntityDataPacket;
import com.mafuyu404.diligentstalker.network.RClickBlockPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class CameraEntityAction {
    private static boolean interactLock = true;

    @SubscribeEvent
    public static void onAction(InputEvent.Key event) {
        if (Minecraft.getInstance().screen != null) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        Options options = Minecraft.getInstance().options;
        if (!CameraEntityManage.isEnable(player)) return;
        ArrayList<Integer> controlKey = new ArrayList<>();
        if (event.getKey() == options.keySprint.getKey().getValue()) {
            options.keySprint.setDown(false);
            CameraEntityManage.quit();
        }
        if (event.getAction() == InputConstants.PRESS) {
            controlKey.add(options.keyUp.getKey().getValue());
            controlKey.add(options.keyDown.getKey().getValue());
            controlKey.add(options.keyLeft.getKey().getValue());
            controlKey.add(options.keyRight.getKey().getValue());
            controlKey.add(options.keyJump.getKey().getValue());
            controlKey.add(options.keyShift.getKey().getValue());
            if (controlKey.contains(event.getKey())) {
                syncData();
            }
        }
    }

    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity().isLocalPlayer() && CameraEntityManage.isEnable(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onInteract(InputEvent.MouseButton event) {
        if (Minecraft.getInstance().screen != null) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!CameraEntityManage.isEnable(player)) return;
        if (event.getAction() != InputConstants.PRESS) return;
        if (interactLock) {
            interactLock = false;
            return;
        } else interactLock = true;
        BlockHitResult traceResult = Tools.rayTraceBlocks(player.level(), CameraEntityManage.getCameraPosition(), CameraEntityManage.getViewVector(), 4);
        if (traceResult.getType() == HitResult.Type.BLOCK ) {
            if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                NetworkHandler.CHANNEL.sendToServer(new RClickBlockPacket(CameraEntityManage.getCameraPosition(), CameraEntityManage.getViewVector()));
                RightClickBlock(player, CameraEntityManage.getCameraPosition(), CameraEntityManage.getViewVector());
            }
        }
    }

    public static CompoundTag handleInput() {
        Options options = Minecraft.getInstance().options;
        CompoundTag input = new CompoundTag();
        input.putBoolean("Up", options.keyUp.isDown());
        input.putBoolean("Down", options.keyDown.isDown());
        input.putBoolean("Left", options.keyLeft.isDown());
        input.putBoolean("Right", options.keyRight.isDown());
        input.putBoolean("Jump", options.keyJump.isDown());
        input.putBoolean("Shift", options.keyShift.isDown());
        input.putFloat("xRot", CameraEntityManage.xRot);
        input.putFloat("yRot", CameraEntityManage.yRot);
        return input;
    }

    public static void syncData() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        NetworkHandler.CHANNEL.sendToServer(new EntityDataPacket(player.getId(), player.getPersistentData()));
    }

    public static void RightClickBlock(Player player, Vec3 position, Vec3 viewVec) {
        Level level = player.level();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getForcedChunks().forEach(value -> {
                System.out.print(value+"\n");
            });
        }
        BlockHitResult traceResult = Tools.rayTraceBlocks(level, position, viewVec, 4);
        BlockState state = level.getBlockState(traceResult.getBlockPos());
        InteractionResult result = state.use(level, player, InteractionHand.MAIN_HAND, traceResult);
        if (result.consumesAction()) {
            level.sendBlockUpdated(traceResult.getBlockPos(), state, state, 3);
        }
    }
}
