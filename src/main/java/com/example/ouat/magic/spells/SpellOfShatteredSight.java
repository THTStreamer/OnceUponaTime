package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.CurseInstance;
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

public class SpellOfShatteredSight extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "spell_of_shattered_sight");

    public SpellOfShatteredSight() {
        super(ID, "Spell of Shattered Sight", 40, MagicType.DARK);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 14)) return false;

        // Ingrid's spell: makes everyone turn on each other (area effect)
        for (ServerPlayer target : player.level().getServer().getPlayerList().getPlayers()) {
            if (target == player) continue;
            if (target.distanceTo(player) > 30) continue;

            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 2));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 1));
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));
            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 600, 0));
            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 600, 0));

            PlayerSupernaturalData targetData = target.getData(PlayerSupernaturalData.TYPE);
            targetData.addCurse(new CurseInstance(
                    ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "shattered_sight"),
                    "Spell of Shattered Sight", 60000, player.getUUID()
            ));

            target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§5§lYou see everyone as your enemy! Trust no one!"));
        }

        if (player.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 80; i++) {
                double angle = (i / 80.0) * Math.PI * 2;
                double radius = 2.0 + (i % 20) * 0.5;
                double x = player.getX() + Math.cos(angle) * radius;
                double z = player.getZ() + Math.sin(angle) * radius;
                serverLevel.sendParticles(new net.minecraft.core.particles.DustColorTransitionOptions(
                        new org.joml.Vector3f(0.8f, 0.2f, 0.8f),
                        new org.joml.Vector3f(0.2f, 0.0f, 0.2f), 1.0F),
                        x, player.getY() + 1.5 + Math.sin(i * 0.3) * 0.5, z,
                        5, 0.1, 0.1, 0.1, 0.02);
            }
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    player.getX(), player.getY() + 2, player.getZ(),
                    50, 5, 2, 5, 0.1);
        }

        player.playSound(SoundEvents.WITHER_DEATH, 2.0F, 0.5F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§5§lYou cast the Spell of Shattered Sight! Everyone turns on each other!"));
        return true;
    }
}
