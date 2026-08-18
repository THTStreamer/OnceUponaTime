package com.example.ouat.magic;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TeleportationMagic {
    public static final ResourceLocation TELEPORTATION_MIST = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "teleportation_mist");
    public static final ResourceLocation PLAYER_TELEPORT = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "player_teleport");
    public static final ResourceLocation SELF_TELEPORT = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "self_teleport");

    public static boolean castMist(ServerPlayer caster, ServerPlayer target) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 20) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough for teleportation mist."));
            return false;
        }

        Level level = caster.level();
        Vec3 pos = caster.position();

        for (int i = 0; i < 50; i++) {
            double x = pos.x + (level.random.nextDouble() - 0.5) * 4;
            double y = pos.y + level.random.nextDouble() * 3;
            double z = pos.z + (level.random.nextDouble() - 0.5) * 4;
            level.addParticle(net.minecraft.core.particles.ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 0, 0.1, 0);
        }

        level.playSound(null, caster.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.5F);

        caster.teleportTo(target.getX(), target.getY(), target.getZ());

        level.playSound(null, caster.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.5F);

        for (int i = 0; i < 50; i++) {
            double x = caster.getX() + (level.random.nextDouble() - 0.5) * 4;
            double y = caster.getY() + level.random.nextDouble() * 3;
            double z = caster.getZ() + (level.random.nextDouble() - 0.5) * 4;
            level.addParticle(net.minecraft.core.particles.ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 0, 0.1, 0);
        }

        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou teleport through the mist to " + target.getName().getString() + "!"));
        target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§a" + caster.getName().getString() + " appears from the mist!"));

        return true;
    }

    public static boolean teleportToLocation(ServerPlayer caster, double x, double y, double z) {
        PlayerSupernaturalData casterData = caster.getData(PlayerSupernaturalData.TYPE);
        if (casterData.getMagicProficiency() < 15) {
            caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough for self teleportation."));
            return false;
        }

        Level level = caster.level();
        Vec3 pos = caster.position();

        for (int i = 0; i < 30; i++) {
            double px = pos.x + (level.random.nextDouble() - 0.5) * 3;
            double py = pos.y + level.random.nextDouble() * 2;
            double pz = pos.z + (level.random.nextDouble() - 0.5) * 3;
            level.addParticle(net.minecraft.core.particles.ParticleTypes.CAMPFIRE_COSY_SMOKE, px, py, pz, 0, 0.1, 0);
        }

        level.playSound(null, caster.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.5F);

        caster.teleportTo(x, y, z);

        level.playSound(null, caster.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.5F);

        for (int i = 0; i < 30; i++) {
            double px = caster.getX() + (level.random.nextDouble() - 0.5) * 3;
            double py = caster.getY() + level.random.nextDouble() * 2;
            double pz = caster.getZ() + (level.random.nextDouble() - 0.5) * 3;
            level.addParticle(net.minecraft.core.particles.ParticleTypes.CAMPFIRE_COSY_SMOKE, px, py, pz, 0, 0.1, 0);
        }

        caster.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou teleport through the mist!"));

        return true;
    }
}
