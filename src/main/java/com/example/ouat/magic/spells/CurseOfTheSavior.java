package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.CurseInstance;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.data.UniqueRoleRegistry;
import com.example.ouat.magic.Spell;
import com.example.ouat.magic.Spell.MagicType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.UUID;

public class CurseOfTheSavior extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "curse_of_the_savior");

    public CurseOfTheSavior() {
        super(ID, "Curse of the Savior", 45, MagicType.DARK);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 14)) return false;

        // Zelena's curse: turns the Savior dark
        net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 8.0);
        if (target == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNo target within range."));
            return false;
        }
        if (!(target instanceof ServerPlayer targetPlayer)) return false;

        // Corruption: wither + weakness + darkness + confusion
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.WITHER, 600, 2));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 2));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 600, 0));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 600, 0));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 400, 1));

        PlayerSupernaturalData targetData = targetPlayer.getData(PlayerSupernaturalData.TYPE);
        targetData.addCurse(new CurseInstance(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "savior_curse"),
                "Curse of the Savior", 120000, player.getUUID()
        ));
        // Reduce their light magic proficiency
        targetData.setMagicProficiency(Math.max(0, targetData.getMagicProficiency() - 30));

        if (player.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 50; i++) {
                double angle = (i / 50.0) * Math.PI * 2;
                double radius = 1.0;
                double x = targetPlayer.getX() + Math.cos(angle) * radius;
                double z = targetPlayer.getZ() + Math.sin(angle) * radius;
                serverLevel.sendParticles(new net.minecraft.core.particles.DustColorTransitionOptions(
                        new org.joml.Vector3f(0.0f, 0.8f, 0.0f),
                        new org.joml.Vector3f(0.0f, 0.0f, 0.0f), 1.0F),
                        x, targetPlayer.getY() + 0.5 + (i % 10) * 0.2, z,
                        3, 0.05, 0.05, 0.05, 0.02);
            }
            serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH,
                    targetPlayer.getX(), targetPlayer.getY() + 1.5, targetPlayer.getZ(),
                    40, 0.5, 0.5, 0.5, 0.1);
        }

        player.playSound(SoundEvents.WITHER_DEATH, 2.0F, 0.3F);
        targetPlayer.playSound(SoundEvents.WITHER_DEATH, 2.0F, 0.3F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lThe darkness corrupts " + targetPlayer.getName().getString() + "! Their light fades!"));
        targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lDarkness engulfs you... your power is being drained!"));
        return true;
    }
}
