package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.event.ChunkLoadTask;
import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientboundLevelChunkWithLightPacket.class)
public class ClientboundLevelChunkWithLightPacketMixin {
    @Inject(method = "handle(Lnet/minecraft/network/protocol/game/ClientGamePacketListener;)V", at = @At("HEAD"), cancellable = true)
    private void www(ClientGamePacketListener p_195716_, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (Stalker.hasInstanceOf(player)) {
            if (((Object) this) instanceof ClientboundLevelChunkWithLightPacket packet) {
                ChunkLoadTask.TaskList.add(packet);
                ci.cancel();
            }
        }
    }
}
