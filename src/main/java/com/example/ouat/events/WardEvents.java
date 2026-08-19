package com.example.ouat.events;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.WardSavedData;
import com.example.ouat.data.WardSavedData.WardedBuilding;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.joml.Vector3f;

import java.util.*;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class WardEvents {

    private static int tickCounter = 0;
    private static final Map<String, Set<BlockPos>> doorwayCache = new HashMap<>();

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
    public static void onServerTick(ServerTickEvent.Post event) {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        tickCounter++;
        if (tickCounter % 8 != 0) return;

        for (ServerLevel level : server.getAllLevels()) {
            WardSavedData wardData = WardSavedData.get(level);
            List<WardedBuilding> wards = wardData.getAllWards();

            String levelKey = level.dimension().location().toString();
            Set<BlockPos> allDoorways = doorwayCache.computeIfAbsent(levelKey, k -> new HashSet<>());

            Set<BlockPos> currentDoorways = new HashSet<>();
            for (WardedBuilding ward : wards) {
                currentDoorways.addAll(computeDoorways(level, ward));
            }

            allDoorways.retainAll(currentDoorways);
            allDoorways.addAll(currentDoorways);

            spawnShimmerParticles(level, allDoorways);
        }
    }

    private static Set<BlockPos> computeDoorways(ServerLevel level, WardedBuilding ward) {
        Set<BlockPos> doorways = new HashSet<>();
        for (BlockPos pos : ward.getInteriorAir()) {
            if (isExposedToOutside(level, pos, ward)) {
                doorways.add(pos);
            }
        }
        return doorways;
    }

    private static boolean isExposedToOutside(ServerLevel level, BlockPos pos, WardedBuilding ward) {
        for (BlockPos neighbor : List.of(
                pos.above(), pos.below(),
                pos.north(), pos.south(),
                pos.east(), pos.west())) {
            if (!ward.getInteriorAir().contains(neighbor) && level.getBlockState(neighbor).isAir()) {
                return true;
            }
        }
        return false;
    }

    private static void spawnShimmerParticles(ServerLevel level, Set<BlockPos> doorways) {
        for (BlockPos pos : doorways) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;

            // Golden-white shimmer — the OUAT protection barrier
            level.sendParticles(
                    new DustColorTransitionOptions(
                            new Vector3f(1.0f, 0.95f, 0.7f),
                            new Vector3f(1.0f, 0.85f, 0.4f), 1.5F),
                    x, y, z,
                    3, 0.3, 0.4, 0.3, 0.01);

            // Enchantment sparkles
            level.sendParticles(ParticleTypes.ENCHANT,
                    x, y, z,
                    5, 0.2, 0.3, 0.2, 0.5);

            // Subtle glow
            level.sendParticles(ParticleTypes.GLOW,
                    x, y + 0.5, z,
                    2, 0.1, 0.2, 0.1, 0.02);
        }
    }
}
