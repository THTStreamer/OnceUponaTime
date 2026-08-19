package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.UUID;

public class HeartRelease extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "heart_release");

    public HeartRelease() {
        super(ID, "Heart Release", 25);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 6)) return false;

        // Raycast to find target (10 block range)
        HitResult hitResult = player.pick(10.0, 0.0F, false);
        ServerPlayer targetPlayer = null;

        if (hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult entityHit) {
            if (entityHit.getEntity() instanceof ServerPlayer sp && sp != player) {
                targetPlayer = sp;
            }
        }

        // If not looking at a player, release own heart protection
        if (targetPlayer == null) {
            targetPlayer = player;
        }

        PlayerSupernaturalData targetData = targetPlayer.getData(PlayerSupernaturalData.TYPE);

        if (!targetData.isHeartProtected()) {
            if (targetPlayer == player) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour heart is not protected."));
            } else {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c" + targetPlayer.getName().getString() + "'s heart is not protected."));
            }
            return false;
        }

        // Only the caster who applied the protection (or the heart owner) can remove it
        UUID protector = targetData.getHeartProtectedBy();
        boolean isOwner = targetPlayer == player;
        boolean isProtector = protector != null && protector.equals(player.getUUID());
        if (!isOwner && !isProtector) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cOnly " + targetPlayer.getName().getString() + " or the one who cast the protection can remove it."));
            return false;
        }

        // Remove protection
        targetData.setHeartProtected(false, null);

        // Smoke particles (protection fading)
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                    15, 0.3, 0.3, 0.3, 0.05);
        }

        player.playSound(SoundEvents.GLASS_BREAK, 1.0F, 1.5F);
        targetPlayer.playSound(SoundEvents.GLASS_BREAK, 1.0F, 1.5F);

        if (targetPlayer == player) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7You release the protection on your heart."));
        } else {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7You release the protection on " + targetPlayer.getName().getString() + "'s heart."));
            targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7The protection on your heart has been released."));
        }

        onSuccessfulCast(player, 2);
        shiftAlignment(player, 10);
        return true;
    }
}
