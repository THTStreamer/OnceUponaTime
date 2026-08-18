package com.example.ouat.data;

import com.example.ouat.OnceUponATime;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UniqueRoleRegistry extends SavedData {
    private static final String DATA_NAME = OnceUponATime.MOD_ID + "_unique_roles";
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();

    private UUID darkOneUUID;
    private UUID saviorUUID;
    private UUID truestBelieverUUID;
    private UUID authorUUID;

    private final List<UUID> historicalDarkOnes = new ArrayList<>();
    private final List<UUID> historicalSaviors = new ArrayList<>();
    private final List<UUID> historicalTruestBelievers = new ArrayList<>();
    private final List<UUID> historicalAuthors = new ArrayList<>();

    public UniqueRoleRegistry() {
        this.darkOneUUID = null;
        this.saviorUUID = null;
        this.truestBelieverUUID = null;
        this.authorUUID = null;
    }

    public static UniqueRoleRegistry load(CompoundTag tag, HolderLookup.Provider registries) {
        UniqueRoleRegistry data = new UniqueRoleRegistry();

        if (tag.hasUUID("DarkOneUUID")) {
            data.darkOneUUID = tag.getUUID("DarkOneUUID");
        }
        if (tag.hasUUID("SaviorUUID")) {
            data.saviorUUID = tag.getUUID("SaviorUUID");
        }
        if (tag.hasUUID("TruestBelieverUUID")) {
            data.truestBelieverUUID = tag.getUUID("TruestBelieverUUID");
        }
        if (tag.hasUUID("AuthorUUID")) {
            data.authorUUID = tag.getUUID("AuthorUUID");
        }

        data.historicalDarkOnes.clear();
        ListTag darkOnesList = tag.getList("HistoricalDarkOnes", 8);
        for (int i = 0; i < darkOnesList.size(); i++) {
            data.historicalDarkOnes.add(UUID.fromString(darkOnesList.getString(i)));
        }

        data.historicalSaviors.clear();
        ListTag saviorsList = tag.getList("HistoricalSaviors", 8);
        for (int i = 0; i < saviorsList.size(); i++) {
            data.historicalSaviors.add(UUID.fromString(saviorsList.getString(i)));
        }

        data.historicalTruestBelievers.clear();
        ListTag truestBelieversList = tag.getList("HistoricalTruestBelievers", 8);
        for (int i = 0; i < truestBelieversList.size(); i++) {
            data.historicalTruestBelievers.add(UUID.fromString(truestBelieversList.getString(i)));
        }

        data.historicalAuthors.clear();
        ListTag authorsList = tag.getList("HistoricalAuthors", 8);
        for (int i = 0; i < authorsList.size(); i++) {
            data.historicalAuthors.add(UUID.fromString(authorsList.getString(i)));
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        LOCK.readLock().lock();
        try {
            if (darkOneUUID != null) {
                tag.putUUID("DarkOneUUID", darkOneUUID);
            }
            if (saviorUUID != null) {
                tag.putUUID("SaviorUUID", saviorUUID);
            }
            if (truestBelieverUUID != null) {
                tag.putUUID("TruestBelieverUUID", truestBelieverUUID);
            }
            if (authorUUID != null) {
                tag.putUUID("AuthorUUID", authorUUID);
            }

            ListTag darkOnesList = new ListTag();
            for (UUID uuid : historicalDarkOnes) {
                darkOnesList.add(net.minecraft.nbt.StringTag.valueOf(uuid.toString()));
            }
            tag.put("HistoricalDarkOnes", darkOnesList);

            ListTag saviorsList = new ListTag();
            for (UUID uuid : historicalSaviors) {
                saviorsList.add(net.minecraft.nbt.StringTag.valueOf(uuid.toString()));
            }
            tag.put("HistoricalSaviors", saviorsList);

            ListTag truestBelieversList = new ListTag();
            for (UUID uuid : historicalTruestBelievers) {
                truestBelieversList.add(net.minecraft.nbt.StringTag.valueOf(uuid.toString()));
            }
            tag.put("HistoricalTruestBelievers", truestBelieversList);

            ListTag authorsList = new ListTag();
            for (UUID uuid : historicalAuthors) {
                authorsList.add(net.minecraft.nbt.StringTag.valueOf(uuid.toString()));
            }
            tag.put("HistoricalAuthors", authorsList);
        } finally {
            LOCK.readLock().unlock();
        }
        return tag;
    }

    public static UniqueRoleRegistry get(ServerLevel level) {
        ServerLevel overworld = level.getServer().getLevel(ServerLevel.OVERWORLD);
        if (overworld == null) {
            overworld = level;
        }
        return overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(UniqueRoleRegistry::new, UniqueRoleRegistry::load, null),
                DATA_NAME
        );
    }

    public static UniqueRoleRegistry get(MinecraftServer server) {
        ServerLevel overworld = server.getLevel(ServerLevel.OVERWORLD);
        return overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(UniqueRoleRegistry::new, UniqueRoleRegistry::load, null),
                DATA_NAME
        );
    }

    public enum RoleType {
        DARK_ONE,
        SAVIOR,
        TRUEST_BELIEVER,
        AUTHOR
    }

    public boolean isRoleVacant(RoleType role) {
        LOCK.readLock().lock();
        try {
            return switch (role) {
                case DARK_ONE -> darkOneUUID == null;
                case SAVIOR -> saviorUUID == null;
                case TRUEST_BELIEVER -> truestBelieverUUID == null;
                case AUTHOR -> authorUUID == null;
            };
        } finally {
            LOCK.readLock().unlock();
        }
    }

    public UUID getHolder(RoleType role) {
        LOCK.readLock().lock();
        try {
            return switch (role) {
                case DARK_ONE -> darkOneUUID;
                case SAVIOR -> saviorUUID;
                case TRUEST_BELIEVER -> truestBelieverUUID;
                case AUTHOR -> authorUUID;
            };
        } finally {
            LOCK.readLock().unlock();
        }
    }

    public String getHolderName(MinecraftServer server, RoleType role) {
        UUID holder = getHolder(role);
        if (holder == null) return "NONE";

        var player = server.getPlayerList().getPlayer(holder);
        if (player != null) {
            return player.getName().getString();
        }
        return "Unknown (" + holder.toString().substring(0, 8) + ")";
    }

    public boolean claimRole(UUID playerUUID, RoleType role) {
        LOCK.writeLock().lock();
        try {
            if (!isRoleVacant(role)) {
                return false;
            }

            switch (role) {
                case DARK_ONE -> {
                    if (darkOneUUID != null) return false;
                    darkOneUUID = playerUUID;
                    if (!historicalDarkOnes.contains(playerUUID)) {
                        historicalDarkOnes.add(playerUUID);
                    }
                }
                case SAVIOR -> {
                    if (saviorUUID != null) return false;
                    saviorUUID = playerUUID;
                    if (!historicalSaviors.contains(playerUUID)) {
                        historicalSaviors.add(playerUUID);
                    }
                }
                case TRUEST_BELIEVER -> {
                    if (truestBelieverUUID != null) return false;
                    truestBelieverUUID = playerUUID;
                    if (!historicalTruestBelievers.contains(playerUUID)) {
                        historicalTruestBelievers.add(playerUUID);
                    }
                }
                case AUTHOR -> {
                    if (authorUUID != null) return false;
                    authorUUID = playerUUID;
                    if (!historicalAuthors.contains(playerUUID)) {
                        historicalAuthors.add(playerUUID);
                    }
                }
            }

            setDirty();
            OnceUponATime.LOGGER.info("Player {} claimed role {}", playerUUID, role);
            return true;
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public void releaseRole(RoleType role) {
        LOCK.writeLock().lock();
        try {
            switch (role) {
                case DARK_ONE -> darkOneUUID = null;
                case SAVIOR -> saviorUUID = null;
                case TRUEST_BELIEVER -> truestBelieverUUID = null;
                case AUTHOR -> authorUUID = null;
            }
            setDirty();
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public void transferRole(UUID newOwner, RoleType role) {
        LOCK.writeLock().lock();
        try {
            releaseRole(role);
            claimRole(newOwner, role);
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public boolean hasPlayerHeldRole(UUID playerUUID, RoleType role) {
        LOCK.readLock().lock();
        try {
            return switch (role) {
                case DARK_ONE -> historicalDarkOnes.contains(playerUUID);
                case SAVIOR -> historicalSaviors.contains(playerUUID);
                case TRUEST_BELIEVER -> historicalTruestBelievers.contains(playerUUID);
                case AUTHOR -> historicalAuthors.contains(playerUUID);
            };
        } finally {
            LOCK.readLock().unlock();
        }
    }

    public List<UUID> getHistoricalHolders(RoleType role) {
        LOCK.readLock().lock();
        try {
            return switch (role) {
                case DARK_ONE -> List.copyOf(historicalDarkOnes);
                case SAVIOR -> List.copyOf(historicalSaviors);
                case TRUEST_BELIEVER -> List.copyOf(historicalTruestBelievers);
                case AUTHOR -> List.copyOf(historicalAuthors);
            };
        } finally {
            LOCK.readLock().unlock();
        }
    }

    public static void onServerStarting(net.neoforged.neoforge.event.server.ServerStartingEvent event) {
        UniqueRoleRegistry registry = get(event.getServer());
        OnceUponATime.LOGGER.info("Unique Role Registry loaded. Dark One: {}, Savior: {}, Truest Believer: {}, Author: {}",
                registry.isRoleVacant(RoleType.DARK_ONE) ? "VACANT" : "OCCUPIED",
                registry.isRoleVacant(RoleType.SAVIOR) ? "VACANT" : "OCCUPIED",
                registry.isRoleVacant(RoleType.TRUEST_BELIEVER) ? "VACANT" : "OCCUPIED",
                registry.isRoleVacant(RoleType.AUTHOR) ? "VACANT" : "OCCUPIED");
    }

    public static void onServerStopping(net.neoforged.neoforge.event.server.ServerStoppingEvent event) {
        OnceUponATime.LOGGER.info("Unique Role Registry saved on server stop");
    }
}
