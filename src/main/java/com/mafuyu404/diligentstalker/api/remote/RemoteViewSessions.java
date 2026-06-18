package com.mafuyu404.diligentstalker.api.remote;

import com.mafuyu404.diligentstalker.init.Stalker;
import com.mafuyu404.diligentstalker.utils.ServerStalkerUtil;
import com.mafuyu404.diligentstalker.utils.StalkerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public final class RemoteViewSessions {
    private RemoteViewSessions() {
    }

    @Nullable
    public static RemoteViewSession get(ServerPlayer player) {
        Stalker stalker = Stalker.getInstanceOf(player);
        if (stalker != null) {
            Entity entity = stalker.getStalker();
            if (entity != null) {
                return new EntityRemoteViewSession(player, entity);
            }
        }

        if (ServerStalkerUtil.hasVisualCenter(player)) {
            return new FixedRemoteViewSession(player, ServerStalkerUtil.getVisualCenter(player));
        }

        return null;
    }

    public static boolean has(ServerPlayer player) {
        return get(player) != null;
    }

    private record EntityRemoteViewSession(ServerPlayer player, Entity stalkerEntity) implements RemoteViewSession {
        @Override
        public net.minecraft.resources.ResourceKey<Level> dimension() {
            return stalkerEntity.level().dimension();
        }

        @Override
        public BlockPos centerBlock() {
            return stalkerEntity.blockPosition();
        }

        @Override
        public int loadRadius() {
            return StalkerUtil.getLoadRadius(stalkerEntity, 0);
        }

        @Override
        public int renderRadius() {
            return StalkerUtil.getLoadRadius(stalkerEntity, 0);
        }

        @Override
        public RemoteViewMode mode() {
            return RemoteViewMode.ENTITY;
        }
    }

    private record FixedRemoteViewSession(ServerPlayer player, BlockPos centerBlock) implements RemoteViewSession {
        @Override
        public net.minecraft.resources.ResourceKey<Level> dimension() {
            return player.level().dimension();
        }

        @Override
        public int loadRadius() {
            return StalkerUtil.getFixedCenterRadius(0);
        }

        @Override
        public int renderRadius() {
            return StalkerUtil.getFixedCenterRadius(0);
        }

        @Override
        public RemoteViewMode mode() {
            return RemoteViewMode.FIXED_CENTER;
        }

        @Override
        public Entity stalkerEntity() {
            return null;
        }
    }
}
