package com.mafuyu404.diligentstalker.trash;

import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkRenderDispatcher.class)
public abstract class ChunkRenderDispatcherMixin {
//    @Inject(
//            method = "schedule",
//            at = @At("HEAD"),
//            cancellable = true
//    )
//    private void onScheduleChunkUpdate(ChunkRenderDispatcher.RenderChunk.ChunkCompileTask p_112710_, CallbackInfo ci) {
//        Minecraft client = Minecraft.getInstance();
//        if (client.level != null) {
//            for (Entity entity : client.level.entitiesForRendering()) {
//                if (entity instanceof DroneStalkerEntity) {
//                    ChunkPos entityChunk = entity.chunkPosition();
//                    if (Math.abs(pos.x - entityChunk.x) <= 5 &&
//                            Math.abs(pos.z - entityChunk.z) <= 5) {
//                        ci.cancel(); // 保持区块渲染状态
//                        return;
//                    }
//                }
//            }
//        }
//    }
}
