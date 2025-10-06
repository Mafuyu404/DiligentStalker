package com.mafuyu404.diligentstalker.event.handler;

import com.mafuyu404.diligentstalker.api.IControllable;
import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.entity.VoidStalkerEntity;
import com.mafuyu404.diligentstalker.event.client.MouseCallbacks;
import com.mafuyu404.diligentstalker.data.ModComponents;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.item.StalkerCoreItem;
import com.mafuyu404.diligentstalker.network.EntityDataPacket;
import com.mafuyu404.diligentstalker.network.RClickBlockPacket;
import com.mafuyu404.diligentstalker.registry.KeyBindings;
import com.mafuyu404.diligentstalker.utils.ClientStalkerUtil;
import com.mafuyu404.diligentstalker.utils.ControllableUtils;
import com.mafuyu404.diligentstalker.utils.StalkerUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import static com.mafuyu404.diligentstalker.utils.ClientStalkerUtil.getCameraPosition;

public class StalkerControl {
    public static float fixedXRot, fixedYRot;
    public static float xRot, yRot;
    public static boolean screen = false;

    public static void initClientEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> onClientTick());
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> onInteract(player, hand, entity));
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> onUseBlock(player));
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> onAttackBlock(player));
        MouseCallbacks.MOUSE_BUTTON_EVENT.register(StalkerControl::onAction);
        MouseCallbacks.MOUSE_SCROLL_EVENT.register(StalkerControl::onMouseScrolling);
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> onClientEnter(entity));
    }

    public static void onClientTick() {
        screen = Minecraft.getInstance().screen != null;
        if (screen) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ClientLevel level = player.clientLevel;
        for (Entity entity : level.entitiesForRendering()) {
            if (ClientStalkerUtil.matchConnectingTarget(entity)) {
                Stalker.connect(player, entity);
                ClientStalkerUtil.setConnectingTarget(null);
                break;
            }
        }

        Entity stalker = ClientStalkerUtil.getLocalStalker();
        if (stalker != null) {
            ClientStalkerUtil.setVisualCenter(null);
    
            if (ControllableUtils.isControllable(stalker)) {
                if (ControllableUtils.isCameraControlling(stalker)) {
                    xRot = ClientStalkerUtil.getCameraXRot();
                    yRot = ClientStalkerUtil.getCameraYRot();
                    stalker.setXRot(xRot);
                    stalker.setYRot(yRot);
                }
                StalkerControl.syncControl();
            }
        }

        if (KeyBindings.DISCONNECT.isDown()) {
            Player p = Minecraft.getInstance().player;
            if (Stalker.hasInstanceOf(p))
                Stalker.getInstanceOf(p).disconnect();
        }
        if (KeyBindings.VIEW.isDown()) {
            Entity s = ClientStalkerUtil.getLocalStalker();
            if (s != null) ControllableUtils.switchCameraState(s);
        }
        if (KeyBindings.CONTROL.isDown()) {
            Entity s = ClientStalkerUtil.getLocalStalker();
            if (s != null) ControllableUtils.turnActionControlling(s);
        }
        if (StalkerUtil.ControlMap.containsValue(KeyBindings.CONTROL.key.getValue())) {
            syncControl();
        }
    }

    public static InteractionResult onInteract(Player player, InteractionHand hand, Entity target) {
        if (Minecraft.getInstance().isPaused()) return InteractionResult.PASS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (player.level().isClientSide) {
            if (player.getItemInHand(hand).getItem() instanceof StalkerCoreItem) {
                Stalker.connect(player, target);
                return InteractionResult.SUCCESS;
            } else if (target instanceof DroneStalkerEntity stalker) {
                Stalker.connect(player, stalker);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }


    public static InteractionResult onUseBlock(Player player) {
        if (player.level().isClientSide && Stalker.hasInstanceOf(player)) return InteractionResult.FAIL;
        return InteractionResult.PASS;
    }


    public static InteractionResult onAttackBlock(Player player) {
        return onUseBlock(player);
    }


    public static boolean onAction(int button, int action) {
        if (Minecraft.getInstance().screen != null) return false;
        Player player = Minecraft.getInstance().player;
        if (!Stalker.hasInstanceOf(player)) return false;
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            DroneStalkerHUD.RPress = action == InputConstants.PRESS;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            DroneStalkerHUD.LPress = action == InputConstants.PRESS;
        }
        if (action != InputConstants.PRESS) return false;
        if (button != GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            return true;
        }
        Stalker instance = Stalker.getInstanceOf(player);
        if (ControllableUtils.isControllable(instance.getStalker())) {
            Vec3 viewVector = StalkerUtil.calculateViewVector(xRot, yRot);
            BlockHitResult traceResult = StalkerUtil.rayTraceBlocks(player.level(), player, getCameraPosition(), viewVector, 4);
            if (traceResult.getType() == HitResult.Type.BLOCK) {
                NetworkHandler.sendToServer(NetworkHandler.RCLICK_BLOCK_PACKET, new RClickBlockPacket(getCameraPosition(), viewVector));
                RightClickBlock(player, getCameraPosition(), viewVector);
            }
        }
        return true;
    }


    private static boolean onMouseScrolling(double xOffset, double yOffset) {
        if (Minecraft.getInstance().screen != null) return false;
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;
        return Stalker.hasInstanceOf(player);
    }

    public static CompoundTag handleInput() {
        CompoundTag input = new CompoundTag();
        StalkerUtil.ControlMap.forEach((s, key) -> {
            input.putBoolean(s, ClientStalkerUtil.isKeyPressed(key));
        });
        input.putFloat("xRot", ClientStalkerUtil.getCameraXRot());
        input.putFloat("yRot", ClientStalkerUtil.getCameraYRot());
        return input;
    }

    public static void syncControl() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        updateControlMap();
        CompoundTag input = StalkerControl.handleInput();
        Entity stalker = ClientStalkerUtil.getLocalStalker();
        if (ControllableUtils.isControllable(stalker)) {
            ((IControllable) stalker).pushAdditionalControl(input);
            if (!ControllableUtils.isActionControlling(stalker)) {
                CompoundTag minimal = new CompoundTag();
                minimal.putFloat("xRot", input.getFloat("xRot"));
                minimal.putFloat("yRot", input.getFloat("yRot"));
                input = minimal;
            }
        }
        ModComponents.STALKER_DATA.get(player).getStalkerData().put(ControllableUtils.CONTROL_INPUT_KEY, input);
        NetworkHandler.sendToServer(NetworkHandler.ENTITY_DATA_PACKET, new EntityDataPacket(player.getId(), ModComponents.STALKER_DATA.get(player).getStalkerData()));
    }

    public static void RightClickBlock(Player player, Vec3 position, Vec3 viewVec) {
        player.displayClientMessage(Component.translatable("message.diligentstalker.under_maintaining").withStyle(ChatFormatting.RED), true);
//        Level level = player.level();
//        BlockHitResult traceResult = StalkerUtil.rayTraceBlocks(level, position, viewVec, 4);
//        BlockState state = level.getBlockState(traceResult.getBlockPos());
//        InteractionResult result = state.use(level, player, InteractionHand.MAIN_HAND, traceResult);
//        if (result.consumesAction()) {
//            level.sendBlockUpdated(traceResult.getBlockPos(), state, state, 3);
//        }
    }

    public static void connect(Player player, Entity stalker) {
        if (!player.isLocalPlayer()) return;
        fixedXRot = player.getXRot();
        fixedYRot = player.getYRot();
        xRot = fixedXRot;
        yRot = fixedYRot;
        if (ControllableUtils.isControllable(stalker)) {
            xRot = ClientStalkerUtil.getCameraXRot();
            yRot = ClientStalkerUtil.getCameraYRot();
            ControllableUtils.setCameraControlling(stalker);
        }
    }


    public static void onClientEnter(Entity entity) {
        if (!Minecraft.getInstance().isRunning()) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (ClientStalkerUtil.matchConnectingTarget(entity)) {
            Stalker.connect(player, entity);
        }

        if (entity instanceof ArrowStalkerEntity stalker) {
            if (stalker.getOwner() != null && stalker.getOwner().getUUID().equals(player.getUUID())) {
                if (Stalker.hasInstanceOf(player)) return;
                Stalker.connect(player, stalker);
            }
        }
        if (entity instanceof VoidStalkerEntity stalker) {
            if (stalker.getOwner() != null && stalker.getOwner().getUUID().equals(player.getUUID())) {
                if (Stalker.hasInstanceOf(player)) return;
                Stalker.connect(player, stalker);
            }
        }
    }

    private static void updateControlMap() {
        Options options = Minecraft.getInstance().options;
        StalkerUtil.ControlMap.put("Up", options.keyUp.key.getValue());
        StalkerUtil.ControlMap.put("Down", options.keyDown.key.getValue());
        StalkerUtil.ControlMap.put("Left", options.keyLeft.key.getValue());
        StalkerUtil.ControlMap.put("Right", options.keyRight.key.getValue());
        StalkerUtil.ControlMap.put("Jump", options.keyJump.key.getValue());
        StalkerUtil.ControlMap.put("Shift", options.keyShift.key.getValue());
    }
}