package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.event.ServerStalker;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.entity.EntityAccess;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = ChunkMap.class)
public abstract class ChunkMapMixin {
    @Shadow
    private static double euclideanDistanceSquared(ChunkPos p_140227_, Entity p_140228_) {
        return 0;
    }

    @Shadow protected abstract void updateChunkTracking(ServerPlayer p_183755_, ChunkPos p_183756_, MutableObject<ClientboundLevelChunkWithLightPacket> p_183757_, boolean p_183758_, boolean p_183759_);

    @Shadow @Final private ServerLevel level;

    @Shadow @Nullable protected abstract ChunkHolder getVisibleChunkIfPresent(long p_140328_);

    @Shadow protected abstract void playerLoadedChunk(ServerPlayer p_183761_, MutableObject<ClientboundLevelChunkWithLightPacket> p_183762_, LevelChunk p_183763_);

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;of(Lnet/minecraft/world/level/entity/EntityAccess;)Lnet/minecraft/core/SectionPos;"))
    private SectionPos ssa(EntityAccess p_235862_) {
        if (ServerStalker.getCameraEntity((Player) p_235862_) != null) {
            return SectionPos.of(ServerStalker.getCameraEntity((Player) p_235862_).blockPosition());
        }
        return SectionPos.of(p_235862_);
    }
    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getLastSectionPos()Lnet/minecraft/core/SectionPos;"))
    private SectionPos ssaa(ServerPlayer instance) {
        if (ServerStalker.getCameraEntity(instance) != null) {
            return SectionPos.of(ServerStalker.getCameraEntity(instance).blockPosition());
        }
        return instance.getLastSectionPos();
    }
    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getBlockX()I"))
    private int ssaax(ServerPlayer instance) {
        if (ServerStalker.getCameraEntity(instance) != null) {
            return ServerStalker.getCameraEntity(instance).getBlockX();
        }
        return instance.getBlockX();
    }
    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getBlockZ()I"))
    private int ssaaz(ServerPlayer instance) {
        if (ServerStalker.getCameraEntity(instance) != null) {
            return ServerStalker.getCameraEntity(instance).getBlockZ();
        }
        return instance.getBlockZ();
    }
    @Redirect(
            method = "updatePlayerPos",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/SectionPos;of(Lnet/minecraft/world/level/entity/EntityAccess;)Lnet/minecraft/core/SectionPos;"
            )
    )
    private SectionPos redirectSectionPosOf(EntityAccess p_235862_) {
        if (ServerStalker.getCameraEntity((Player) p_235862_) != null) {
            return SectionPos.of(ServerStalker.getCameraEntity((Player) p_235862_).blockPosition());
        }
        return SectionPos.of(p_235862_);
    }
    @Redirect(
            method = "playerIsCloseEnoughForSpawning",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;euclideanDistanceSquared(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/entity/Entity;)D"
            )
    )
    private double redirectEuclideanDistance(ChunkPos chunkPos, Entity entity) {
        if (ServerStalker.getCameraEntity((Player) entity) != null) {
            return euclideanDistanceSquared(chunkPos, ServerStalker.getCameraEntity((Player) entity));
        }
        return euclideanDistanceSquared(chunkPos, entity);
    }
//    @ModifyVariable(
//            method = "updateChunkTracking",
//            at = @At("HEAD"),
//            argsOnly = true
//    )
//    private ChunkPos modifyChunkPos(ChunkPos original, ServerPlayer player) {
//        if (ServerStalker.getCameraEntity(player) != null) {
//            return new ChunkPos(ServerStalker.getCameraEntity(player).blockPosition());
//        }
//        return original;
//    }
    @Inject(method = "move", at = @At("RETURN"))
    private void wwaaa(ServerPlayer p_140185_, CallbackInfo ci) {
        if (ServerStalker.getCameraEntity(p_140185_) != null) {
            ChunkPos center = new ChunkPos(ServerStalker.getCameraEntity(p_140185_).blockPosition());
//            System.out.print(center+"\n");
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
//                    System.out.print(x+"/"+z+"\n");
//                    this.updateChunkTracking(p_140185_, new ChunkPos(center.x + x, center.z + z), new MutableObject<>(), true, true);
                    ChunkHolder chunkholder = this.getVisibleChunkIfPresent(new ChunkPos(center.x + x, center.z + z).toLong());
                    if (chunkholder != null) {
                        LevelChunk levelchunk = chunkholder.getTickingChunk();
                        if (levelchunk != null) {
//                            System.out.print(levelchunk.getPos()+"\n");
                            this.playerLoadedChunk(p_140185_, new MutableObject<>(), levelchunk);
                        }
                    }
//                    this.playerLoadedChunk(p_140185_, new MutableObject<>(), );
                }
            }
        }
    }
    @Inject(method = "updateChunkTracking", at = @At("HEAD"))
    private void wwaaa(ServerPlayer p_183755_, ChunkPos p_183756_, MutableObject<ClientboundLevelChunkWithLightPacket> p_183757_, boolean p_183758_, boolean p_183759_, CallbackInfo ci) {
//        System.out.print((p_183755_.level() == this.level)+"/"+(p_183759_ && !p_183758_)+"/");
//        ChunkHolder chunkholder = this.getVisibleChunkIfPresent(p_183756_.toLong());
//        if (chunkholder != null) {
//            LevelChunk levelchunk = chunkholder.getTickingChunk();
//            System.out.print(levelchunk != null);
//        }
//        System.out.print("\n");
    }
    @Inject(method = "updateChunkTracking", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;playerLoadedChunk(Lnet/minecraft/server/level/ServerPlayer;Lorg/apache/commons/lang3/mutable/MutableObject;Lnet/minecraft/world/level/chunk/LevelChunk;)V"))
    private void aaw(ServerPlayer p_183755_, ChunkPos p_183756_, MutableObject<ClientboundLevelChunkWithLightPacket> p_183757_, boolean p_183758_, boolean p_183759_, CallbackInfo ci) {
//        System.out.print(p_183756_+"\n");
    }

    @Inject(method = "removeEntity", at = @At("HEAD"))
    private void wwwaxxx(Entity p_140332_, CallbackInfo ci) {
        System.out.print(p_140332_+"\n");
    }
}
