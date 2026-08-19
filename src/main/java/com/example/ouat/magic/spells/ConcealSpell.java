package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.ConcealmentSavedData;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import com.example.ouat.network.ConcealmentStatePacket;
import com.example.ouat.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class ConcealSpell extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "conceal");

    public ConcealSpell() {
        super(ID, "Conceal", 20);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 8)) return false;

        data.setConcealReady(true);
        player.sendSystemMessage(Component.literal("§5§lConcealment ready. Right-click a door to conceal the room beyond."));
        return true;
    }

    public static boolean handleDoorClick(ServerPlayer player, ServerLevel level, BlockPos clickedDoorPos) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!data.isConcealReady()) return false;

        BlockState clickedState = level.getBlockState(clickedDoorPos);
        if (!(clickedState.getBlock() instanceof DoorBlock)) {
            player.sendSystemMessage(Component.literal("§cYou must target a door."));
            data.setConcealReady(false);
            return false;
        }

        data.setConcealReady(false);

        DoorBlock doorBlock = (DoorBlock) clickedState.getBlock();
        DoubleBlockHalf clickedHalf = clickedState.getValue(DoorBlock.HALF);
        BlockPos baseDoor = (clickedHalf == DoubleBlockHalf.LOWER) ? clickedDoorPos : clickedDoorPos.below();
        BlockState baseState = level.getBlockState(baseDoor);
        Direction facing = baseState.getValue(DoorBlock.FACING);

        Set<BlockPos> interiorAir = new HashSet<>();
        findInteriorAir(level, baseDoor, facing, interiorAir);

        if (interiorAir.isEmpty()) {
            player.sendSystemMessage(Component.literal("§cNo room found behind that door."));
            return false;
        }

        Set<BlockPos> shellBlocks = new HashSet<>();
        for (BlockPos air : interiorAir) {
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = air.relative(dir);
                if (!interiorAir.contains(neighbor)) {
                    BlockState neighborState = level.getBlockState(neighbor);
                    if (!neighborState.isAir() && !(neighborState.getBlock() instanceof DoorBlock)) {
                        shellBlocks.add(neighbor);
                    }
                }
            }
        }

        shellBlocks.add(baseDoor);
        shellBlocks.add(baseDoor.above());

        BlockPos roomCenter = findCenter(interiorAir);

        int exitX = baseDoor.getX() + facing.getStepX() * 3;
        int exitZ = baseDoor.getZ() + facing.getStepZ() * 3;
        BlockPos exitPosition = new BlockPos(exitX, baseDoor.getY(), exitZ);

        Map<BlockPos, BlockState> originalBlocks = new LinkedHashMap<>();
        List<BlockPos> disguisedPositions = new ArrayList<>();

        for (BlockPos shell : shellBlocks) {
            BlockState currentState = level.getBlockState(shell);
            originalBlocks.put(shell, currentState);
            level.setBlockAndUpdate(shell, Blocks.BARRIER.defaultBlockState());
            disguisedPositions.add(shell);
        }

        ConcealmentSavedData roomData = ConcealmentSavedData.get(level);
        ConcealmentSavedData.ConcealedRoom room = new ConcealmentSavedData.ConcealedRoom(
                player.getUUID(), baseDoor, roomCenter, exitPosition,
                originalBlocks, disguisedPositions);
        roomData.addRoom(player.getUUID(), room);

        Map<BlockPos, BlockState> casterBlocks = new HashMap<>();
        for (BlockPos pos : disguisedPositions) {
            if (originalBlocks.containsKey(pos)) {
                casterBlocks.put(pos, originalBlocks.get(pos));
            }
        }

        ConcealmentStatePacket packet = new ConcealmentStatePacket(casterBlocks, false);
        player.connection.send(packet);

        addConcealmentEffects(level, roomCenter, shellBlocks.size());

        player.sendSystemMessage(Component.literal("§5§lThe building has been concealed from sight."));
        player.sendSystemMessage(Component.literal("§7Right-click the invisible wall to teleport in/out."));
        return true;
    }

    private static void findInteriorAir(ServerLevel level, BlockPos startDoor, Direction facing, Set<BlockPos> result) {
        int dx = facing.getStepX();
        int dz = facing.getStepZ();
        BlockPos start = startDoor.offset(-dx, 0, -dz);

        BlockState startState = level.getBlockState(start);
        if (!startState.isAir() && !(startState.getBlock() instanceof DoorBlock)) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos test = startDoor.relative(dir);
                if (level.getBlockState(test).isAir()) {
                    start = test;
                    break;
                }
            }
        }

        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(start);
        result.add(start);

        int maxY = startDoor.getY() + 20;
        int minY = startDoor.getY() - 5;
        int maxBlocks = 1000;
        int count = 0;

        while (!queue.isEmpty() && count < maxBlocks) {
            BlockPos current = queue.poll();
            count++;

            if (current.getY() < minY || current.getY() > maxY) continue;

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);
                if (!result.contains(neighbor) && neighbor.getY() >= minY && neighbor.getY() <= maxY) {
                    BlockState state = level.getBlockState(neighbor);
                    if (state.isAir() || state.getBlock() instanceof DoorBlock) {
                        result.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }
    }

    private static BlockPos findCenter(Set<BlockPos> positions) {
        int sumX = 0, sumY = 0, sumZ = 0;
        for (BlockPos p : positions) {
            sumX += p.getX();
            sumY += p.getY();
            sumZ += p.getZ();
        }
        int size = positions.size();
        return new BlockPos(sumX / size, sumY / size, sumZ / size);
    }

    private static void addConcealmentEffects(ServerLevel level, BlockPos center, int blockCount) {
        level.playSound(null, center.getX(), center.getY(), center.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 2.0F, 0.5F);
        level.playSound(null, center.getX(), center.getY(), center.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 1.5F, 0.8F);

        int particleCount = Math.min(blockCount * 2, 200);
        for (int i = 0; i < particleCount; i++) {
            double x = center.getX() + (level.random.nextDouble() - 0.5) * 10;
            double y = center.getY() + (level.random.nextDouble() - 0.5) * 5;
            double z = center.getZ() + (level.random.nextDouble() - 0.5) * 10;
            level.sendParticles(ModParticles.SMOKE_SOFT.get(), x, y, z, 1, 0, 0.05, 0, 0.02);
        }
    }

    private static final Map<UUID, Long> TELEPORT_COOLDOWN = new HashMap<>();

    public static BlockPos getTeleportTarget(ServerPlayer player, ServerLevel level, ConcealmentSavedData.ConcealedRoom room) {
        long now = level.getGameTime();
        Long lastTeleport = TELEPORT_COOLDOWN.get(player.getUUID());
        if (lastTeleport != null && now - lastTeleport < 5) {
            return null;
        }

        BlockPos playerPos = player.blockPosition();
        boolean isInside = room.getInteriorAir().contains(playerPos);

        TELEPORT_COOLDOWN.put(player.getUUID(), now);

        if (isInside) {
            return room.getExitPosition();
        } else {
            return room.getTeleportTarget();
        }
    }
}
