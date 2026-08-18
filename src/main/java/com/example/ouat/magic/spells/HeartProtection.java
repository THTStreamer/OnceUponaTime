package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class HeartProtection extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "heart_protection");

    public HeartProtection() {
        super(ID, "Heart Protection", 25);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 8)) return false;

        // Raycast to find target (10 block range)
        HitResult hitResult = player.pick(10.0, 0.0F, false);
        ServerPlayer targetPlayer = null;

        if (hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult entityHit) {
            if (entityHit.getEntity() instanceof ServerPlayer sp && sp != player) {
                targetPlayer = sp;
            }
        }

        // If not looking at a player, protect own heart
        if (targetPlayer == null) {
            targetPlayer = player;
        }

        PlayerSupernaturalData targetData = targetPlayer.getData(PlayerSupernaturalData.TYPE);

        // Check: can't protect a ripped heart
        if (targetData.isHeartRipped()) {
            if (targetPlayer == player) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour heart is ripped! Return it before protecting it."));
            } else {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c" + targetPlayer.getName().getString() + "'s heart is ripped! Protect it after it's returned."));
            }
            return false;
        }

        // Already protected
        if (targetData.isHeartProtected()) {
            if (targetPlayer == player) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour heart is already protected."));
            } else {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c" + targetPlayer.getName().getString() + "'s heart is already protected."));
            }
            return false;
        }

        // Apply protection
        targetData.setHeartProtected(true, player.getUUID());

        // Golden shield particles
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                    targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                    30, 0.5, 0.5, 0.5, 0.1);
            serverLevel.sendParticles(new DustColorTransitionOptions(
                    new org.joml.Vector3f(1.0f, 0.84f, 0.0f),
                    new org.joml.Vector3f(1.0f, 1.0f, 0.5f), 1.0F),
                    targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                    20, 0.3, 0.3, 0.3, 0.05);
        }

        player.playSound(SoundEvents.TOTEM_USE, 1.5F, 1.2F);
        targetPlayer.playSound(SoundEvents.TOTEM_USE, 1.5F, 1.2F);

        if (targetPlayer == player) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6§lYour heart is now protected by magic. No one can rip it from you."));
        } else {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6§lYou shield " + targetPlayer.getName().getString() + "'s heart with magic."));
            targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6§lYour heart is now protected by " + player.getName().getString() + "'s magic."));
            for (ServerPlayer other : player.level().getServer().getPlayerList().getPlayers()) {
                if (other != player && other != targetPlayer) {
                    other.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6" + player.getName().getString() + " protects " + targetPlayer.getName().getString() + "'s heart with magic."));
                }
            }
        }

        return true;
    }
}
