package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.CurseInstance;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class CurseOfCorruption extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "curse_of_corruption");

    public CurseOfCorruption() {
        super(ID, "Curse of Corruption", 32);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 12)) return false;

        net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 8.0);
        if (target == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNo target within range."));
            return false;
        }
        if (!(target instanceof ServerPlayer targetPlayer)) return false;

        targetPlayer.addEffect(new MobEffectInstance(MobEffects.WITHER, 400, 1));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 1));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600, 1));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 400, 0));

        PlayerSupernaturalData targetData = targetPlayer.getData(PlayerSupernaturalData.TYPE);
        targetData.addCurse(new CurseInstance(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "corruption_curse"),
                "Curse of Corruption", 120000, player.getUUID()
        ));

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new DustColorTransitionOptions(
                    new org.joml.Vector3f(0, 50.0f/255, 0),
                    new org.joml.Vector3f(0, 0, 0), 1.0F),
                    targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                    40, 0.3, 0.3, 0.3, 0.05);
            serverLevel.sendParticles(ParticleTypes.WITCH,
                    targetPlayer.getX(), targetPlayer.getY() + 1.5, targetPlayer.getZ(),
                    30, 0.3, 0.3, 0.3, 0.05);
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    targetPlayer.getX(), targetPlayer.getY() + 0.5, targetPlayer.getZ(),
                    25, 0.2, 0.2, 0.2, 0.02);
        }

        player.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.5F);
        targetPlayer.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.3F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou curse " + targetPlayer.getName().getString() + " with corruption!"));
        targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lDark corruption seeps into your body..."));
        return true;
    }
}
