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

public class MemoryWipe extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "memory_wipe");

    public MemoryWipe() {
        super(ID, "Memory Wipe", 25);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 10)) return false;

        net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 8.0);
        if (target == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNo target within range."));
            return false;
        }
        if (!(target instanceof ServerPlayer targetPlayer)) return false;

        PlayerSupernaturalData targetData = targetPlayer.getData(PlayerSupernaturalData.TYPE);
        int spellsRemoved = targetData.getLearnedSpells().size();
        targetData.getLearnedSpells().clear();
        targetData.setMagicProficiency(Math.max(0, targetData.getMagicProficiency() - 20));

        targetPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 400, 0));

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    targetPlayer.getX(), targetPlayer.getY() + 2, targetPlayer.getZ(),
                    50, 0.3, 0.3, 0.3, 0.05);
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                    40, 0.5, 0.5, 0.5, 0.02);
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    targetPlayer.getX(), targetPlayer.getY() + 1.5, targetPlayer.getZ(),
                    20, 0.3, 0.3, 0.3, 0.03);
        }

        player.playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.0F, 0.5F);
        targetPlayer.playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.0F, 0.5F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou wipe " + spellsRemoved + " spells from " + targetPlayer.getName().getString() + "'s memory!"));
        targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou feel your knowledge fading..."));
        onSuccessfulCast(player, 3);
        shiftAlignment(player, -5);
        return true;
    }
}
