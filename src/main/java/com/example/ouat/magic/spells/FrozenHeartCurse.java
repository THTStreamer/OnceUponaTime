package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.CurseInstance;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class FrozenHeartCurse extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "frozen_heart_curse");

    public FrozenHeartCurse() {
        super(ID, "Frozen Heart Curse", 30);
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

        // Ingrid's ribbon curse: gradually freezes the heart
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 800, 2));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 800, 2));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600, 1));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 400, 0));

        PlayerSupernaturalData targetData = targetPlayer.getData(PlayerSupernaturalData.TYPE);
        targetData.addCurse(new CurseInstance(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "frozen_heart"),
                "Frozen Heart Curse", 80000, player.getUUID()
        ));

        if (player.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 40; i++) {
                double angle = (i / 40.0) * Math.PI * 2;
                double x = targetPlayer.getX() + Math.cos(angle) * 0.6;
                double z = targetPlayer.getZ() + Math.sin(angle) * 0.6;
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                        x, targetPlayer.getY() + 1.0 + (i % 8) * 0.2, z,
                        3, 0.05, 0.05, 0.05, 0.02);
            }
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                    30, 0.3, 0.3, 0.3, 0.05);
        }

        player.playSound(SoundEvents.GLASS_BREAK, 1.5F, 0.3F);
        targetPlayer.playSound(SoundEvents.GLASS_BREAK, 1.5F, 0.3F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§b§lYou curse " + targetPlayer.getName().getString() + "'s heart to freeze!"));
        targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§b§lYou feel your heart growing colder..."));
        return true;
    }
}
