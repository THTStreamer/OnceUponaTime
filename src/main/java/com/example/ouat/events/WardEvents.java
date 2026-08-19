package com.example.ouat.events;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.WardSavedData;
import com.example.ouat.data.WardSavedData.WardedBuilding;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class WardEvents {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        BlockPos pos = event.getPos();
        WardSavedData wardData = WardSavedData.get(level);
        WardedBuilding ward = wardData.findWardAt(pos);

        if (ward == null) return;

        Player player = event.getPlayer();
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        // Owner or authorized player can break
        if (ward.isAuthorized(serverPlayer.getUUID())) return;

        // Block the break
        event.setCanceled(true);
        serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "§cThis area is protected by a Ward. Only the owner and authorized players can modify it."));
        serverPlayer.playSound(net.minecraft.sounds.SoundEvents.NOTE_BLOCK_BASS.value(), 1.0F, 0.5F);
    }
}
