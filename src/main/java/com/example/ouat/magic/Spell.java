package com.example.ouat.magic;

import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public abstract class Spell {
    protected final ResourceLocation spellId;
    protected final String spellName;
    protected final int proficiencyRequired;

    public Spell(ResourceLocation spellId, String spellName, int proficiencyRequired) {
        this.spellId = spellId;
        this.spellName = spellName;
        this.proficiencyRequired = proficiencyRequired;
    }

    public ResourceLocation getSpellId() { return spellId; }
    public String getSpellName() { return spellName; }
    public int getProficiencyRequired() { return proficiencyRequired; }

    public boolean canCast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        return data.getMagicProficiency() >= proficiencyRequired;
    }

    public abstract boolean cast(ServerPlayer player);

    public boolean cast(ServerPlayer player, String argument) {
        return cast(player);
    }

    protected boolean consumeFood(ServerPlayer player, int amount) {
        if (player.isCreative()) return true;
        if (player.getFoodData().getFoodLevel() < amount) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cMagic requires a price... You are too hungry."));
            return false;
        }
        player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() - amount);
        return true;
    }
}
