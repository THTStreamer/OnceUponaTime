package com.example.ouat.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class MagicEffects {

    public static void spawnParticles(ServerLevel level, Vec3 pos, ParticleOptions particle, int count, double spreadX, double spreadY, double spreadZ) {
        for (int i = 0; i < count; i++) {
            double x = pos.x + (level.random.nextDouble() - 0.5) * spreadX;
            double y = pos.y + level.random.nextDouble() * spreadY;
            double z = pos.z + (level.random.nextDouble() - 0.5) * spreadZ;
            level.addParticle(particle, x, y, z, 0, 0, 0);
        }
    }

    public static void playSound(ServerLevel level, Vec3 pos, SoundEvent sound, SoundSource source, float volume, float pitch) {
        level.playSound(null, pos.x, pos.y, pos.z, sound, source, volume, pitch);
    }

    public static void sendTitle(ServerPlayer player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6§l" + title));
        if (subtitle != null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7" + subtitle));
        }
    }

    public static void broadcastMessage(ServerLevel level, String message, double range) {
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (player.distanceToSqr(Vec3.ZERO) < range * range) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(message));
            }
        }
    }

    public static void createMagicCircle(ServerLevel level, Vec3 center, int radius, ParticleOptions particle) {
        for (int i = 0; i < 360; i += 10) {
            double angle = Math.toRadians(i);
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            level.addParticle(particle, x, center.y, z, 0, 0.1, 0);
        }
    }

    public static void createLightningEffect(ServerLevel level, Vec3 pos) {
        level.addParticle(net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y, pos.z, 0, 0, 0);
        level.playSound(null, pos.x, pos.y, pos.z, net.minecraft.sounds.SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 1.0F, 2.0F);
    }

    public static void createDarknessEffect(ServerLevel level, Vec3 pos) {
        for (int i = 0; i < 30; i++) {
            double x = pos.x + (level.random.nextDouble() - 0.5) * 6;
            double y = pos.y + level.random.nextDouble() * 4;
            double z = pos.z + (level.random.nextDouble() - 0.5) * 6;
            level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, x, y, z, 0, 0.1, 0);
        }
    }

    public static void createLightEffect(ServerLevel level, Vec3 pos) {
        for (int i = 0; i < 30; i++) {
            double x = pos.x + (level.random.nextDouble() - 0.5) * 6;
            double y = pos.y + level.random.nextDouble() * 4;
            double z = pos.z + (level.random.nextDouble() - 0.5) * 6;
            level.addParticle(net.minecraft.core.particles.ParticleTypes.ENCHANT, x, y, z, 0, 0.1, 0);
        }
    }
}
