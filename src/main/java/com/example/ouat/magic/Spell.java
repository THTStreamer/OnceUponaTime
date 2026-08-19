package com.example.ouat.magic;

import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.items.DarkOneDaggerItem;
import com.example.ouat.roles.DarkOneRole;
import com.example.ouat.roles.SaviorRole;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

public abstract class Spell {
    protected final ResourceLocation spellId;
    protected final String spellName;
    protected final int proficiencyRequired;
    protected final int baseFoodCost;
    protected final MagicType magicType;

    private static final Random RANDOM = new Random();

    public enum MagicType {
        DARK,
        LIGHT,
        NEUTRAL
    }

    public Spell(ResourceLocation spellId, String spellName, int proficiencyRequired, int baseFoodCost, MagicType magicType) {
        this.spellId = spellId;
        this.spellName = spellName;
        this.proficiencyRequired = proficiencyRequired;
        this.baseFoodCost = baseFoodCost;
        this.magicType = magicType;
    }

    public Spell(ResourceLocation spellId, String spellName, int proficiencyRequired, MagicType magicType) {
        this(spellId, spellName, proficiencyRequired, 6, magicType);
    }

    public Spell(ResourceLocation spellId, String spellName, int proficiencyRequired) {
        this(spellId, spellName, proficiencyRequired, 6, MagicType.NEUTRAL);
    }

    public ResourceLocation getSpellId() { return spellId; }
    public String getSpellName() { return spellName; }
    public int getProficiencyRequired() { return proficiencyRequired; }
    public int getBaseFoodCost() { return baseFoodCost; }
    public MagicType getMagicType() { return magicType; }

    /**
     * Returns the chance of successfully casting this spell (0.0 to 1.0).
     * Based on per-spell proficiency, role bonuses, and Dark One's Dagger amplification.
     */
    public float getSuccessChance(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        int prof = data.getSpellProficiency(spellId);

        // Base chance: 50% at 0 proficiency, 100% at 100 proficiency
        float chance = 0.5f + (prof / 200.0f);

        // Role-based bonuses
        if (magicType == MagicType.DARK && DarkOneRole.isDarkOne(player)) {
            chance += 0.25f;
            // Dark One's Dagger amplification
            if (isHoldingDagger(player)) {
                chance += 0.25f;
            }
        } else if (magicType == MagicType.LIGHT && SaviorRole.isSavior(player)) {
            chance += 0.25f;
        }

        return Math.min(1.0f, chance);
    }

    private boolean isHoldingDagger(ServerPlayer player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof DarkOneDaggerItem) {
                return true;
            }
        }
        return false;
    }

    /**
     * Rolls for success based on getSuccessChance. Returns true if the cast succeeds.
     */
    public boolean rollForSuccess(ServerPlayer player) {
        float chance = getSuccessChance(player);
        return RANDOM.nextFloat() < chance;
    }

    /**
     * Legacy canCast — checks if player has learned the spell.
     */
    public boolean canCast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        return data.hasSpell(spellId);
    }

    /**
     * Attempts to cast the spell with a success roll.
     * Returns true if the cast succeeded, false if it failed or player couldn't cast.
     */
    public boolean tryCast(ServerPlayer player) {
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYou haven't learned this spell."));
            return false;
        }
        if (!consumeFood(player, baseFoodCost)) return false;

        if (!rollForSuccess(player)) {
            float chance = getSuccessChance(player);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§c§lThe spell fizzles... (Success chance: " + Math.round(chance * 100) + "%)"));
            player.playSound(net.minecraft.sounds.SoundEvents.GLASS_BREAK, 0.5F, 0.5F);
            onSuccessfulCast(player, 1);
            return false;
        }

        return cast(player);
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
     */
    protected void onSuccessfulCast(ServerPlayer player, int proficiencyGain) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        data.addSpellProficiency(spellId, proficiencyGain);
        data.addMagicProficiency(1);
    }

    /**
     * Call after a successful cast to shift alignment.
     */
    protected void shiftAlignment(ServerPlayer player, int amount) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        data.shiftAlignment(amount);
    }
}
