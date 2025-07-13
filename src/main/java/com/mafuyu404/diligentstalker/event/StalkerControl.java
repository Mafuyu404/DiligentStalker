package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.entity.VoidStalkerEntity;
import com.mafuyu404.diligentstalker.init.ClientUtil;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.init.StalkerUtil;
import com.mafuyu404.diligentstalker.item.StalkerCoreItem;
import com.mafuyu404.diligentstalker.network.EntityDataPacket;
import com.mafuyu404.diligentstalker.network.RClickBlockPacket;
import com.mafuyu404.diligentstalker.registry.KeyBindings;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
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

import java.util.Map;
import java.util.UUID;

import static com.mafuyu404.diligentstalker.init.ClientUtil.getCameraPosition;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class StalkerControl {
    public static float fixedXRot, fixedYRot;
    public static float xRot, yRot;
    public static boolean screen = false;
    public static BlockPos visualCenter;

    public static void setVisualCenter(BlockPos blockPos) {
        if (Stalker.hasInstanceOf(Minecraft.getInstance().player)) return;
        visualCenter = blockPos;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        screen = Minecraft.getInstance().screen != null;
        if (screen) return;
        if (event.phase == TickEvent.Phase.START) return;
        LocalPlayer player = Minecraft.getInstance().player;
        UUID entityUUID = StalkerUtil.uuidOfUsingStalkerMaster(player);
        if (entityUUID != null) {
            ClientLevel level = player.clientLevel;
            level.entitiesForRendering().forEach(entity -> {
                if (entity.getUUID().equals(entityUUID)) {
                    Stalker.connect(player, entity);
                }
            });
        }
        Map.Entry<String, BlockPos> entry = StalkerUtil.entryOfUsingStalkerMaster(player);
        if (entry != null) {
            visualCenter = entry.getValue();
        }
        if (!Stalker.hasInstanceOf(player)) return;
        visualCenter = null;
        Stalker instance = Stalker.getInstanceOf(player);
        Entity stalker = instance.getStalker();
        if (stalker instanceof DroneStalkerEntity droneStalker) {
            stalker.setXRot(xRot);
            stalker.setYRot(yRot);
            StalkerControl.syncControl();
        }
    }

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide().isClient()) {
            Player player = event.getEntity();
            if (player.isShiftKeyDown()) return;
            if (event.getItemStack().getItem() instanceof StalkerCoreItem) {
                Stalker.connect(player, event.getTarget());
                event.setCanceled(true);
            } else if (event.getTarget() instanceof DroneStalkerEntity stalker) {
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
        updateControlMap();
        if (event.getAction() == InputConstants.PRESS) {
            if (event.getKey() == KeyBindings.DISCONNECT.getKey().getValue()) {
                if (Stalker.hasInstanceOf(player)) Stalker.getInstanceOf(player).disconnect();
            }
        }
        if (StalkerUtil.ControlMap.containsValue(event.getKey())) {
            syncControl();
        }
    }

    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.RightClickBlock event) {
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
        Player player = Minecraft.getInstance().player;
        if (!Stalker.hasInstanceOf(player)) return;
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            DroneStalkerHUD.RPress = event.getAction() == InputConstants.PRESS;
        }
        if (event.getAction() != InputConstants.PRESS) return;
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            event.setCanceled(true);
            return;
        }
        Stalker instance = Stalker.getInstanceOf(player);
        if (instance.getStalker() instanceof DroneStalkerEntity) {
            BlockHitResult traceResult = StalkerUtil.rayTraceBlocks(player.level(), getCameraPosition(), getViewVector(), 4);
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
        StalkerUtil.ControlMap.forEach((s, key) -> {
            input.putBoolean(s, ClientUtil.isKeyPressed(key));
        });
        input.putFloat("xRot", xRot);
        input.putFloat("yRot", yRot);
        return input;
    }

    public static void syncControl() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        updateControlMap();
        CompoundTag input = StalkerControl.handleInput();
        player.getPersistentData().put("DroneStalkerInput", input);
        NetworkHandler.CHANNEL.sendToServer(new EntityDataPacket(player.getId(), player.getPersistentData()));
    }

    public static void RightClickBlock(Player player, Vec3 position, Vec3 viewVec) {
        Level level = player.level();
        BlockHitResult traceResult = StalkerUtil.rayTraceBlocks(level, position, viewVec, 4);
        BlockState state = level.getBlockState(traceResult.getBlockPos());
        InteractionResult result = state.use(level, player, InteractionHand.MAIN_HAND, traceResult);
        if (result.consumesAction()) {
            level.sendBlockUpdated(traceResult.getBlockPos(), state, state, 3);
        }
    }

    public static void connect(Player player, Entity stalker) {
        if (!player.isLocalPlayer()) return;
        fixedXRot = player.getXRot();
        fixedYRot = player.getYRot();
        xRot = fixedXRot;
        yRot = fixedYRot;
        if (stalker instanceof DroneStalkerEntity) {
            xRot = stalker.getXRot();
            yRot = stalker.getYRot();
        }
    }

    @SubscribeEvent
    public static void onClientEnter(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (event.getEntity() instanceof DroneStalkerEntity stalker) {
            UUID entityUUID = StalkerUtil.uuidOfUsingStalkerMaster(player);
            if (stalker.getUUID().equals(entityUUID)) {
                Stalker.connect(player, stalker);
            }
        }
        if (event.getEntity() instanceof ArrowStalkerEntity stalker) {
            if (stalker.getOwner() != null && stalker.getOwner().getUUID().equals(player.getUUID())) {
                if (Stalker.hasInstanceOf(player)) return;
                Stalker.connect(player, stalker);
            }
        }
        if (event.getEntity() instanceof VoidStalkerEntity stalker) {
            if (stalker.getOwner() != null && stalker.getOwner().getUUID().equals(player.getUUID())) {
                if (Stalker.hasInstanceOf(player)) return;
                Stalker.connect(player, stalker);
            }
        }
    }

    private static void updateControlMap() {
        Options options = Minecraft.getInstance().options;
        StalkerUtil.ControlMap.put("Up", options.keyUp.getKey().getValue());
        StalkerUtil.ControlMap.put("Down", options.keyDown.getKey().getValue());
        StalkerUtil.ControlMap.put("Left", options.keyLeft.getKey().getValue());
        StalkerUtil.ControlMap.put("Right", options.keyRight.getKey().getValue());
        StalkerUtil.ControlMap.put("Jump", options.keyJump.getKey().getValue());
        StalkerUtil.ControlMap.put("Shift", options.keyShift.getKey().getValue());
    }

    public static Vec3 getViewVector() {
        return StalkerUtil.calculateViewVector(xRot, yRot);
    }
}
