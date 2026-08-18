package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import com.example.ouat.registry.ModDataComponents;
import com.example.ouat.registry.ModItems;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class HeartRipping extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "heart_ripping");

    public HeartRipping() {
        super(ID, "Heart Ripping", 35);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 10)) return false;

        // Raycast to find what player is looking at (10 block range)
        HitResult hitResult = player.pick(10.0, 0.0F, false);
        ServerPlayer targetPlayer = null;

        if (hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult entityHit) {
            if (entityHit.getEntity() instanceof ServerPlayer sp && sp != player) {
                targetPlayer = sp;
            }
        }

        // If not looking at a player, rip own heart
        if (targetPlayer == null) {
            targetPlayer = player;
        }

        // Check: one heart per player — can't rip if already ripped
        PlayerSupernaturalData targetData = targetPlayer.getData(PlayerSupernaturalData.TYPE);
        if (targetData.isHeartRipped()) {
            if (targetPlayer == player) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour heart has already been ripped! It must be returned first."));
            } else {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c" + targetPlayer.getName().getString() + "'s heart has already been ripped!"));
            }
            return false;
        }

        // Check: heart protection — target's heart is shielded
        if (targetData.isHeartProtected()) {
            if (targetPlayer == player) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour heart is protected! Use /ouat cast heart_release first."));
            } else {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c" + targetPlayer.getName().getString() + "'s heart is protected by magic!"));
            }
            return false;
        }

        // Mark heart as ripped
        targetData.setHeartRipped(true, player.getUUID());

        // Create stolen heart item — no debuffs, the act is neutral
        ItemStack heartStack = new ItemStack(ModItems.STOLEN_HEART.get(), 1);
        heartStack.set(ModDataComponents.STOLEN_HEART.value(),
                new ModDataComponents.StolenHeartData(targetPlayer.getName().getString(), targetPlayer.getUUID()));

        ItemEntity heartDrop = new ItemEntity(
                targetPlayer.level(),
                targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                heartStack
        );
        targetPlayer.level().addFreshEntity(heartDrop);

        // Particles
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HEART,
                    targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                    20, 0.5, 0.5, 0.5, 0.1);
            serverLevel.sendParticles(new DustColorTransitionOptions(
                    new org.joml.Vector3f(200.0f / 255, 0, 0),
                    new org.joml.Vector3f(100.0f / 255, 0, 0), 1.0F),
                    targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                    30, 0.3, 0.3, 0.3, 0.05);
        }

        player.playSound(SoundEvents.WITHER_DEATH, 2.0F, 0.5F);
        targetPlayer.playSound(SoundEvents.WITHER_DEATH, 2.0F, 0.3F);

        if (targetPlayer == player) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou pull your own heart from your chest. You hold it carefully."));
        } else {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou rip the heart from " + targetPlayer.getName().getString() + "!"));
            targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYour heart is pulled from your chest!"));
            for (ServerPlayer other : player.level().getServer().getPlayerList().getPlayers()) {
                if (other != player && other != targetPlayer) {
                    other.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§4" + player.getName().getString() + " rips the heart from " + targetPlayer.getName().getString() + "!"));
                }
            }
        }

        return true;
    }
}
