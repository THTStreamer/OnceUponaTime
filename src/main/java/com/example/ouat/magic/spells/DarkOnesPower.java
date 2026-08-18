package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.data.UniqueRoleRegistry;
import com.example.ouat.magic.Spell;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.UUID;

public class DarkOnesPower extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "dark_ones_power");

    public DarkOnesPower() {
        super(ID, "Dark One's Power", 40);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }

        UniqueRoleRegistry registry = UniqueRoleRegistry.get(player.level().getServer());
        UUID darkOneUUID = registry.getHolder(UniqueRoleRegistry.RoleType.DARK_ONE);
        if (darkOneUUID == null || !darkOneUUID.equals(player.getUUID())) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cOnly the Dark One can channel this power."));
            return false;
        }

        if (!consumeFood(player, 14)) return false;

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 2));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0));
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));

        if (player.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 50; i++) {
                double angle = (i / 50.0) * Math.PI * 2;
                double radius = 0.5 + (i % 15) * 0.15;
                double x = player.getX() + Math.cos(angle) * radius;
                double z = player.getZ() + Math.sin(angle) * radius;
                serverLevel.sendParticles(new DustColorTransitionOptions(
                        new org.joml.Vector3f(80.0f/255, 0, 120.0f/255),
                        new org.joml.Vector3f(0, 0, 0), 1.0F),
                        x, player.getY() + 0.5 + (i % 10) * 0.2, z,
                        3, 0.05, 0.05, 0.05, 0.02);
            }
            serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH,
                    player.getX(), player.getY() + 1.5, player.getZ(),
                    30, 0.5, 0.5, 0.5, 0.05);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    20, 0.3, 0.3, 0.3, 0.02);
        }

        player.playSound(SoundEvents.WITHER_DEATH, 2.0F, 0.3F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lThe darkness surges through you! You feel its immense power!"));
        return true;
    }
}
