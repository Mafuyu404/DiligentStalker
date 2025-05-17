package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.init.Tools;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public class ChunkMap$TrackedEntityMixin {
    @Redirect(method = "updatePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;position()Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 wwa(ServerPlayer player) {
        if (Stalker.hasInstanceOf(player)) {
            Entity stalker = Stalker.getInstanceOf(player).getStalker();
            return stalker.position();
        } else {
            Map.Entry<String, BlockPos> entry = Tools.entryOfUsingStalkerMaster(player);
            if (entry != null) {
                return entry.getValue().getCenter();
            }
        }
        return player.position();
    }
}
