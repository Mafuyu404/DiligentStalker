package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.event.ServerStalker;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @Shadow public abstract Entity getCamera();

    @Shadow @Nullable private Entity camera;

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;move(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private ServerPlayer oodaa(ServerPlayer player) {
        if (ServerStalker.getCameraEntity(player) != null) {
            if (ServerStalker.getCameraEntity(player) instanceof DroneStalkerEntity entity) {
                System.out.print("ddaaa\n");
                if (entity.fakePlayer != null) return entity.fakePlayer;
            }
        }
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
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void addd(CallbackInfo ci) {
        if (ServerStalker.getCameraEntity(this) != null) {
            this.camera = ServerStalker.getCameraEntity(this);
//            cir.setReturnValue(ServerStalker.getCameraEntity(this));
        }
    }
}
