package com.mafuyu404.diligentstalker.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class ServerStalkerUtil {
    public static void setVisualCenter(Player player, BlockPos blockPos) {
        if (blockPos.equals(BlockPos.ZERO)) player.getPersistentData().remove("visualCenter");
        else player.getPersistentData().putLong("visualCenter", blockPos.asLong());
    }

    public static boolean hasVisualCenter(Player player) {
        return player.getPersistentData().contains("visualCenter");
    }

    public static BlockPos getVisualCenter(Player player) {
        return BlockPos.of(player.getPersistentData().getLong("visualCenter"));
    }
}
