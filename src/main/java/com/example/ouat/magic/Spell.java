package com.example.ouat.magic;

import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public abstract class Spell {
    protected final ResourceLocation spellId;
    protected final String spellName;
    protected final int proficiencyRequired;
    protected final int baseFoodCost;

    public Spell(ResourceLocation spellId, String spellName, int proficiencyRequired, int baseFoodCost) {
        this.spellId = spellId;
        this.spellName = spellName;
        this.proficiencyRequired = proficiencyRequired;
        this.baseFoodCost = baseFoodCost;
    }

    public Spell(ResourceLocation spellId, String spellName, int proficiencyRequired) {
        this(spellId, spellName, proficiencyRequired, 6);
    }

    public ResourceLocation getSpellId() { return spellId; }
    public String getSpellName() { return spellName; }
    public int getProficiencyRequired() { return proficiencyRequired; }
    public int getBaseFoodCost() { return baseFoodCost; }

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
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        float multiplier = data.getFoodCostMultiplier(spellId);
        int actualCost = Math.max(1, Math.round(amount * multiplier));
        if (player.getFoodData().getFoodLevel() < actualCost) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cMagic requires a price... You are too hungry."));
            return false;
        }
        player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() - actualCost);
        return true;
    }

    /**
     * Call after a successful cast to grant proficiency and alignment changes.
     * Subclasses should call this at the end of cast().
     */
    protected void onSuccessfulCast(ServerPlayer player, int proficiencyGain) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        data.addSpellProficiency(spellId, proficiencyGain);
    }

    /**
     * Call after a successful cast to shift alignment.
     */
    protected void shiftAlignment(ServerPlayer player, int amount) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        data.shiftAlignment(amount);
    }
}
