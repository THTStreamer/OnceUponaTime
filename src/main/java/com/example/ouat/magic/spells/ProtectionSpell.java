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

public class ProtectionSpell extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "protection");

    public ProtectionSpell() {
        super(ID, "Protection Spell", 20);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 8)) return false;

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 2));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0));

        if (player.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 40; i++) {
                double angle = (i / 40.0) * Math.PI * 2;
                double x = player.getX() + Math.cos(angle) * 1.5;
                double z = player.getZ() + Math.sin(angle) * 1.5;
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                        x, player.getY() + 0.5 + (i % 5) * 0.3, z,
                        2, 0.1, 0.1, 0.1, 0.02);
            }
            serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(), player.getY() + 1.5, player.getZ(),
                    15, 0.5, 0.5, 0.5, 0.3);
        }

        player.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.5F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aA blood-bound protection envelops you!"));
        return true;
    }
}
