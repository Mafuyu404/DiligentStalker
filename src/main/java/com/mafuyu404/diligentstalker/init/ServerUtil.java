package com.mafuyu404.diligentstalker.init;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class ServerUtil {
    public static void setVisualCenter(Player player, BlockPos blockPos) {
        player.getPersistentData().putLong("visualCenter", blockPos.asLong());
    }
}
