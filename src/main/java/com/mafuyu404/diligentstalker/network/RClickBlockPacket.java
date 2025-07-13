package com.mafuyu404.diligentstalker.network;

import com.mafuyu404.diligentstalker.event.StalkerControl;
import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RClickBlockPacket {
    private final Vec3 position;
    private final Vec3 viewVec;

    public RClickBlockPacket(Vec3 position, Vec3 viewVec) {
        this.position = position;
        this.viewVec = viewVec;
    }

    public static void encode(RClickBlockPacket msg, FriendlyByteBuf buffer) {
        buffer.writeVector3f(msg.position.toVector3f());
        buffer.writeVector3f(msg.viewVec.toVector3f());
    }

    public static RClickBlockPacket decode(FriendlyByteBuf buffer) {
        return new RClickBlockPacket(new Vec3(buffer.readVector3f()), new Vec3(buffer.readVector3f()));
    }

    public static void handle(RClickBlockPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            ServerLevel level = player.serverLevel();
            if (!Stalker.hasInstanceOf(player)) return;
//            Entity entity = Stalker.getInstanceOf(player).getStalker();
//            if (entity == null) return;

            StalkerControl.RightClickBlock(player, msg.position, msg.viewVec);

//            BlockState state = level.getBlockState(msg.blockPos);
//            Vec3 hitVec = new Vec3(msg.blockPos.getX() + 0.5, msg.blockPos.getY() + 0.5, msg.blockPos.getZ() + 0.5);
//            BlockHitResult traceResult = StalkerUtil.rayTraceBlocks(level, entity.position(), msg.lookAngle, 4);
//            BlockHitResult hitResult = new BlockHitResult(hitVec, traceResult.getDirection(), msg.blockPos, false);
//
////            PlayerInteractEvent.RightClickBlock event = new PlayerInteractEvent.RightClickBlock(
////                    player,
////                    InteractionHand.MAIN_HAND,
////                    msg.blockPos,
////                    hitResult
////            );
////            if (MinecraftForge.EVENT_BUS.post(event)) {
////                return;
////            }
////            if (event.getResult() == Event.Result.DENY) return;
//
//            InteractionResult result = state.use(level, player, InteractionHand.MAIN_HAND, traceResult);
//            if (result.consumesAction()) {
//                level.sendBlockUpdated(msg.blockPos, state, state, 3);
//            }
        });
        ctx.get().setPacketHandled(true);
    }
}
