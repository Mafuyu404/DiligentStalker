package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.api.PersistentDataHolder;
import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.entity.VoidStalkerEntity;
import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.init.Tools;
import com.mafuyu404.diligentstalker.item.StalkerCoreItem;
import com.mafuyu404.diligentstalker.network.EntityDataPacket;
import com.mafuyu404.diligentstalker.network.RClickBlockPacket;
import com.mafuyu404.diligentstalker.registry.KeyBindings;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
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
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class StalkerControl {
    public static float fixedXRot, fixedYRot;
    public static float xRot, yRot;
    public static boolean screen = false;

    public static void init() {
        // 注册客户端Tick事件
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            onClientTick();
        });

        // 注册实体交互事件
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide()) {
                if (player.isShiftKeyDown()) return InteractionResult.PASS;
                if (player.getItemInHand(hand).getItem() instanceof StalkerCoreItem) {
                    Stalker.connect(player, entity);
                    return InteractionResult.SUCCESS;
                } else if (entity instanceof DroneStalkerEntity stalker) {
                    Stalker.connect(player, stalker);
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        });

        // 注册方块交互事件
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClientSide() && Stalker.hasInstanceOf(player)) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        // 注册实体加入世界事件
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!world.isClientSide()) return;
            Player player = Minecraft.getInstance().player;
            if (player == null) return;

            if (entity instanceof DroneStalkerEntity stalker) {
                UUID entityUUID = Tools.uuidOfUsingStalkerMaster(player);
                if (stalker.getUUID().equals(entityUUID)) {
                    Stalker.connect(player, stalker);
                }
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
        });

        // 注册鼠标按钮事件
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // 在每个tick检查鼠标按钮状态
            if (GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS) {
                handleMouseInput(GLFW.GLFW_MOUSE_BUTTON_RIGHT, InputConstants.PRESS);
            } else if (GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_RELEASE) {
                handleMouseInput(GLFW.GLFW_MOUSE_BUTTON_RIGHT, InputConstants.RELEASE);
            }
        });


    }

    private static void onClientTick() {
        screen = Minecraft.getInstance().screen != null;
        if (screen) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

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
            StalkerControl.syncControl();
        }

        // 处理键盘输入
        handleKeyInput();
    }

    private static void handleKeyInput() {
        Player player = Minecraft.getInstance().player;
        if (!Stalker.hasInstanceOf(player)) return;

        updateControlMap();

        // 检查断开连接键
        if (KeyBindings.DISCONNECT.isDown()) {
            if (Stalker.hasInstanceOf(player)) Stalker.getInstanceOf(player).disconnect();
        }

        // 检查移动键
        boolean shouldSync = false;
        for (int key : Tools.ControlMap.values()) {
            if (isKeyPressed(key)) {
                shouldSync = true;
                break;
            }
        }

        if (shouldSync) {
            syncControl();
        }
    }

    // 处理鼠标输入
    public static void handleMouseInput(int button, int action) {
        if (Minecraft.getInstance().screen != null) return;
        Player player = Minecraft.getInstance().player;
        if (!Stalker.hasInstanceOf(player)) return;

        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            DroneStalkerHUD.RPress = action == InputConstants.PRESS;
        }

        if (action != InputConstants.PRESS) return;
        if (button != GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            return;
        }

        Stalker instance = Stalker.getInstanceOf(player);
        if (instance.getStalker() instanceof DroneStalkerEntity) {
            BlockHitResult traceResult = Tools.rayTraceBlocks(player.level(), getCameraPosition(), getViewVector(), 4, player);
            if (traceResult.getType() == HitResult.Type.BLOCK) {
                NetworkHandler.sendToServer(new RClickBlockPacket(getCameraPosition(), getViewVector()));
                RightClickBlock(player, getCameraPosition(), getViewVector());
            }
        }
    }

    public static CompoundTag handleInput() {
        Options options = Minecraft.getInstance().options;
        CompoundTag input = new CompoundTag();
        Tools.ControlMap.forEach((s, key) -> {
            input.putBoolean(s, isKeyPressed(key));
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

        PersistentDataHolder holder = (PersistentDataHolder) player;
        holder.getPersistentData().put("DroneStalkerInput", input);
        NetworkHandler.sendToServer(new EntityDataPacket(player.getId(), holder.getPersistentData()));
    }

    public static void RightClickBlock(Player player, Vec3 position, Vec3 viewVec) {
        Level level = player.level();
        BlockHitResult traceResult = Tools.rayTraceBlocks(level, position, viewVec, 4, player);
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

    public static Vec3 getViewVector() {
        return Tools.calculateViewVector(xRot, yRot);
    }

    public static Vec3 getCameraPosition() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
    }

    private static void updateControlMap() {
        Options options = Minecraft.getInstance().options;
        Tools.ControlMap.put("Up", options.keyUp.key.getValue());
        Tools.ControlMap.put("Down", options.keyDown.key.getValue());
        Tools.ControlMap.put("Left", options.keyLeft.key.getValue());
        Tools.ControlMap.put("Right", options.keyRight.key.getValue());
        Tools.ControlMap.put("Jump", options.keyJump.key.getValue());
        Tools.ControlMap.put("Shift", options.keyShift.key.getValue());
    }

    public static boolean isKeyPressed(int glfwKeyCode) {
        Minecraft minecraft = Minecraft.getInstance();
        long windowHandle = minecraft.getWindow().getWindow();
        return GLFW.glfwGetKey(windowHandle, glfwKeyCode) == GLFW.GLFW_PRESS;
    }
}