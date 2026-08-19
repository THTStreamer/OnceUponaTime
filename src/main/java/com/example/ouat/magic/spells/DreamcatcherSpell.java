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

public class DreamcatcherSpell extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "dreamcatcher_spell");

    public DreamcatcherSpell() {
        super(ID, "Dreamcatcher Spell", 20, MagicType.NEUTRAL);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 8)) return false;

        // Rumple's dreamcatcher: view target's memories
        net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 8.0);
        if (target == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNo target within range."));
            return false;
        }
        if (!(target instanceof ServerPlayer targetPlayer)) return false;

        // Reveal target's role/identity
        PlayerSupernaturalData targetData = targetPlayer.getData(PlayerSupernaturalData.TYPE);
        int targetProficiency = targetData.getMagicProficiency();

        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§d§lYou peer into " + targetPlayer.getName().getString() + "'s memories..."));
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7Magic Proficiency: " + targetProficiency));
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7Spells Known: " + targetData.getLearnedSpells().size()));
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7Curses: " + targetData.getCurses().size()));

        targetPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    targetPlayer.getX(), targetPlayer.getY() + 2, targetPlayer.getZ(),
                    40, 0.5, 0.5, 0.5, 0.05);
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                    30, 0.3, 0.3, 0.3, 0.02);
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    player.getX(), player.getY() + 2, player.getZ(),
                    25, 0.3, 0.3, 0.3, 0.05);
        }

        player.playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.0F, 1.5F);
        targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§5You feel someone probing your memories..."));
        return true;
    }
}
