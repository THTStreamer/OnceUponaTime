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

public class AgeManipulation extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "age_manipulation");

    public AgeManipulation() {
        super(ID, "Age Manipulation", 35);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 14)) return false;

        net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 8.0);
        if (target == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNo target within range."));
            return false;
        }
        if (!(target instanceof ServerPlayer targetPlayer)) return false;

        boolean aging = player.getRandom().nextBoolean();

        if (aging) {
            targetPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 2));
            targetPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 2));
            targetPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600, 1));
            targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7You feel your body aging rapidly..."));
        } else {
            targetPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 1));
            targetPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));
            targetPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
            targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7You feel rejuvenated and full of vigor!"));
        }

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(aging ? ParticleTypes.ANGRY_VILLAGER : ParticleTypes.HAPPY_VILLAGER,
                    targetPlayer.getX(), targetPlayer.getY() + 1.5, targetPlayer.getZ(),
                    40, 0.5, 0.5, 0.5, 0.1);
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                    30, 0.3, 0.3, 0.3, 0.05);
        }

        player.playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.0F, 0.5F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7You manipulate the age of " + targetPlayer.getName().getString() + "!"));
        return true;
    }
}
