package com.mafuyu404.diligentstalker.utils;

import com.mafuyu404.diligentstalker.data.ModLookupApi;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class ServerStalkerUtil {
    public static void setVisualCenter(Player player, BlockPos blockPos) {
        var data = ModLookupApi.STALKER_DATA.find(player, null);
        if (data == null) return;
        if (blockPos.equals(BlockPos.ZERO)) {
            data.getData().remove("visualCenter");
        } else {
            data.getData().putLong("visualCenter", blockPos.asLong());
        }
    }

    public static boolean hasVisualCenter(Player player) {
        var data = ModLookupApi.STALKER_DATA.find(player, null);
        return data != null && data.getData().contains("visualCenter");
    }

    public static BlockPos getVisualCenter(Player player) {
        var data = ModLookupApi.STALKER_DATA.find(player, null);
        return BlockPos.of(data != null ? data.getData().getLong("visualCenter") : BlockPos.ZERO.asLong());
    }
}
