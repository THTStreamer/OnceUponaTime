package com.example.ouat.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class ConcealmentSavedData extends SavedData {
    private static final String DATA_NAME = "onceuponatime_concealment";
    private final Map<UUID, List<ConcealedRoom>> playerRooms = new HashMap<>();

    public ConcealmentSavedData() {}

    public static ConcealmentSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        ConcealmentSavedData data = new ConcealmentSavedData();
        ListTag playersTag = tag.getList("Players", 10);
        for (int i = 0; i < playersTag.size(); i++) {
            CompoundTag playerTag = playersTag.getCompound(i);
            UUID owner = playerTag.getUUID("Owner");
            ListTag roomsTag = playerTag.getList("Rooms", 10);
            List<ConcealedRoom> rooms = new ArrayList<>();
            for (int j = 0; j < roomsTag.size(); j++) {
                rooms.add(ConcealedRoom.fromTag(roomsTag.getCompound(j), registries));
            }
            data.playerRooms.put(owner, rooms);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag playersTag = new ListTag();
        for (Map.Entry<UUID, List<ConcealedRoom>> entry : playerRooms.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("Owner", entry.getKey());
            ListTag roomsTag = new ListTag();
            for (ConcealedRoom room : entry.getValue()) {
                roomsTag.add(room.toTag(registries));
            }
            playerTag.put("Rooms", roomsTag);
            playersTag.add(playerTag);
        }
        tag.put("Players", playersTag);
        return tag;
    }

    public void addRoom(UUID owner, ConcealedRoom room) {
        playerRooms.computeIfAbsent(owner, k -> new ArrayList<>()).add(room);
        setDirty();
    }

    public List<ConcealedRoom> getRooms(UUID owner) {
        return playerRooms.getOrDefault(owner, Collections.emptyList());
    }

    public ConcealedRoom findRoomAt(BlockPos pos) {
        for (List<ConcealedRoom> rooms : playerRooms.values()) {
            for (ConcealedRoom room : rooms) {
                if (room.getDisguisedPositions().contains(pos)) {
                    return room;
                }
            }
        }
        return null;
    }

    public boolean removeRoom(UUID owner, BlockPos doorPos) {
        List<ConcealedRoom> rooms = playerRooms.get(owner);
        if (rooms != null) {
            boolean removed = rooms.removeIf(r -> r.getDoorPos().equals(doorPos));
            if (removed) setDirty();
            return removed;
        }
        return false;
    }

    public static ConcealmentSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(ConcealmentSavedData::new, ConcealmentSavedData::load),
                DATA_NAME
        );
    }

    public static class ConcealedRoom {
        private final UUID ownerUUID;
        private final BlockPos doorPos;
        private final BlockPos teleportTarget;
        private final BlockPos exitPosition;
        private final Map<BlockPos, BlockState> originalBlocks;
        private final List<BlockPos> disguisedPositions;
        private final Set<BlockPos> interiorAir;

        public ConcealedRoom(UUID ownerUUID, BlockPos doorPos, BlockPos teleportTarget, BlockPos exitPosition,
                             Map<BlockPos, BlockState> originalBlocks, List<BlockPos> disguisedPositions) {
            this.ownerUUID = ownerUUID;
            this.doorPos = doorPos;
            this.teleportTarget = teleportTarget;
            this.exitPosition = exitPosition;
            this.originalBlocks = originalBlocks;
            this.disguisedPositions = disguisedPositions;
            this.interiorAir = new HashSet<>();
            for (Map.Entry<BlockPos, BlockState> entry : originalBlocks.entrySet()) {
                if (entry.getValue().isAir()) {
                    this.interiorAir.add(entry.getKey());
                }
            }
        }

        public UUID getOwnerUUID() { return ownerUUID; }
        public BlockPos getDoorPos() { return doorPos; }
        public BlockPos getTeleportTarget() { return teleportTarget; }
        public BlockPos getExitPosition() { return exitPosition; }
        public Map<BlockPos, BlockState> getOriginalBlocks() { return originalBlocks; }
        public List<BlockPos> getDisguisedPositions() { return disguisedPositions; }
        public Set<BlockPos> getInteriorAir() { return interiorAir; }

        public CompoundTag toTag(HolderLookup.Provider registries) {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("Owner", ownerUUID);
            tag.putInt("DoorX", doorPos.getX());
            tag.putInt("DoorY", doorPos.getY());
            tag.putInt("DoorZ", doorPos.getZ());
            tag.putInt("TeleX", teleportTarget.getX());
            tag.putInt("TeleY", teleportTarget.getY());
            tag.putInt("TeleZ", teleportTarget.getZ());
            tag.putInt("ExitX", exitPosition.getX());
            tag.putInt("ExitY", exitPosition.getY());
            tag.putInt("ExitZ", exitPosition.getZ());

            ListTag blocksTag = new ListTag();
            for (Map.Entry<BlockPos, BlockState> entry : originalBlocks.entrySet()) {
                CompoundTag bTag = new CompoundTag();
                bTag.putInt("X", entry.getKey().getX());
                bTag.putInt("Y", entry.getKey().getY());
                bTag.putInt("Z", entry.getKey().getZ());
                bTag.put("State", NbtUtils.writeBlockState(entry.getValue()));
                blocksTag.add(bTag);
            }
            tag.put("OriginalBlocks", blocksTag);

            ListTag disguisedTag = new ListTag();
            for (BlockPos pos : disguisedPositions) {
                CompoundTag dTag = new CompoundTag();
                dTag.putInt("X", pos.getX());
                dTag.putInt("Y", pos.getY());
                dTag.putInt("Z", pos.getZ());
                disguisedTag.add(dTag);
            }
            tag.put("DisguisedPositions", disguisedTag);

            return tag;
        }

        public static ConcealedRoom fromTag(CompoundTag tag, HolderLookup.Provider registries) {
            UUID owner = tag.getUUID("Owner");
            BlockPos door = new BlockPos(tag.getInt("DoorX"), tag.getInt("DoorY"), tag.getInt("DoorZ"));
            BlockPos tele = new BlockPos(tag.getInt("TeleX"), tag.getInt("TeleY"), tag.getInt("TeleZ"));
            BlockPos exit = new BlockPos(tag.getInt("ExitX"), tag.getInt("ExitY"), tag.getInt("ExitZ"));

            var blockGetter = registries.lookupOrThrow(Registries.BLOCK);

            Map<BlockPos, BlockState> origBlocks = new HashMap<>();
            ListTag blocksTag = tag.getList("OriginalBlocks", 10);
            for (int i = 0; i < blocksTag.size(); i++) {
                CompoundTag bTag = blocksTag.getCompound(i);
                BlockPos pos = new BlockPos(bTag.getInt("X"), bTag.getInt("Y"), bTag.getInt("Z"));
                BlockState state = NbtUtils.readBlockState(blockGetter, bTag.getCompound("State"));
                origBlocks.put(pos, state);
            }

            List<BlockPos> disguised = new ArrayList<>();
            ListTag disguisedTag = tag.getList("DisguisedPositions", 10);
            for (int i = 0; i < disguisedTag.size(); i++) {
                CompoundTag dTag = disguisedTag.getCompound(i);
                disguised.add(new BlockPos(dTag.getInt("X"), dTag.getInt("Y"), dTag.getInt("Z")));
            }

            return new ConcealedRoom(owner, door, tele, exit, origBlocks, disguised);
        }
    }
}
