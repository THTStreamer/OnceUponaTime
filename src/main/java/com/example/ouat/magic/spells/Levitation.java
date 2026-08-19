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

public class Levitation extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "levitation");

    public Levitation() {
        super(ID, "Levitation", 20, MagicType.NEUTRAL);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 8)) return false;

        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 400, 0));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 400, 2));
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 0));

        if (player.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 30; i++) {
                double angle = (i / 30.0) * Math.PI * 2;
                double x = player.getX() + Math.cos(angle) * 0.5;
                double z = player.getZ() + Math.sin(angle) * 0.5;
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                        x, player.getY() + (i % 10) * 0.2, z,
                        3, 0.05, 0.05, 0.05, 0.02);
            }
            serverLevel.sendParticles(ParticleTypes.CLOUD,
                    player.getX(), player.getY() - 0.5, player.getZ(),
                    20, 0.5, 0.1, 0.5, 0.05);
        }

        player.playSound(SoundEvents.ELYTRA_FLYING, 1.0F, 1.5F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7You rise into the air, floating like a fairy!"));
        return true;
    }
}
