package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.ConcealmentSavedData;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

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

    public static boolean handleDoorClick(ServerPlayer player, ServerLevel level, BlockPos doorPos) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!data.isConcealReady()) return false;

        BlockState state = level.getBlockState(doorPos);
        if (!(state.getBlock() instanceof DoorBlock)) {
            player.sendSystemMessage(Component.literal("§cYou must target a door."));
            data.setConcealReady(false);
            return false;
        }

        data.setConcealReady(false);

        BlockPos roomCenter = findRoomBehindDoor(level, doorPos, state);
        if (roomCenter == null) {
            player.sendSystemMessage(Component.literal("§cNo room found behind that door."));
            return false;
        }

        boolean success = concealRoom(player, level, doorPos, state, roomCenter);
        if (success) {
            player.sendSystemMessage(Component.literal("§5§lThe room has been concealed from sight."));
            player.playSound(net.minecraft.sounds.SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
        }
        return success;
    }

    private static BlockPos findRoomBehindDoor(ServerLevel level, BlockPos doorPos, BlockState doorState) {
        Direction facing = doorState.getValue(DoorBlock.FACING);
        DoubleBlockHalf half = doorState.getValue(DoorBlock.HALF);

        BlockPos searchStart = (half == DoubleBlockHalf.LOWER) ? doorPos : doorPos.below();

        int dx = 0, dz = 0;
        switch (facing) {
            case NORTH -> dz = 1;
            case SOUTH -> dz = -1;
            case EAST -> dx = -1;
            case WEST -> dx = 1;
        }

        BlockPos behindDoor = searchStart.offset(dx, 0, dz);

        Set<BlockPos> airBlocks = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(behindDoor);
        airBlocks.add(behindDoor);

        int maxRoomSize = 500;
        int count = 0;

        while (!queue.isEmpty() && count < maxRoomSize) {
            BlockPos current = queue.poll();
            count++;

            for (Direction dir : Direction.values()) {
                if (dir == Direction.UP || dir == Direction.DOWN) continue;
                BlockPos neighbor = current.relative(dir);
                if (!airBlocks.contains(neighbor) && level.getBlockState(neighbor).isAir()) {
                    airBlocks.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        if (airBlocks.isEmpty()) return null;

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (BlockPos p : airBlocks) {
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            minZ = Math.min(minZ, p.getZ());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
            maxZ = Math.max(maxZ, p.getZ());
        }

        return new BlockPos((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
    }

    private static boolean concealRoom(ServerPlayer player, ServerLevel level, BlockPos doorPos, BlockState doorState, BlockPos roomCenter) {
        UUID ownerUUID = player.getUUID();
        Direction facing = doorState.getValue(DoorBlock.FACING);
        DoubleBlockHalf half = doorState.getValue(DoorBlock.HALF);

        BlockPos baseDoor = (half == DoubleBlockHalf.LOWER) ? doorPos : doorPos.below();

        BlockState wallBlock = findMatchingWallBlock(level, baseDoor);

        Map<BlockPos, BlockState> originalBlocks = new LinkedHashMap<>();
        List<BlockPos> disguisedPositions = new ArrayList<>();

        disguiseBlock(level, baseDoor, wallBlock, originalBlocks, disguisedPositions);
        disguiseBlock(level, baseDoor.above(), wallBlock, originalBlocks, disguisedPositions);

        int dx = 0, dz = 0;
        switch (facing) {
            case NORTH -> dz = 1;
            case SOUTH -> dz = -1;
            case EAST -> dx = -1;
            case WEST -> dx = 1;
        }

        disguiseBlock(level, baseDoor.offset(dx, 0, dz), wallBlock, originalBlocks, disguisedPositions);
        disguiseBlock(level, baseDoor.offset(dx, 1, dz), wallBlock, originalBlocks, disguisedPositions);
        disguiseBlock(level, baseDoor.offset(-dx, 0, -dz), wallBlock, originalBlocks, disguisedPositions);
        disguiseBlock(level, baseDoor.offset(-dx, 1, -dz), wallBlock, originalBlocks, disguisedPositions);

        disguiseBlock(level, baseDoor.offset(dz, 0, dx), wallBlock, originalBlocks, disguisedPositions);
        disguiseBlock(level, baseDoor.offset(dz, 1, dx), wallBlock, originalBlocks, disguisedPositions);
        disguiseBlock(level, baseDoor.offset(-dz, 0, -dx), wallBlock, originalBlocks, disguisedPositions);
        disguiseBlock(level, baseDoor.offset(-dz, 1, -dx), wallBlock, originalBlocks, disguisedPositions);

        ConcealmentSavedData roomData = ConcealmentSavedData.get(level);
        ConcealmentSavedData.ConcealedRoom room = new ConcealmentSavedData.ConcealedRoom(
                ownerUUID, baseDoor, roomCenter, originalBlocks, disguisedPositions);
        roomData.addRoom(ownerUUID, room);

        return true;
    }

    private static void disguiseBlock(ServerLevel level, BlockPos pos, BlockState wallBlock,
                                     Map<BlockPos, BlockState> originalBlocks, List<BlockPos> disguisedPositions) {
        BlockState current = level.getBlockState(pos);
        if (!current.isAir() && !(current.getBlock() instanceof DoorBlock)) return;

        originalBlocks.put(pos, current);
        level.setBlockAndUpdate(pos, wallBlock);
        disguisedPositions.add(pos);
    }

    private static BlockState findMatchingWallBlock(ServerLevel level, BlockPos doorPos) {
        for (Direction dir : Direction.values()) {
            if (dir == Direction.UP || dir == Direction.DOWN) continue;
            BlockPos neighbor = doorPos.relative(dir);
            BlockState neighborState = level.getBlockState(neighbor);
            if (!neighborState.isAir() && !(neighborState.getBlock() instanceof DoorBlock)) {
                return neighborState;
            }
        }
        return Blocks.STONE_BRICKS.defaultBlockState();
    }
}
