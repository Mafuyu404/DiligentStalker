package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.utils.ServerStalkerUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public class ChunkMap$TrackedEntityMixin {

    @Redirect(
            method = "updatePlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;position()Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 redirectPosition(ServerPlayer player) {
        Vec3 original = player.position();
        Vec3 redirected;

        if (Stalker.hasInstanceOf(player)) {
            Entity stalker = Stalker.getInstanceOf(player).getStalker();
            redirected = stalker.position();
        } else if (ServerStalkerUtil.hasVisualCenter(player)) {
            redirected = ServerStalkerUtil.getVisualCenter(player).getCenter();
        } else {
            redirected = original;
        }


        return redirected;
    }
}
