package com.example.ouat.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class WardSavedData extends SavedData {
    private static final String DATA_NAME = "onceuponatime_wards";
    private final Map<UUID, List<WardedBuilding>> playerWards = new HashMap<>();

    public WardSavedData() {}

    public static WardSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        WardSavedData data = new WardSavedData();
        ListTag playersTag = tag.getList("Players", 10);
        for (int i = 0; i < playersTag.size(); i++) {
            CompoundTag playerTag = playersTag.getCompound(i);
            UUID owner = playerTag.getUUID("Owner");
            ListTag wardsTag = playerTag.getList("Wards", 10);
            List<WardedBuilding> wards = new ArrayList<>();
            for (int j = 0; j < wardsTag.size(); j++) {
                wards.add(WardedBuilding.fromTag(wardsTag.getCompound(j)));
            }
            data.playerWards.put(owner, wards);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag playersTag = new ListTag();
        for (Map.Entry<UUID, List<WardedBuilding>> entry : playerWards.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("Owner", entry.getKey());
            ListTag wardsTag = new ListTag();
            for (WardedBuilding ward : entry.getValue()) {
                wardsTag.add(ward.toTag());
            }
            playerTag.put("Wards", wardsTag);
            playersTag.add(playerTag);
        }
        tag.put("Players", playersTag);
        return tag;
    }

    public void addWard(UUID owner, WardedBuilding ward) {
        playerWards.computeIfAbsent(owner, k -> new ArrayList<>()).add(ward);
        setDirty();
    }

    public List<WardedBuilding> getWards(UUID owner) {
        return playerWards.getOrDefault(owner, Collections.emptyList());
    }

    public WardedBuilding findWardAt(BlockPos pos) {
        for (List<WardedBuilding> wards : playerWards.values()) {
            for (WardedBuilding ward : wards) {
                if (ward.getProtectedPositions().contains(pos)) {
                    return ward;
                }
            }
        }
        return null;
    }

    public boolean removeWard(UUID owner, BlockPos doorPos) {
        List<WardedBuilding> wards = playerWards.get(owner);
        if (wards != null) {
            boolean removed = wards.removeIf(w -> w.getDoorPos().equals(doorPos));
            if (removed) setDirty();
            return removed;
        }
        return false;
    }

    public List<WardedBuilding> getAllWards() {
        List<WardedBuilding> all = new ArrayList<>();
        for (List<WardedBuilding> wards : playerWards.values()) {
            all.addAll(wards);
        }
        return all;
    }

    public static WardSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(WardSavedData::new, WardSavedData::load),
                DATA_NAME
        );
    }

    public static class WardedBuilding {
        private final UUID ownerUUID;
        private final BlockPos doorPos;
        private final Set<BlockPos> interiorAir;
        private final Set<BlockPos> protectedPositions;
        private final Set<UUID> authorizedPlayers;

        public WardedBuilding(UUID ownerUUID, BlockPos doorPos, Set<BlockPos> interiorAir, Set<BlockPos> protectedPositions) {
            this.ownerUUID = ownerUUID;
            this.doorPos = doorPos;
            this.interiorAir = interiorAir;
            this.protectedPositions = protectedPositions;
            this.authorizedPlayers = new HashSet<>();
            this.authorizedPlayers.add(ownerUUID);
        }

        public UUID getOwnerUUID() { return ownerUUID; }
        public BlockPos getDoorPos() { return doorPos; }
        public Set<BlockPos> getInteriorAir() { return interiorAir; }
        public Set<BlockPos> getProtectedPositions() { return protectedPositions; }
        public Set<UUID> getAuthorizedPlayers() { return authorizedPlayers; }

        public void authorizePlayer(UUID player) { authorizedPlayers.add(player); }
        public void deauthorizePlayer(UUID player) { authorizedPlayers.remove(player); }
        public boolean isAuthorized(UUID player) { return authorizedPlayers.contains(player); }

        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("Owner", ownerUUID);
            tag.putInt("DoorX", doorPos.getX());
            tag.putInt("DoorY", doorPos.getY());
            tag.putInt("DoorZ", doorPos.getZ());

            ListTag interiorTag = new ListTag();
            for (BlockPos pos : interiorAir) {
                CompoundTag pTag = new CompoundTag();
                pTag.putInt("X", pos.getX());
                pTag.putInt("Y", pos.getY());
                pTag.putInt("Z", pos.getZ());
                interiorTag.add(pTag);
            }
            tag.put("InteriorAir", interiorTag);

            ListTag protectedTag = new ListTag();
            for (BlockPos pos : protectedPositions) {
                CompoundTag pTag = new CompoundTag();
                pTag.putInt("X", pos.getX());
                pTag.putInt("Y", pos.getY());
                pTag.putInt("Z", pos.getZ());
                protectedTag.add(pTag);
            }
            tag.put("ProtectedPositions", protectedTag);

            ListTag authTag = new ListTag();
            for (UUID uuid : authorizedPlayers) {
                authTag.add(net.minecraft.nbt.NbtUtils.createUUID(uuid));
            }
            tag.put("AuthorizedPlayers", authTag);

            return tag;
        }

        public static WardedBuilding fromTag(CompoundTag tag) {
            UUID owner = tag.getUUID("Owner");
            BlockPos door = new BlockPos(tag.getInt("DoorX"), tag.getInt("DoorY"), tag.getInt("DoorZ"));

            Set<BlockPos> interior = new HashSet<>();
            ListTag interiorTag = tag.getList("InteriorAir", 10);
            for (int i = 0; i < interiorTag.size(); i++) {
                CompoundTag pTag = interiorTag.getCompound(i);
                interior.add(new BlockPos(pTag.getInt("X"), pTag.getInt("Y"), pTag.getInt("Z")));
            }

            Set<BlockPos> protected_ = new HashSet<>();
            ListTag protectedTag = tag.getList("ProtectedPositions", 10);
            for (int i = 0; i < protectedTag.size(); i++) {
                CompoundTag pTag = protectedTag.getCompound(i);
                protected_.add(new BlockPos(pTag.getInt("X"), pTag.getInt("Y"), pTag.getInt("Z")));
            }

            WardedBuilding ward = new WardedBuilding(owner, door, interior, protected_);

            ListTag authTag = tag.getList("AuthorizedPlayers", 11);
            for (int i = 0; i < authTag.size(); i++) {
                ward.authorizedPlayers.add(net.minecraft.nbt.NbtUtils.loadUUID(authTag.getCompound(i)));
            }

            return ward;
        }
    }
}
