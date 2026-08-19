package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.WardSavedData;
import com.example.ouat.data.WardSavedData.WardedBuilding;
import com.example.ouat.events.WardEvents;
import com.example.ouat.magic.Spell;
import com.example.ouat.magic.Spell.MagicType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.*;

public class WardSpell extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "ward");

    private static final int MAX_WARD_SIZE = 256;
    private static final int MIN_WARD_SIZE = 2;

    public WardSpell() {
        super(ID, "Ward", 25, 10, MagicType.LIGHT);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough. Requires proficiency 25."));
            return false;
        }
        if (!consumeFood(player, 10)) return false;

        ServerLevel level = player.serverLevel();

        HitResult hitResult = player.pick(10.0, 0.0F, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cLook at a doorway or entrance to set the ward."));
            return false;
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos hitPos = blockHit.getBlockPos();
        BlockState hitState = level.getBlockState(hitPos);

        List<BlockPos> candidates = new ArrayList<>();
        if (hitState.isAir()) {
            candidates.add(hitPos);
        }
        for (BlockPos neighbor : List.of(
                hitPos.above(), hitPos.below(),
                hitPos.north(), hitPos.south(),
                hitPos.east(), hitPos.west())) {
            if (level.getBlockState(neighbor).isAir()) {
                candidates.add(neighbor);
            }
        }

        if (candidates.isEmpty()) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cLook at an open doorway or entrance (need an air block nearby)."));
            return false;
        }

        BlockPos playerEye = BlockPos.containing(player.getEyePosition());
        BlockPos playerFeet = player.blockPosition();

        BlockPos doorPos = null;
        Set<BlockPos> bestInterior = null;
        double bestScore = -Double.MAX_VALUE;

        for (BlockPos candidate : candidates) {
            Set<BlockPos> interior = floodFillAir(level, candidate, MAX_WARD_SIZE);
            if (interior.size() < MIN_WARD_SIZE) continue;

            double density = (double) computeEnclosure(level, interior) / interior.size();
            boolean playerInside = interior.contains(playerEye) || interior.contains(playerFeet);

            double score;
            if (playerInside) {
                score = 1000.0 + density;
            } else {
                score = density;
            }

            if (score > bestScore) {
                bestScore = score;
                bestInterior = interior;
                doorPos = candidate;
            }
        }

        if (doorPos == null || bestInterior == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cInterior too small. Needs at least " + MIN_WARD_SIZE + " air blocks."));
            return false;
        }

        Set<BlockPos> interiorAir = bestInterior;
        Set<BlockPos> protectedPositions = computeProtectedPositions(level, interiorAir);

        WardedBuilding ward = new WardedBuilding(player.getUUID(), doorPos, interiorAir, protectedPositions);
        WardSavedData wardData = WardSavedData.get(level);
        wardData.addWard(player.getUUID(), ward);

        level.sendParticles(ParticleTypes.ENCHANT,
                doorPos.getX() + 0.5, doorPos.getY() + 0.5, doorPos.getZ() + 0.5,
                30, 0.3, 0.5, 0.3, 0.5);

        player.playSound(SoundEvents.BEACON_ACTIVATE, 1.5F, 1.2F);

        onSuccessfulCast(player, 2);
        shiftAlignment(player, 3);

        WardEvents.syncWardBoundaries(level);

        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "§a§lWard established! " + interiorAir.size() + " blocks protected."));

        return true;
    }

    private int computeEnclosure(ServerLevel level, Set<BlockPos> interior) {
        int solidNeighbors = 0;
        for (BlockPos pos : interior) {
            for (BlockPos neighbor : List.of(
                    pos.above(), pos.below(),
                    pos.north(), pos.south(),
                    pos.east(), pos.west())) {
                if (!level.getBlockState(neighbor).isAir()) {
                    solidNeighbors++;
                }
            }
        }
        return solidNeighbors;
    }

    private Set<BlockPos> floodFillAir(ServerLevel level, BlockPos start, int maxBlocks) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty() && visited.size() < maxBlocks) {
            BlockPos current = queue.poll();
            if (!visited.add(current)) continue;

            for (BlockPos neighbor : List.of(
                    current.above(), current.below(),
                    current.north(), current.south(),
                    current.east(), current.west())) {
                if (!visited.contains(neighbor) && level.getBlockState(neighbor).isAir()) {
                    queue.add(neighbor);
                }
            }
        }
        return visited;
    }

    private Set<BlockPos> computeProtectedPositions(ServerLevel level, Set<BlockPos> interiorAir) {
        Set<BlockPos> protected_ = new HashSet<>(interiorAir);
        for (BlockPos pos : interiorAir) {
            for (BlockPos neighbor : List.of(
                    pos.above(), pos.below(),
                    pos.north(), pos.south(),
                    pos.east(), pos.west())) {
                if (!interiorAir.contains(neighbor) && !level.getBlockState(neighbor).isAir()) {
                    protected_.add(neighbor);
                }
            }
        }
        return protected_;
    }
}
