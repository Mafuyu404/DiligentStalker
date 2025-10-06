package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.chunk.DiligentChunkManager;
import com.mafuyu404.diligentstalker.chunk.DiligentForcedChunksData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "prepareLevels", at = @At("TAIL"))
    private void diligent$reinstateForcedChunks(ChunkProgressListener chunkProgressListener, CallbackInfo ci) {
        MinecraftServer self = (MinecraftServer) (Object) this;
        for (ServerLevel level : self.getAllLevels()) {
            DiligentForcedChunksData data = level.getDataStorage().get(DiligentForcedChunksData.factory(), DiligentForcedChunksData.SAVE_ID);
            if (data != null) {
                DiligentChunkManager.reinstatePersistentChunks(level, data);
            }
        }
    }
}