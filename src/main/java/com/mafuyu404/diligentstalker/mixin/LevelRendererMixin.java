package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.event.CameraEntityManage;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
//    @Shadow @Final private AtomicReference<LevelRenderer.RenderChunkStorage> renderChunkStorage;

    @ModifyArg(method = "initializeQueueForFullUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ViewArea;getRenderChunkAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk;", ordinal = 1))
    private BlockPos modify(BlockPos blockPos) {
        System.out.print(blockPos+"\n");
        return blockPos;
    }
    @ModifyArg(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;updateRenderChunks(Ljava/util/LinkedHashSet;Lnet/minecraft/client/renderer/LevelRenderer$RenderInfoMap;Lnet/minecraft/world/phys/Vec3;Ljava/util/Queue;Z)V"))
    private Queue sdsaaa(Queue queue) {
//        System.out.print(queue.size()+"\n");
        return queue;
    }
    @Inject(method = "setupRender", at = @At("HEAD"))
    private void sdsaaa(Camera p_194339_, Frustum p_194340_, boolean p_194341_, boolean p_194342_, CallbackInfo ci) {
//        System.out.print(p_194339_.getPosition()+"\n");
    }

    @Inject(method = "initializeQueueForFullUpdate", at = @At("RETURN"))
    private void modifdy(Camera p_194344_, Queue p_194345_, CallbackInfo ci) {
//        System.out.print(p_194345_.size()+"\n\n");
    }
    @Redirect(method = "updateRenderChunks", at = @At(value = "INVOKE", target = "Ljava/util/Queue;poll()Ljava/lang/Object;"))
    private Object aaad(Queue instance) {
        Object obj = instance.poll();
//        System.out.print(instance.size()+"\n");
        return obj;
    }
    @Redirect(method = "updateRenderChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;", ordinal = 0))
    private BlockPos oodsaa(BlockPos instance, int x, int y, int z) {
//        System.out.print(instance+"\n\n");
        BlockPos origin = instance.offset(x, y, z);
        if (CameraEntityManage.targetEntity != null) {
            ChunkPos center = CameraEntityManage.targetEntity.chunkPosition();
//            System.out.print(center.getMiddleBlockPosition(origin.getY()).offset(-8, -8, -8));
            return center.getMiddleBlockPosition(origin.getY()).offset(-8, -8, -8);
        }
        return new BlockPos(512, -64, 512);
//        return new BlockPos(center.x, instance.getY() + y, center.z);
    }
    @Redirect(method = "setupRender",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getX()D"))
    private double modifyX(LocalPlayer instance) {
        if (CameraEntityManage.targetEntity != null) {
            return CameraEntityManage.targetEntity.getX();
        }
        return instance.getX();
    }
    @Redirect(method = "setupRender",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getY()D"))
    private double modifyY(LocalPlayer instance) {
        if (CameraEntityManage.targetEntity != null) {
            return CameraEntityManage.targetEntity.getY();
        }
        return instance.getY();
    }
    @Redirect(method = "setupRender",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getZ()D"))
    private double modifyZ(LocalPlayer instance) {
        if (CameraEntityManage.targetEntity != null) {
            return CameraEntityManage.targetEntity.getZ();
        }
        return instance.getZ();
    }

    @Redirect(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ViewArea;repositionCamera(DD)V"))
    private void wwa(ViewArea instance, double x, double z) {
        if (CameraEntityManage.targetEntity != null) {
            instance.repositionCamera(CameraEntityManage.targetEntity.getX(), CameraEntityManage.targetEntity.getZ());
        }
        else instance.repositionCamera(x, z);
    }

    @Inject(method = "setupRender", at = @At("HEAD"))
    private void onSetupRender(Camera p_194339_, Frustum p_194340_, boolean p_194341_, boolean p_194342_, CallbackInfo ci) {
        if (CameraEntityManage.targetEntity != null) {
            ChunkPos center = CameraEntityManage.targetEntity.chunkPosition();
            for (int x = -5; x <= 5; x++) {
                for (int z = -5; z <= 5; z++) {
                    requestClientChunk(center.x + x, center.z + z);
                }
            }
        }
    }

    private void requestClientChunk(int x, int z) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            level.getChunk(x, z, ChunkStatus.FULL, true);
        }
    }
}
