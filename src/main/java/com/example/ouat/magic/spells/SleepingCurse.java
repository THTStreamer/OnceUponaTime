package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.CurseInstance;
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

public class SleepingCurse extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "sleeping_curse");

    public SleepingCurse() {
        super(ID, "Sleeping Curse", 20, MagicType.DARK);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 8)) return false;

        net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 8.0);
        if (target == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNo target within range."));
            return false;
        }
        if (!(target instanceof ServerPlayer targetPlayer)) return false;

        targetPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 600, 0));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 2));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 0));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0));

        PlayerSupernaturalData targetData = targetPlayer.getData(PlayerSupernaturalData.TYPE);
        targetData.addCurse(new CurseInstance(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "sleeping_curse"),
                "Sleeping Curse", 60000, player.getUUID()
        ));

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                    40, 0.3, 0.3, 0.3, 0.05);
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    targetPlayer.getX(), targetPlayer.getY() + 2, targetPlayer.getZ(),
                    20, 0.2, 0.2, 0.2, 0.02);
            serverLevel.sendParticles(ParticleTypes.EFFECT,
                    targetPlayer.getX(), targetPlayer.getY() + 0.5, targetPlayer.getZ(),
                    30, 0.5, 0.5, 0.5, 0.1);
        }

        player.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.5F);
        targetPlayer.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.3F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou cast the Sleeping Curse on " + targetPlayer.getName().getString() + "!"));
        targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou fall into an enchanted sleep..."));
        return true;
    }
}
