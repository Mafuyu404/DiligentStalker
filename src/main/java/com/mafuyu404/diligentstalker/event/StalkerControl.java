package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.init.Tools;
import com.mafuyu404.diligentstalker.network.EntityDataPacket;
import com.mafuyu404.diligentstalker.network.RClickBlockPacket;
import com.mafuyu404.diligentstalker.registry.KeyBindings;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class StalkerControl {
    public static float fixedXRot, fixedYRot;
    public static float xRot, yRot;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().screen != null) return;
        if (event.phase == TickEvent.Phase.START) return;
        LocalPlayer player = Minecraft.getInstance().player;
        UUID entityUUID = Tools.uuidOfUsingStalkerMaster(player);
        if (entityUUID != null) {
            ClientLevel level = player.clientLevel;
            level.entitiesForRendering().forEach(entity -> {
                if (entity.getUUID().equals(entityUUID)) {
                    Stalker.connect(player, entity);
                }
            });
        }
        if (!Stalker.hasInstanceOf(player)) return;
        Stalker instance = Stalker.getInstanceOf(player);
        Entity stalker = instance.getStalker();
        if (stalker instanceof DroneStalkerEntity) {
            stalker.setXRot(xRot);
            stalker.setYRot(yRot);
            CompoundTag input = StalkerControl.handleInput();
            player.getPersistentData().put("DroneStalkerInput", input);
            StalkerControl.syncData();
        }
    }

    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide().isClient()) {
            Player player = event.getEntity();
            if (player.isShiftKeyDown()) return;
            if (event.getTarget() instanceof DroneStalkerEntity stalker) {
                Stalker.connect(player, stalker);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onControl(InputEvent.Key event) {
        if (Minecraft.getInstance().screen != null) return;
        Player player = Minecraft.getInstance().player;
        Options options = Minecraft.getInstance().options;
        if (!Stalker.hasInstanceOf(player)) return;
        if (event.getAction() == InputConstants.PRESS) {
            if (event.getKey() == KeyBindings.DISCONNECT.getKey().getValue()) {
                if (Stalker.hasInstanceOf(player)) Stalker.getInstanceOf(player).disconnect();
            }
        }
        ArrayList<Integer> controlKey = new ArrayList<>();
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

    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.RightClickBlock event) {
        System.out.print(event.getSide()+"\n");
        if (event.getSide().isClient() && Stalker.hasInstanceOf(event.getEntity())) {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getSide().isClient() && Stalker.hasInstanceOf(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onAction(InputEvent.MouseButton.Pre event) {
        if (Minecraft.getInstance().screen != null) return;
        if (event.getAction() != InputConstants.PRESS) return;
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_RIGHT) return;
        Player player = Minecraft.getInstance().player;
        if (!Stalker.hasInstanceOf(player)) return;
        Stalker instance = Stalker.getInstanceOf(player);
        if (instance.getStalker() instanceof DroneStalkerEntity) {
            BlockHitResult traceResult = Tools.rayTraceBlocks(player.level(), getCameraPosition(), getViewVector(), 4);
            if (traceResult.getType() == HitResult.Type.BLOCK) {
                NetworkHandler.CHANNEL.sendToServer(new RClickBlockPacket(getCameraPosition(), getViewVector()));
                RightClickBlock(player, getCameraPosition(), getViewVector());
            }
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onMouseScrolling(InputEvent.MouseScrollingEvent event) {
        if (Minecraft.getInstance().screen != null) return;
        Player player = Minecraft.getInstance().player;
        if (!Stalker.hasInstanceOf(player)) return;
        event.setCanceled(true);
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
        input.putFloat("xRot", xRot);
        input.putFloat("yRot", yRot);
        return input;
    }

    public static void syncData() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        NetworkHandler.CHANNEL.sendToServer(new EntityDataPacket(player.getId(), player.getPersistentData()));
    }

    public static void RightClickBlock(Player player, Vec3 position, Vec3 viewVec) {
        Level level = player.level();
        BlockHitResult traceResult = Tools.rayTraceBlocks(level, position, viewVec, 4);
        BlockState state = level.getBlockState(traceResult.getBlockPos());
        InteractionResult result = state.use(level, player, InteractionHand.MAIN_HAND, traceResult);
        if (result.consumesAction()) {
            level.sendBlockUpdated(traceResult.getBlockPos(), state, state, 3);
        }
    }

    public static void connect(Player player) {
        if (!player.isLocalPlayer()) return;
        fixedXRot = player.getXRot();
        fixedYRot = player.getYRot();
        xRot = fixedXRot;
        yRot = fixedYRot;
    }

    public static Vec3 getViewVector() {
        return Tools.calculateViewVector(xRot, yRot);
    }

    public static Vec3 getCameraPosition() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
    }

    @SubscribeEvent
    public static void onClientEnter(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide) return;
        Player player = Minecraft.getInstance().player;
        if (event.getEntity() instanceof DroneStalkerEntity stalker) {
            UUID entityUUID = Tools.uuidOfUsingStalkerMaster(player);
            if (stalker.getUUID().equals(entityUUID)) {
                Stalker.connect(player, stalker);
            }
        }
    }
}
