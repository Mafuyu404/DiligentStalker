package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.component.ModComponents;
import com.mafuyu404.diligentstalker.utils.ControllableUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record EntityDataPacket(int entityId, CompoundTag nbt) implements Packet {
    public static final Type<EntityDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DiligentStalker.MODID, "entity_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EntityDataPacket::entityId,
            StreamCodec.of(
                    (buf, nbt) -> buf.writeNbt(nbt),
                    buf -> buf.readNbt()
            ), EntityDataPacket::nbt,
            EntityDataPacket::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Packet> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, EntityDataPacket msg) {
        server.execute(() -> {
            Level level = player.level();
            Entity entity = level.getEntity(msg.entityId);
            if (entity == null) return;
            ModComponents.STALKER_DATA.get(entity).getStalkerData().merge(msg.nbt);
            ModComponents.STALKER_DATA.get(entity).getStalkerData().put(ControllableUtils.CONTROL_INPUT_KEY, msg.nbt.get(ControllableUtils.CONTROL_INPUT_KEY));
        });
    }
}