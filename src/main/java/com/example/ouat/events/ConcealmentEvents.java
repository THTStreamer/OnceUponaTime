package com.example.ouat.events;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.ConcealmentSavedData;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.spells.ConcealSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.DoorBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ConcealmentEvents {

    @SubscribeEvent
    public static void onBlockInteract(UseItemOnBlockEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (event.getHand() != net.minecraft.world.InteractionHand.MAIN_HAND) return;

        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!data.isConcealReady()) return;

        BlockPos pos = event.getPos();
        net.minecraft.world.level.block.state.BlockState state = event.getLevel().getBlockState(pos);

        if (state.getBlock() instanceof DoorBlock) {
            event.cancelWithResult(net.minecraft.world.ItemInteractionResult.sidedSuccess(false));
            ConcealSpell.handleDoorClick(player, (ServerLevel) event.getLevel(), pos);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (data.isConcealReady()) return;

        BlockPos playerPos = player.blockPosition();
        ConcealmentSavedData roomData = ConcealmentSavedData.get((ServerLevel) player.level());
        ConcealmentSavedData.ConcealedRoom room = roomData.findRoomAt(playerPos);

        if (room == null || !room.getOwnerUUID().equals(player.getUUID())) return;

        BlockPos tele = ConcealSpell.getTeleportTarget(player, (ServerLevel) player.level(), room);
        if (tele == null) return;

        player.teleportTo(tele.getX() + 0.5, tele.getY(), tele.getZ() + 0.5);
        player.playSound(net.minecraft.sounds.SoundEvents.ENDER_EYE_LAUNCH, 1.0F, 1.5F);
    }
}
