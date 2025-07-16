package com.mafuyu404.diligentstalker.init;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class ServerUtil {
    public static void setVisualCenter(Player player, BlockPos blockPos) {
        player.getPersistentData().putLong("visualCenter", blockPos.asLong());
    }

    public static void clearVisualCenter(Player player) {
        player.getPersistentData().remove("visualCenter");
    }

    public static boolean hasVisualCenter(Player player) {
        return player.getPersistentData().contains("visualCenter");
    }

    public static BlockPos getVisualCenter(Player player) {
        return BlockPos.of(player.getPersistentData().getLong("visualCenter"));
    }
}
