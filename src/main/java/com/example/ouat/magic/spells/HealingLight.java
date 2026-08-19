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

public class HealingLight extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "healing_light");

    public HealingLight() {
        super(ID, "Healing Light", 10);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 6)) return false;

        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 1));
        player.heal(6.0F);

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    player.getX(), player.getY() + 1, player.getZ(),
                    30, 0.5, 0.5, 0.5, 0.1);
            serverLevel.sendParticles(ParticleTypes.GLOW,
                    player.getX(), player.getY() + 1.5, player.getZ(),
                    20, 0.3, 0.3, 0.3, 0.05);
        }

        player.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.5F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou channel the light of true love and heal your wounds!"));
        onSuccessfulCast(player, 3);
        shiftAlignment(player, 5);
        return true;
    }
}
