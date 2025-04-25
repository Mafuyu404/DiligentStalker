package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.event.ServerStalker;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @Shadow public abstract Entity getCamera();

    @Shadow @Nullable private Entity camera;

    @Shadow @Final public ServerPlayerGameMode gameMode;

    @Shadow public abstract boolean isSpectator();

    @Shadow public abstract ServerLevel serverLevel();

    @Shadow public ServerGamePacketListenerImpl connection;

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;move(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private ServerPlayer oodaa(ServerPlayer player) {
//        if (ServerStalker.getCameraEntity(player) != null) {
//            if (ServerStalker.getCameraEntity(player) instanceof DroneStalkerEntity entity) {
////                System.out.print(entity.fakePlayer+"\n");
//                if (entity.fakePlayer != null) {
////                    System.out.print(entity.fakePlayer.position()+"\n");
//                    return entity.fakePlayer;
//                }
//            }
//        }
        return player;
    }
//    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getCamera()Lnet/minecraft/world/entity/Entity;"))
//    private Entity dddaaaa(ServerPlayer instance) {
//        if (ServerStalker.getCameraEntity(instance) != null) {
//            return ServerStalker.getCameraEntity(instance);
//        }
//        return instance.getCamera();
//    }
//    @Inject(method = "getCamera", at = @At("HEAD"), cancellable = true)
//    private void addd(CallbackInfoReturnable<Entity> cir) {
//        if (ServerStalker.getCameraEntity(this) != null) {
//            System.out.print(ServerStalker.getCameraEntity(this));
////            cir.setReturnValue(ServerStalker.getCameraEntity(this));
//        }
//    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void addd(CallbackInfo ci) {
        if (ServerStalker.getCameraEntity(this) != null) {
//            this.camera = ServerStalker.getCameraEntity(this);
//            this.setShiftKeyDown(false);
            if (ServerStalker.getCameraEntity(this) instanceof DroneStalkerEntity entity) {
                if (this.tickCount % 40 == 0) {
                    SectionPos sectionpos = SectionPos.of(entity);
                    this.connection.send(new ClientboundSetChunkCacheCenterPacket(sectionpos.x(), sectionpos.z()));
                    this.serverLevel().getChunkSource().move((ServerPlayer) entity.getMasterPlayer());
                }
            }
        }
//        else if (!this.isSpectator()) {
//            this.camera = null;
//        }
    }
    @Inject(method = "trackChunk", at = @At("HEAD"), cancellable = true)
    private void addda(ChunkPos p_184136_, Packet<?> p_184137_, CallbackInfo ci) {
//        System.out.print(p_184136_+"\n");
        if (this.getTags().contains("fake")) {
//            System.out.print(p_184137_+"\n");
//            System.out.print(this.camera);
            if (this.camera instanceof DroneStalkerEntity entity) {
                if (entity.getMasterPlayer() == null) return;
                if (entity.getMasterPlayer() instanceof ServerPlayer player) {
//                    System.out.print(player.serverLevel().getChunkSource().chunkMap.size()+"\n");
//                    player.connection.send(p_184137_);
                }
            }
//            cir.setReturnValue(ServerStalker.getCameraEntity(this));
        }
    }
}
