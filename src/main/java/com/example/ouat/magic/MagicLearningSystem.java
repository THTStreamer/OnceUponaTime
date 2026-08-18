package com.example.ouat.magic;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class MagicLearningSystem {

    public static boolean teachSpell(ServerPlayer teacher, ServerPlayer student, ResourceLocation spellId) {
        Spell spell = SpellRegistry.getSpell(spellId);
        if (spell == null) {
            OnceUponATime.LOGGER.warn("Spell {} not found", spellId);
            return false;
        }

        PlayerSupernaturalData teacherData = teacher.getData(PlayerSupernaturalData.TYPE);
        PlayerSupernaturalData studentData = student.getData(PlayerSupernaturalData.TYPE);

        if (!teacherData.hasSpell(spellId)) {
            teacher.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYou don't know this spell."));
            return false;
        }

        if (studentData.hasSpell(spellId)) {
            student.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYou already know this spell."));
            return false;
        }

        if (studentData.getMagicProficiency() < spell.getProficiencyRequired()) {
            student.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic proficiency is too low. Required: " + spell.getProficiencyRequired()));
            return false;
        }

        studentData.addSpell(spellId);
        student.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou have learned " + spell.getSpellName() + "!"));
        teacher.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou have taught " + spell.getSpellName() + " to " + student.getName().getString() + "."));

        OnceUponATime.LOGGER.info("{} taught {} to {}", teacher.getName().getString(), spell.getSpellName(), student.getName().getString());
        return true;
    }

    public static boolean absorbGrimoire(ServerPlayer player, ResourceLocation grimoireId) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);

        if (!data.isMagicallyGifted()) {
            data.setMagicalAlignment(PlayerSupernaturalData.MagicalAlignment.NEUTRAL);
        }

        data.addMagicProficiency(10);

        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou have absorbed magical knowledge from the grimoire!"));

        OnceUponATime.LOGGER.info("{} absorbed grimoire {}", player.getName().getString(), grimoireId);
        return true;
    }

    public static boolean gainProficiency(ServerPlayer player, int amount) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        int oldProficiency = data.getMagicProficiency();
        data.addMagicProficiency(amount);
        int newProficiency = data.getMagicProficiency();

        if (newProficiency > oldProficiency) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYour magic proficiency increased to " + newProficiency + "!"));
            return true;
        }
        return false;
    }
}
