package com.mafuyu404.diligentstalker.utils;

import com.mafuyu404.diligentstalker.data.ModComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class ServerStalkerUtil {
    public static void setVisualCenter(Player player, BlockPos blockPos) {
        if (blockPos.equals(BlockPos.ZERO))
            ModComponents.STALKER_DATA.get(player).getStalkerData().remove("visualCenter");
        else ModComponents.STALKER_DATA.get(player).getStalkerData().putLong("visualCenter", blockPos.asLong());
    }

    public static boolean hasVisualCenter(Player player) {
        return ModComponents.STALKER_DATA.get(player).getStalkerData().contains("visualCenter");
    }

    public static BlockPos getVisualCenter(Player player) {
        return BlockPos.of(ModComponents.STALKER_DATA.get(player).getStalkerData().getLong("visualCenter"));
    }
}
