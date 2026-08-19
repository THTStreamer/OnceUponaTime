package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.WardSavedData;
import com.example.ouat.data.WardSavedData.WardedBuilding;
import com.example.ouat.magic.Spell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.*;

public class WardSpell extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "ward");

    private static final int MAX_WARD_SIZE = 128;
    private static final int MIN_WARD_SIZE = 2;

    public WardSpell() {
        super(ID, "Ward", 25, 10);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough. Requires proficiency 25."));
            return false;
        }
        if (!consumeFood(player, 10)) return false;

        ServerLevel level = player.serverLevel();

        // Raycast to find the door block the player is looking at
        HitResult hitResult = player.pick(5.0, 0.0F, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cLook at a doorway or entrance to set the ward."));
            return false;
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos hitPos = blockHit.getBlockPos();
        BlockState hitState = level.getBlockState(hitPos);

        // Find the air block for the doorway
        BlockPos doorPos;
        if (hitState.isAir()) {
            doorPos = hitPos;
        } else {
            // Look for adjacent air blocks in all 6 directions
            doorPos = null;
            for (BlockPos neighbor : List.of(
                    hitPos.above(), hitPos.below(),
                    hitPos.north(), hitPos.south(),
                    hitPos.east(), hitPos.west())) {
                if (level.getBlockState(neighbor).isAir()) {
                    doorPos = neighbor;
                    break;
                }
            }
            if (doorPos == null) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cLook at an open doorway or entrance (need an air block nearby)."));
                return false;
            }
        }

        // Flood-fill to find interior air pockets
        Set<BlockPos> interiorAir = floodFillAir(level, doorPos, MAX_WARD_SIZE);
        if (interiorAir.size() < MIN_WARD_SIZE) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cInterior too small. Needs at least " + MIN_WARD_SIZE + " air blocks."));
            return false;
        }

        // Compute protected positions: interior air + 1 block shell
        Set<BlockPos> protectedPositions = computeProtectedPositions(level, interiorAir);

        // Save ward
        WardedBuilding ward = new WardedBuilding(player.getUUID(), doorPos, interiorAir, protectedPositions);
        WardSavedData wardData = WardSavedData.get(level);
        wardData.addWard(player.getUUID(), ward);

        // VFX: spawn particles at every doorway block
        for (BlockPos pos : interiorAir) {
            // Check if this block is on the edge of the air fill
            if (isDoorway(level, pos)) {
                level.sendParticles(ParticleTypes.WITCH,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        20, 0.3, 0.5, 0.3, 0.01);
                level.sendParticles(ParticleTypes.ENCHANT,
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                        30, 0.2, 0.3, 0.2, 0.5);
            }
        }

        // Sound
        player.playSound(SoundEvents.BEACON_ACTIVATE, 1.5F, 1.2F);

        // Proficiency gain
        onSuccessfulCast(player, 2);
        shiftAlignment(player, 3);

        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "§a§lWard established! " + interiorAir.size() + " blocks protected."));

        return true;
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

    private boolean isDoorway(ServerLevel level, BlockPos pos) {
        // A doorway is an air block that has at least one solid neighbor
        // (i.e., it's on the boundary between interior and exterior)
        return !level.getBlockState(pos.below()).isAir()
                || !level.getBlockState(pos.above()).isAir()
                || !level.getBlockState(pos.north()).isAir()
                || !level.getBlockState(pos.south()).isAir()
                || !level.getBlockState(pos.east()).isAir()
                || !level.getBlockState(pos.west()).isAir();
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
