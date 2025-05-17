package com.mafuyu404.diligentstalker.mixin;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.init.VirtualInventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class)
public abstract class PlayerMixin {
    @Shadow @Final private Inventory inventory;

    @Shadow public abstract boolean isLocalPlayer();

    @Inject(method = "getInventory", at = @At("HEAD"), cancellable = true)
    private void wwa(CallbackInfoReturnable<Inventory> cir) {
        Player player = this.inventory.player;
        if (Stalker.hasInstanceOf(player) && !this.isLocalPlayer()) {
            if (Stalker.getInstanceOf(player).getStalker() instanceof DroneStalkerEntity stalker) {
                VirtualInventory virtualInventory = new VirtualInventory(this.inventory.getContainerSize(), player);
                for (int i = 0; i < stalker.getContainerSize(); i++) {
                    virtualInventory.setItem(i + 9, stalker.getItem(i));
                }
                cir.setReturnValue(virtualInventory);
            }
        }
    }
}
