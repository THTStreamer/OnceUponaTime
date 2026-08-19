package com.example.ouat.events;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.WardSavedData;
import com.example.ouat.data.WardSavedData.WardedBuilding;
import com.example.ouat.network.WardBoundaryPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
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

        if (ward.isAuthorized(serverPlayer.getUUID())) return;

        event.setCanceled(true);
        serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "§cThis area is protected by a Ward. Only the owner and authorized players can modify it."));
        serverPlayer.playSound(net.minecraft.sounds.SoundEvents.NOTE_BLOCK_BASS.value(), 1.0F, 0.5F);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.level() instanceof ServerLevel level) {
                syncWardBoundariesForPlayer(level, player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.level() instanceof ServerLevel level) {
                syncWardBoundariesForPlayer(level, player);
            }
        }
    }

    public static void syncWardBoundaries(ServerLevel level) {
        String dimKey = level.dimension().location().toString();
        WardSavedData wardData = WardSavedData.get(level);
        List<WardedBuilding> wards = wardData.getAllWards();

        if (wards.isEmpty()) return;

        List<WardBoundaryPacket.WardEntry> entries = buildEntries(wards);
        if (entries.isEmpty()) return;

        WardBoundaryPacket packet = new WardBoundaryPacket(dimKey, entries);
        for (ServerPlayer player : level.players()) {
            PacketDistributor.sendToPlayer(player, packet);
        }
    }

    private static void syncWardBoundariesForPlayer(ServerLevel level, ServerPlayer target) {
        String dimKey = level.dimension().location().toString();
        WardSavedData wardData = WardSavedData.get(level);
        List<WardedBuilding> wards = wardData.getAllWards();
        if (wards.isEmpty()) return;

        List<WardBoundaryPacket.WardEntry> entries = buildEntries(wards);
        if (!entries.isEmpty()) {
            PacketDistributor.sendToPlayer(target, new WardBoundaryPacket(dimKey, entries));
        }
    }

    private static List<WardBoundaryPacket.WardEntry> buildEntries(List<WardedBuilding> wards) {
        List<WardBoundaryPacket.WardEntry> entries = new ArrayList<>();
        for (WardedBuilding ward : wards) {
            Set<BlockPos> interior = ward.getInteriorAir();
            if (interior.isEmpty()) continue;

            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
            for (BlockPos pos : interior) {
                minX = Math.min(minX, pos.getX());
                minY = Math.min(minY, pos.getY());
                minZ = Math.min(minZ, pos.getZ());
                maxX = Math.max(maxX, pos.getX());
                maxY = Math.max(maxY, pos.getY());
                maxZ = Math.max(maxZ, pos.getZ());
            }

            entries.add(new WardBoundaryPacket.WardEntry(
                    minX, minY, minZ, maxX, maxY, maxZ, ward.getOwnerUUID(),
                    new ArrayList<>(interior)));
        }
        return entries;
    }
}
