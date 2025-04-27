package com.mafuyu404.diligentstalker.item;

import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArrowStalkerItem extends ArrowItem {
    public ArrowStalkerItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter) {
        ArrowStalkerEntity stalker = new ArrowStalkerEntity(shooter, level);
        stalker.pickup = AbstractArrow.Pickup.ALLOWED;
        return stalker;
    }
}
