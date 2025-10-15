package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.PlayerChunkSender;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerChunkSender.class)
public class PlayerChunkSenderMixin {

    @Redirect(
        method = "sendNextChunks",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;chunkPosition()Lnet/minecraft/world/level/ChunkPos;")
    )
    private ChunkPos ds$useStalkerViewCenterForSend(ServerPlayer player) {
        if (Stalker.hasInstanceOf(player)) {
            return Stalker.getInstanceOf(player).getStalker().chunkPosition();
        }
        return player.chunkPosition();
    }
}