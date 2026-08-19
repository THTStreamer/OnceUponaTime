package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import com.example.ouat.magic.Spell.MagicType;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class ShapeshiftingSpell extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "shapeshifting");

    public ShapeshiftingSpell() {
        super(ID, "Shapeshifting", 25, MagicType.NEUTRAL);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 10)) return false;

        net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 10.0);
        if (target == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNo target to shift into."));
            return false;
        }
        if (target == player) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYou cannot shift into yourself."));
            return false;
        }

        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 600, 0));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new DustColorTransitionOptions(
                    new org.joml.Vector3f(200.0f/255, 150.0f/255, 255.0f/255),
                    new org.joml.Vector3f(100.0f/255, 50.0f/255, 200.0f/255), 1.0F),
                    player.getX(), player.getY() + 1, player.getZ(),
                    60, 0.5, 0.5, 0.5, 0.05);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    30, 0.3, 0.3, 0.3, 0.05);
        }

        player.playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.0F, 1.0F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7You shift your form to mimic " + target.getName().getString() + "!"));
        target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7You feel someone channeling your appearance..."));
        return true;
    }
}
