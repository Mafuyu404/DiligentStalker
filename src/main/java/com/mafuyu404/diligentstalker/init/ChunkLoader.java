package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagVisitor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.world.ForgeChunkManager;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

public class ChunkLoader {
    private final ServerLevel level;
    public ChunkPos center;
    private final TicketType<ChunkPos> ticketType;
    private boolean active;

    public ChunkLoader(ServerLevel level, ChunkPos center) {
        this.level = level;
        this.center = center;
        this.ticketType = TicketType.PLAYER;
        this.active = false;
    }

    public void activate() {
        if (!active) {
            // 添加区块加载票据
            level.getChunkSource().addRegionTicket(ticketType, center, 33, center);
//            System.out.print(level.getChunkSource().getChunkDebugData(center));
            active = true;
        }
    }

    public void deactivate() {
        if (active) {
            // 移除区块加载票据
            level.getChunkSource().removeRegionTicket(ticketType, center, 33, center);
//            System.out.print(level.getChunkSource().getChunkDebugData(center));
            active = false;
        }
    }
}