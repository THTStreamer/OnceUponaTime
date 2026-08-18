package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class SquidInkParalysis extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "squid_ink_paralysis");

    public SquidInkParalysis() {
        super(ID, "Squid Ink Paralysis", 25);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 8)) return false;

        // Squid Ink: paralyzes magic users
        net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 8.0);
        if (target == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNo target within range."));
            return false;
        }
        if (!(target instanceof ServerPlayer targetPlayer)) return false;

        // Paralysis: complete immobilization + silence
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 255));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600, 255));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400, 0));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 2));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100, 0));

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                    50, 0.3, 0.3, 0.3, 0.05);
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    targetPlayer.getX(), targetPlayer.getY() + 2, targetPlayer.getZ(),
                    30, 0.2, 0.2, 0.2, 0.02);
            serverLevel.sendParticles(new net.minecraft.core.particles.DustColorTransitionOptions(
                    new org.joml.Vector3f(0.1f, 0.1f, 0.1f),
                    new org.joml.Vector3f(0.0f, 0.0f, 0.2f), 1.0F),
                    targetPlayer.getX(), targetPlayer.getY() + 0.5, targetPlayer.getZ(),
                    25, 0.3, 0.3, 0.3, 0.05);
        }

        player.playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.5F, 0.3F);
        targetPlayer.playSound(SoundEvents.GLASS_BREAK, 1.0F, 0.3F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou coat " + targetPlayer.getName().getString() + " in squid ink! Their magic is silenced!"));
        targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lThick ink covers you, silencing your magic!"));
        return true;
    }
}
