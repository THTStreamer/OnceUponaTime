package com.example.ouat.events;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.ConcealmentSavedData;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.spells.ConcealSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.DoorBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ConcealmentEvents {

    private static final Map<UUID, Long> TELEPORT_COOLDOWN = new HashMap<>();

    @SubscribeEvent
    public static void onBlockInteract(UseItemOnBlockEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (event.getHand() != net.minecraft.world.InteractionHand.MAIN_HAND) return;

        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);

        if (data.isConcealReady()) {
            BlockPos pos = event.getPos();
            net.minecraft.world.level.block.state.BlockState state = event.getLevel().getBlockState(pos);

            if (state.getBlock() instanceof DoorBlock) {
                event.cancelWithResult(net.minecraft.world.ItemInteractionResult.sidedSuccess(false));
                ConcealSpell.handleDoorClick(player, (ServerLevel) event.getLevel(), pos);
                return;
            }
        }

        BlockPos pos = event.getPos();
        ConcealmentSavedData roomData = ConcealmentSavedData.get((ServerLevel) event.getLevel());
        ConcealmentSavedData.ConcealedRoom room = roomData.findRoomAt(pos);

        if (room == null || !room.getOwnerUUID().equals(player.getUUID())) return;

        long now = event.getLevel().getGameTime();
        Long lastTeleport = TELEPORT_COOLDOWN.get(player.getUUID());
        if (lastTeleport != null && now - lastTeleport < 10) return;
        TELEPORT_COOLDOWN.put(player.getUUID(), now);

        BlockPos tele = ConcealSpell.getTeleportTarget(player, (ServerLevel) event.getLevel(), room);
        if (tele == null) return;

        event.cancelWithResult(net.minecraft.world.ItemInteractionResult.sidedSuccess(false));

        player.teleportTo(tele.getX() + 0.5, tele.getY(), tele.getZ() + 0.5);
        player.playSound(SoundEvents.ENDER_EYE_LAUNCH, 1.0F, 1.5F);
    }
}
