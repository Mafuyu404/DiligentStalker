package com.mafuyu404.diligentstalker.trash;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
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

    @Inject(method = "trackChunk", at = @At("HEAD"), cancellable = true)
    private void addda(ChunkPos p_184136_, Packet<?> p_184137_, CallbackInfo ci) {
        System.out.print(p_184136_+"\n");
    }
}
