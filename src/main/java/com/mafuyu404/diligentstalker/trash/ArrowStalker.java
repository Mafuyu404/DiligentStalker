package com.mafuyu404.diligentstalker.trash;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArrowStalker {
//    @SubscribeEvent
//    public static void onEntitySpawn(EntityJoinLevelEvent event) {
//        Entity entity = event.getEntity();
//
//        // 检查实体是否为光灵箭
//        if (entity instanceof SpectralArrow arrow) {
//            if (arrow.getOwner() instanceof Player player) {
//                if (!player.isLocalPlayer()) return;
//                if (Stalker.hasInstanceOf(player)) return;
//                Stalker.connect(player, arrow);
//            }
//        }
//    }
//    @SubscribeEvent
//    public static void onArrowShoot(ProjectileImpactEvent event) {
//        if (event.getProjectile() instanceof ArrowStalkerEntity arrow) {
//            if (arrow.getOwner() instanceof Player player) {
//                if (player.getUseItem().getItem() == Items.BOW) {
//                    arrow.pickup = AbstractArrow.Pickup.ALLOWED;
//                    arrow.setPos(arrow.getX(), arrow.getY(), arrow.getZ());
//                }
//            }
//        }
//    }
}
