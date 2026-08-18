package com.example.ouat.data;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class CurseInstance {
    private final ResourceLocation curseId;
    private final String curseName;
    private long startTime;
    private long duration;
    private boolean active;
    private UUID sourcePlayer;

    public CurseInstance(ResourceLocation curseId, String curseName, long duration, UUID sourcePlayer) {
        this.curseId = curseId;
        this.curseName = curseName;
        this.startTime = System.currentTimeMillis();
        this.duration = duration;
        this.active = true;
        this.sourcePlayer = sourcePlayer;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("CurseId", curseId.toString());
        tag.putString("CurseName", curseName);
        tag.putLong("StartTime", startTime);
        tag.putLong("Duration", duration);
        tag.putBoolean("Active", active);
        if (sourcePlayer != null) {
            tag.putString("SourcePlayer", sourcePlayer.toString());
        }
        return tag;
    }

    public static CurseInstance fromTag(CompoundTag tag) {
        ResourceLocation id = ResourceLocation.parse(tag.getString("CurseId"));
        String name = tag.getString("CurseName");
        long start = tag.getLong("StartTime");
        long dur = tag.getLong("Duration");
        UUID source = tag.contains("SourcePlayer") ?
                UUID.fromString(tag.getString("SourcePlayer")) : null;
        CurseInstance instance = new CurseInstance(id, name, dur, source);
        instance.startTime = start;
        instance.active = tag.getBoolean("Active");
        return instance;
    }

    public ResourceLocation getCurseId() { return curseId; }
    public String getCurseName() { return curseName; }
    public long getStartTime() { return startTime; }
    public long getDuration() { return duration; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public UUID getSourcePlayer() { return sourcePlayer; }

    public boolean isExpired() {
        return duration > 0 && (System.currentTimeMillis() - startTime) >= duration;
    }
}
