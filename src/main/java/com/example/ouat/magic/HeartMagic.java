package com.example.ouat.magic;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;

public class HeartMagic {
    public static final ResourceLocation HEART_RIP = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "heart_rip");
    public static final ResourceLocation HEART_CONTROL = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "heart_control");
    public static final ResourceLocation HEART_CRUSH = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "heart_crush");
    public static final ResourceLocation HEART_RESTORE = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "heart_restore");

    public static boolean ripHeart(ServerPlayer caster, ServerPlayer target) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 30) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough to rip a heart."));
            return false;
        }

        PlayerSupernaturalData targetData = target.getData(PlayerSupernaturalData.TYPE);
        if (targetData.hasCurse(ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "heart_ripped"))) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cTheir heart has already been ripped."));
            return false;
        }

        targetData.addCurse(new com.example.ouat.data.CurseInstance(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "heart_ripped"),
                "Heart Ripped",
                -1,
                caster.getUUID()
        ));

        target.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.5F);
        target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYour heart has been ripped from your chest!"));

        caster.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou have ripped " + target.getName().getString() + "'s heart!"));

        return true;
    }

    public static boolean controlHeart(ServerPlayer caster, ServerPlayer target, String command) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 50) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough to control a heart."));
            return false;
        }

        PlayerSupernaturalData targetData = target.getData(PlayerSupernaturalData.TYPE);
        if (!targetData.hasCurse(ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "heart_ripped"))) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYou must rip their heart first."));
            return false;
        }

        target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou feel your heart being manipulated..."));
        target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§l" + command));

        return true;
    }

    public static boolean crushHeart(ServerPlayer caster, ServerPlayer target) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 70) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough to crush a heart."));
            return false;
        }

        PlayerSupernaturalData targetData = target.getData(PlayerSupernaturalData.TYPE);
        if (!targetData.hasCurse(ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "heart_ripped"))) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYou must rip their heart first."));
            return false;
        }

        target.kill();
        targetData.removeCurse(ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "heart_ripped"));

        target.playSound(SoundEvents.GENERIC_EXPLODE.value(), 2.0F, 0.5F);
        caster.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou have crushed " + target.getName().getString() + "'s heart!"));

        return true;
    }

    public static boolean restoreHeart(ServerPlayer caster, ServerPlayer target) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 40) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough to restore a heart."));
            return false;
        }

        PlayerSupernaturalData targetData = target.getData(PlayerSupernaturalData.TYPE);
        if (!targetData.hasCurse(ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "heart_ripped"))) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cTheir heart is not ripped."));
            return false;
        }

        targetData.removeCurse(ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "heart_ripped"));

        target.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);
        target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYour heart has been restored!"));

        caster.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou have restored " + target.getName().getString() + "'s heart!"));

        return true;
    }
}
