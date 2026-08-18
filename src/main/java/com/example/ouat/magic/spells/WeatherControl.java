package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;

public class WeatherControl extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "weather_control");

    public WeatherControl() {
        super(ID, "Weather Control", 18);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 8)) return false;

        ServerLevel level = (ServerLevel) player.level();
        if (!level.isRaining() && !level.isThundering()) {
            level.setWeatherParameters(0, 6000, false, true);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                        player.getX(), player.getY() + 5, player.getZ(),
                        100, 5, 2, 5, 0.1);
            }
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7You conjure rain from the sky..."));
        } else if (level.isRaining() && !level.isThundering()) {
            level.setWeatherParameters(0, 6000, true, true);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        player.getX(), player.getY() + 10, player.getZ(),
                        80, 5, 1, 5, 0.2);
            }
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7You summon a thunderstorm!"));
        } else {
            level.setWeatherParameters(6000, 0, false, false);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        player.getX(), player.getY() + 3, player.getZ(),
                        50, 3, 1, 3, 0.1);
            }
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7The skies clear at your command."));
        }

        player.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 1.0F, 1.0F);
        return true;
    }
}
