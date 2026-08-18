package com.example.ouat.magic;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;

public class ProtectionMagic {
    public static final ResourceLocation PERSONAL_SHIELD = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "personal_shield");
    public static final ResourceLocation CURSE_RESISTANCE = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "curse_resistance");
    public static final ResourceLocation DARK_MAGIC_RESISTANCE = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "dark_magic_resistance");
    public static final ResourceLocation BARRIER_SPELL = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "barrier_spell");

    public static boolean castPersonalShield(ServerPlayer caster) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 25) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough for a personal shield."));
            return false;
        }

        caster.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE, 600, 1, false, true
        ));

        caster.playSound(SoundEvents.ANVIL_USE, 1.0F, 1.5F);
        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aA magical shield surrounds you!"));

        return true;
    }

    public static boolean castCurseResistance(ServerPlayer caster) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 35) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough for curse resistance."));
            return false;
        }

        caster.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.REGENERATION, 600, 0, false, true
        ));

        caster.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.5F);
        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou feel protected from curses!"));

        return true;
    }

    public static boolean castDarkMagicResistance(ServerPlayer caster) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 40) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough for dark magic resistance."));
            return false;
        }

        caster.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE, 600, 0, false, true
        ));

        caster.playSound(SoundEvents.TOTEM_USE, 1.0F, 2.0F);
        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aDark magic resistance courses through you!"));

        return true;
    }

    public static boolean castBarrier(ServerPlayer caster, int durationTicks) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 50) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough for a barrier spell."));
            return false;
        }

        caster.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.ABSORPTION, durationTicks, 2, false, true
        ));

        caster.playSound(SoundEvents.BEACON_ACTIVATE, 1.0F, 1.0F);
        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aA powerful barrier surrounds you!"));

        return true;
    }
}
