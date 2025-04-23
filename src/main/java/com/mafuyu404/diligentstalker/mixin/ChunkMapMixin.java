package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.event.ServerStalker;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChunkMap.class)
public abstract class ChunkMapMixin {
    @Shadow
    private static double euclideanDistanceSquared(ChunkPos p_140227_, Entity p_140228_) {
        return 0;
    }

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
    @ModifyVariable(
            method = "updateChunkTracking",
            at = @At("HEAD"),
            argsOnly = true
    )
    private ChunkPos modifyChunkPos(ChunkPos original, ServerPlayer player) {
        if (ServerStalker.getCameraEntity(player) != null) {
            return new ChunkPos(ServerStalker.getCameraEntity(player).blockPosition());
        }
        return original;
    }
}
