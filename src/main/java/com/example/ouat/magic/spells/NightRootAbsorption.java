package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import com.example.ouat.magic.Spell.MagicType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

public class NightRootAbsorption extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "night_root_absorption");

    public NightRootAbsorption() {
        super(ID, "Night Root Absorption", 22, MagicType.DARK);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 8)) return false;

        // Night Root: absorbs darkness from target, healing caster
        net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 8.0);
        if (target == null) {
            // Self-cast: cleanse own darkness
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1));
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 0, 0));

            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        player.getX(), player.getY() + 1, player.getZ(),
                        30, 0.5, 0.5, 0.5, 0.1);
            }

            player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.5F);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou absorb the darkness within yourself, cleansing your spirit."));
            return true;
        }
        if (!(target instanceof ServerPlayer targetPlayer)) return false;

        // Absorb darkness from target
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 0));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400, 1));

        player.heal(8.0F);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        data.addMagicProficiency(1);

        if (player.level() instanceof ServerLevel serverLevel) {
            for (double d = 0; d < 5; d += 0.3) {
                Vec3 diff = player.position().subtract(targetPlayer.position()).normalize().scale(d);
                Vec3 point = targetPlayer.position().add(0, 1, 0).subtract(diff);
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        point.x, point.y, point.z,
                        3, 0.05, 0.05, 0.05, 0.01);
            }
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    player.getX(), player.getY() + 1, player.getZ(),
                    20, 0.3, 0.3, 0.3, 0.1);
        }

        player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.5F);
        targetPlayer.playSound(SoundEvents.WITHER_DEATH, 0.5F, 1.5F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou absorb darkness from " + targetPlayer.getName().getString() + ", restoring yourself!"));
        targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4You feel your darkness being drained..."));
        return true;
    }
}
