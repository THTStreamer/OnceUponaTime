package com.example.ouat.data;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.nbt.CompoundTag;

public class BlessingInstance {
    private final ResourceLocation blessingId;
    private final String blessingName;
    private long startTime;
    private long duration;
    private boolean active;
    private ResourceLocation sourcePlayer;

    public BlessingInstance(ResourceLocation blessingId, String blessingName, long duration, ResourceLocation sourcePlayer) {
        this.blessingId = blessingId;
        this.blessingName = blessingName;
        this.startTime = System.currentTimeMillis();
        this.duration = duration;
        this.active = true;
        this.sourcePlayer = sourcePlayer;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("BlessingId", blessingId.toString());
        tag.putString("BlessingName", blessingName);
        tag.putLong("StartTime", startTime);
        tag.putLong("Duration", duration);
        tag.putBoolean("Active", active);
        if (sourcePlayer != null) {
            tag.putString("SourcePlayer", sourcePlayer.toString());
        }
        return tag;
    }

    public static BlessingInstance fromTag(CompoundTag tag) {
        ResourceLocation id = ResourceLocation.parse(tag.getString("BlessingId"));
        String name = tag.getString("BlessingName");
        long start = tag.getLong("StartTime");
        long dur = tag.getLong("Duration");
        ResourceLocation source = tag.contains("SourcePlayer") ?
                ResourceLocation.parse(tag.getString("SourcePlayer")) : null;
        BlessingInstance instance = new BlessingInstance(id, name, dur, source);
        instance.startTime = start;
        instance.active = tag.getBoolean("Active");
        return instance;
    }

    public ResourceLocation getBlessingId() { return blessingId; }
    public String getBlessingName() { return blessingName; }
    public long getStartTime() { return startTime; }
    public long getDuration() { return duration; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public ResourceLocation getSourcePlayer() { return sourcePlayer; }

    public boolean isExpired() {
        return duration > 0 && (System.currentTimeMillis() - startTime) >= duration;
    }
}
