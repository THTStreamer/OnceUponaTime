package com.example.ouat.magic;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;

public class DarkMagic {
    public static final ResourceLocation DARK_CURSE = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "dark_curse");
    public static final ResourceLocation LIFE_FORCE_DRAIN = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "life_force_drain");
    public static final ResourceLocation BINDING_SPELL = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "binding_spell");
    public static final ResourceLocation DARK_ATTACK = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "dark_attack");

    public static boolean castDarkCurse(ServerPlayer caster, ServerPlayer target) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 25) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough for a dark curse."));
            return false;
        }

        PlayerSupernaturalData targetData = target.getData(PlayerSupernaturalData.TYPE);
        targetData.addCurse(new com.example.ouat.data.CurseInstance(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "weakness_curse"),
                "Dark Curse",
                60000,
                caster.getUUID()
        ));

        target.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.5F);
        target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lA dark curse afflicts you!"));

        caster.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou have cursed " + target.getName().getString() + "!"));

        return true;
    }

    public static boolean castLifeForceDrain(ServerPlayer caster, ServerPlayer target) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 35) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough for life force drain."));
            return false;
        }

        target.hurt(target.damageSources().magic(), 6.0F);
        caster.heal(4.0F);

        target.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.5F);
        target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYour life force is being drained!"));

        caster.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.5F);
        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou drain life from " + target.getName().getString() + "!"));

        return true;
    }

    public static boolean castBindingSpell(ServerPlayer caster, ServerPlayer target) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 40) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough for a binding spell."));
            return false;
        }

        PlayerSupernaturalData targetData = target.getData(PlayerSupernaturalData.TYPE);
        targetData.addCurse(new com.example.ouat.data.CurseInstance(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "binding_curse"),
                "Binding Curse",
                30000,
                caster.getUUID()
        ));

        target.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.5F);
        target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou feel bound by dark magic!"));

        caster.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou have bound " + target.getName().getString() + "!"));

        return true;
    }

    public static boolean castDarkAttack(ServerPlayer caster, ServerPlayer target) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 20) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough for a dark attack."));
            return false;
        }

        target.hurt(target.damageSources().magic(), 12.0F);
        target.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.5F);

        caster.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.5F);
        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou strike " + target.getName().getString() + " with darkness!"));

        return true;
    }
}
