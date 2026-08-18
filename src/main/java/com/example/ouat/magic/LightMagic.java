package com.example.ouat.magic;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;

public class LightMagic {
    public static final ResourceLocation HEALING = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "healing");
    public static final ResourceLocation PURIFICATION = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "purification");
    public static final ResourceLocation CURSE_REMOVAL = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "curse_removal");
    public static final ResourceLocation LIGHT_ATTACK = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "light_attack");

    public static boolean castHealing(ServerPlayer caster, ServerPlayer target) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 15) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough for healing."));
            return false;
        }

        target.heal(10.0F);
        target.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.5F);
        target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou have been healed by " + caster.getName().getString() + "!"));

        caster.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou have healed " + target.getName().getString() + "!"));

        return true;
    }

    public static boolean castPurification(ServerPlayer caster, ServerPlayer target) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 30) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough for purification."));
            return false;
        }

        PlayerSupernaturalData targetData = target.getData(PlayerSupernaturalData.TYPE);
        var curses = targetData.getCurses();

        if (curses.isEmpty()) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cThere are no curses to purify."));
            return false;
        }

        for (var curse : curses) {
            targetData.removeCurse(curse.getCurseId());
        }

        target.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.5F);
        target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aAll curses have been purified from you!"));

        caster.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou have purified " + target.getName().getString() + "!"));

        return true;
    }

    public static boolean castCurseRemoval(ServerPlayer caster, ServerPlayer target, ResourceLocation curseId) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 25) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough for curse removal."));
            return false;
        }

        PlayerSupernaturalData targetData = target.getData(PlayerSupernaturalData.TYPE);
        if (!targetData.hasCurse(curseId)) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cThat curse is not present."));
            return false;
        }

        targetData.removeCurse(curseId);

        target.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.5F);
        target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aA curse has been removed from you!"));

        caster.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou have removed a curse from " + target.getName().getString() + "!"));

        return true;
    }

    public static boolean castLightAttack(ServerPlayer caster, ServerPlayer target) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 20) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough for a light attack."));
            return false;
        }

        target.hurt(target.damageSources().magic(), 8.0F);
        target.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 1.0F, 2.0F);

        caster.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou strike " + target.getName().getString() + " with light!"));

        return true;
    }
}
