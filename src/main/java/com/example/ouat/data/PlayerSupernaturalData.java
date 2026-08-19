package com.example.ouat.data;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerSupernaturalData {

    public static final Codec<PlayerSupernaturalData> CODEC =
            CompoundTag.CODEC.xmap(PlayerSupernaturalData::deserialize, PlayerSupernaturalData::serialize);

    public static final AttachmentType<PlayerSupernaturalData> TYPE =
            AttachmentType.builder(PlayerSupernaturalData::new)
                    .serialize(CODEC)
                    .build();

    private ResourceLocation currentRole;
    private MagicalAlignment magicalAlignment;
    private int magicProficiency;
    private List<ResourceLocation> learnedSpells;
    private UUID teacherUUID;
    private List<CurseInstance> curses;
    private List<BlessingInstance> blessings;
    private List<ResourceLocation> heldArtifacts;
    private Map<ResourceLocation, Integer> ritualProgression;
    private int storyProgression;
    private boolean hasHeldUniqueRole;
    private List<ResourceLocation> heldRoles;
    private int darkOneKills;
    private int darkOneKillsAsDarkOne;
    private boolean heartRipped;
    private UUID heartRippedBy;
    private boolean heartProtected;
    private UUID heartProtectedBy;
    private final Map<String, PortalLocation> savedPortals = new HashMap<>();
    private boolean concealReady;

    public PlayerSupernaturalData() {
        this.currentRole = null;
        this.magicalAlignment = MagicalAlignment.NONE;
        this.magicProficiency = 0;
        this.learnedSpells = new ArrayList<>();
        this.teacherUUID = null;
        this.curses = new ArrayList<>();
        this.blessings = new ArrayList<>();
        this.heldArtifacts = new ArrayList<>();
        this.ritualProgression = new HashMap<>();
        this.storyProgression = 0;
        this.hasHeldUniqueRole = false;
        this.heldRoles = new ArrayList<>();
        this.darkOneKills = 0;
        this.darkOneKillsAsDarkOne = 0;
        this.heartRipped = false;
        this.heartRippedBy = null;
        this.heartProtected = false;
        this.heartProtectedBy = null;
        this.concealReady = false;
    }

    public static CompoundTag serialize(PlayerSupernaturalData data) {
        CompoundTag tag = new CompoundTag();

        if (data.currentRole != null) {
            tag.putString("CurrentRole", data.currentRole.toString());
        }
        tag.putString("MagicalAlignment", data.magicalAlignment.name());
        tag.putInt("MagicProficiency", data.magicProficiency);
        tag.putInt("StoryProgression", data.storyProgression);
        tag.putBoolean("HasHeldUniqueRole", data.hasHeldUniqueRole);
        tag.putInt("DarkOneKills", data.darkOneKills);
        tag.putInt("DarkOneKillsAsDarkOne", data.darkOneKillsAsDarkOne);
        tag.putBoolean("HeartRipped", data.heartRipped);
        if (data.heartRippedBy != null) {
            tag.putUUID("HeartRippedBy", data.heartRippedBy);
        }
        tag.putBoolean("HeartProtected", data.heartProtected);
        if (data.heartProtectedBy != null) {
            tag.putUUID("HeartProtectedBy", data.heartProtectedBy);
        }
        tag.putBoolean("ConcealReady", data.concealReady);

        CompoundTag portalsTag = new CompoundTag();
        for (Map.Entry<String, PortalLocation> entry : data.savedPortals.entrySet()) {
            CompoundTag pTag = new CompoundTag();
            pTag.putString("Dimension", entry.getValue().dimension());
            pTag.putDouble("X", entry.getValue().x());
            pTag.putDouble("Y", entry.getValue().y());
            pTag.putDouble("Z", entry.getValue().z());
            portalsTag.put(entry.getKey(), pTag);
        }
        tag.put("SavedPortals", portalsTag);

        net.minecraft.nbt.ListTag spellsTag = new net.minecraft.nbt.ListTag();
        for (ResourceLocation spell : data.learnedSpells) {
            spellsTag.add(net.minecraft.nbt.StringTag.valueOf(spell.toString()));
        }
        tag.put("LearnedSpells", spellsTag);

        if (data.teacherUUID != null) {
            tag.putUUID("TeacherUUID", data.teacherUUID);
        }

        net.minecraft.nbt.ListTag cursesTag = new net.minecraft.nbt.ListTag();
        for (CurseInstance curse : data.curses) {
            cursesTag.add(curse.toTag());
        }
        tag.put("Curses", cursesTag);

        net.minecraft.nbt.ListTag blessingsTag = new net.minecraft.nbt.ListTag();
        for (BlessingInstance blessing : data.blessings) {
            blessingsTag.add(blessing.toTag());
        }
        tag.put("Blessings", blessingsTag);

        net.minecraft.nbt.ListTag artifactsTag = new net.minecraft.nbt.ListTag();
        for (ResourceLocation artifact : data.heldArtifacts) {
            artifactsTag.add(net.minecraft.nbt.StringTag.valueOf(artifact.toString()));
        }
        tag.put("HeldArtifacts", artifactsTag);

        CompoundTag ritualTag = new CompoundTag();
        for (Map.Entry<ResourceLocation, Integer> entry : data.ritualProgression.entrySet()) {
            ritualTag.putInt(entry.getKey().toString(), entry.getValue());
        }
        tag.put("RitualProgression", ritualTag);

        net.minecraft.nbt.ListTag heldRolesTag = new net.minecraft.nbt.ListTag();
        for (ResourceLocation role : data.heldRoles) {
            heldRolesTag.add(net.minecraft.nbt.StringTag.valueOf(role.toString()));
        }
        tag.put("HeldRoles", heldRolesTag);

        return tag;
    }

    public static PlayerSupernaturalData deserialize(CompoundTag tag) {
        PlayerSupernaturalData data = new PlayerSupernaturalData();

        if (tag.contains("CurrentRole")) {
            data.currentRole = ResourceLocation.parse(tag.getString("CurrentRole"));
        }
        String align = tag.getString("MagicalAlignment");
        try { data.magicalAlignment = MagicalAlignment.valueOf(align); } catch (Exception e) { data.magicalAlignment = MagicalAlignment.NONE; }
        data.magicProficiency = tag.getInt("MagicProficiency");
        data.storyProgression = tag.getInt("StoryProgression");
        data.hasHeldUniqueRole = tag.getBoolean("HasHeldUniqueRole");
        data.darkOneKills = tag.getInt("DarkOneKills");
        data.darkOneKillsAsDarkOne = tag.getInt("DarkOneKillsAsDarkOne");
        data.heartRipped = tag.getBoolean("HeartRipped");
        if (tag.hasUUID("HeartRippedBy")) {
            data.heartRippedBy = tag.getUUID("HeartRippedBy");
        }
        data.heartProtected = tag.getBoolean("HeartProtected");
        if (tag.hasUUID("HeartProtectedBy")) {
            data.heartProtectedBy = tag.getUUID("HeartProtectedBy");
        }
        data.concealReady = tag.getBoolean("ConcealReady");

        CompoundTag portalsTag = tag.getCompound("SavedPortals");
        for (String name : portalsTag.getAllKeys()) {
            CompoundTag pTag = portalsTag.getCompound(name);
            data.savedPortals.put(name, new PortalLocation(
                    pTag.getString("Dimension"),
                    pTag.getDouble("X"),
                    pTag.getDouble("Y"),
                    pTag.getDouble("Z")
            ));
        }

        net.minecraft.nbt.ListTag spellsTag = tag.getList("LearnedSpells", 8);
        for (int i = 0; i < spellsTag.size(); i++) {
            data.learnedSpells.add(ResourceLocation.parse(spellsTag.getString(i)));
        }

        if (tag.hasUUID("TeacherUUID")) {
            data.teacherUUID = tag.getUUID("TeacherUUID");
        }

        net.minecraft.nbt.ListTag cursesTag = tag.getList("Curses", 10);
        for (int i = 0; i < cursesTag.size(); i++) {
            data.curses.add(CurseInstance.fromTag(cursesTag.getCompound(i)));
        }

        net.minecraft.nbt.ListTag blessingsTag = tag.getList("Blessings", 10);
        for (int i = 0; i < blessingsTag.size(); i++) {
            data.blessings.add(BlessingInstance.fromTag(blessingsTag.getCompound(i)));
        }

        net.minecraft.nbt.ListTag artifactsTag = tag.getList("HeldArtifacts", 8);
        for (int i = 0; i < artifactsTag.size(); i++) {
            data.heldArtifacts.add(ResourceLocation.parse(artifactsTag.getString(i)));
        }

        CompoundTag ritualTag = tag.getCompound("RitualProgression");
        for (String key : ritualTag.getAllKeys()) {
            data.ritualProgression.put(ResourceLocation.parse(key), ritualTag.getInt(key));
        }

        net.minecraft.nbt.ListTag heldRolesTag = tag.getList("HeldRoles", 8);
        for (int i = 0; i < heldRolesTag.size(); i++) {
            data.heldRoles.add(ResourceLocation.parse(heldRolesTag.getString(i)));
        }

        return data;
    }

    // --- Getters and Setters ---

    public ResourceLocation getCurrentRole() { return currentRole; }
    public void setCurrentRole(ResourceLocation role) { this.currentRole = role; }

    public MagicalAlignment getMagicalAlignment() { return magicalAlignment; }
    public void setMagicalAlignment(MagicalAlignment alignment) { this.magicalAlignment = alignment; }

    public int getMagicProficiency() { return magicProficiency; }
    public void setMagicProficiency(int proficiency) { this.magicProficiency = proficiency; }
    public void addMagicProficiency(int amount) { this.magicProficiency = Math.min(100, this.magicProficiency + amount); }

    public List<ResourceLocation> getLearnedSpells() { return learnedSpells; }
    public void addSpell(ResourceLocation spell) { this.learnedSpells.add(spell); }
    public boolean hasSpell(ResourceLocation spell) { return learnedSpells.contains(spell); }

    public UUID getTeacherUUID() { return teacherUUID; }
    public void setTeacherUUID(UUID teacher) { this.teacherUUID = teacher; }

    public List<CurseInstance> getCurses() { return curses; }
    public void addCurse(CurseInstance curse) { this.curses.add(curse); }
    public void removeCurse(ResourceLocation curseId) {
        this.curses.removeIf(c -> c.getCurseId().equals(curseId));
    }
    public boolean hasCurse(ResourceLocation curseId) {
        return this.curses.stream().anyMatch(c -> c.getCurseId().equals(curseId));
    }

    public List<BlessingInstance> getBlessings() { return blessings; }
    public void addBlessing(BlessingInstance blessing) { this.blessings.add(blessing); }
    public void removeBlessing(ResourceLocation blessingId) {
        this.blessings.removeIf(b -> b.getBlessingId().equals(blessingId));
    }

    public List<ResourceLocation> getHeldArtifacts() { return heldArtifacts; }
    public void addHeldArtifact(ResourceLocation artifact) { this.heldArtifacts.add(artifact); }
    public void removeHeldArtifact(ResourceLocation artifact) { this.heldArtifacts.remove(artifact); }

    public Map<ResourceLocation, Integer> getRitualProgression() { return ritualProgression; }
    public void setRitualProgression(ResourceLocation ritual, int stage) { this.ritualProgression.put(ritual, stage); }

    public int getStoryProgression() { return storyProgression; }
    public void setStoryProgression(int progression) { this.storyProgression = progression; }
    public void advanceStoryProgression() { this.storyProgression++; }

    public boolean hasHeldUniqueRole() { return hasHeldUniqueRole; }
    public void setHasHeldUniqueRole(boolean hasHeld) { this.hasHeldUniqueRole = hasHeld; }

    public List<ResourceLocation> getHeldRoles() { return heldRoles; }
    public void addHeldRole(ResourceLocation role) {
        if (!this.heldRoles.contains(role)) {
            this.heldRoles.add(role);
            this.hasHeldUniqueRole = true;
        }
    }

    public int getDarkOneKills() { return darkOneKills; }
    public void addDarkOneKill() { this.darkOneKills++; }

    public int getDarkOneKillsAsDarkOne() { return darkOneKillsAsDarkOne; }
    public void addDarkOneKillAsDarkOne() { this.darkOneKillsAsDarkOne++; }

    public boolean isHeartRipped() { return heartRipped; }
    public UUID getHeartRippedBy() { return heartRippedBy; }
    public void setHeartRipped(boolean ripped, UUID rippedBy) {
        this.heartRipped = ripped;
        this.heartRippedBy = rippedBy;
    }

    public boolean isHeartProtected() { return heartProtected; }
    public UUID getHeartProtectedBy() { return heartProtectedBy; }
    public void setHeartProtected(boolean protected_, UUID protectedBy) {
        this.heartProtected = protected_;
        this.heartProtectedBy = protectedBy;
    }

    public boolean isConcealReady() { return concealReady; }
    public void setConcealReady(boolean ready) { this.concealReady = ready; }

    public Map<String, PortalLocation> getSavedPortals() { return savedPortals; }
    public boolean hasSavedPortals() { return !savedPortals.isEmpty(); }
    public boolean hasPortalNamed(String name) { return savedPortals.containsKey(name); }
    public PortalLocation getPortal(String name) { return savedPortals.get(name); }
    public void savePortal(String name, String dimension, double x, double y, double z) {
        this.savedPortals.put(name, new PortalLocation(dimension, x, y, z));
    }
    public boolean removePortal(String name) { return this.savedPortals.remove(name) != null; }

    public record PortalLocation(String dimension, double x, double y, double z) {}

    public boolean isMagicallyGifted() {
        return this.magicProficiency > 0 || this.currentRole != null || !this.learnedSpells.isEmpty();
    }

    public enum MagicalAlignment {
        NONE,
        LIGHT,
        DARK,
        NEUTRAL
    }
}
