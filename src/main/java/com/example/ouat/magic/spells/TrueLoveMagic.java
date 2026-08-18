package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.BlessingInstance;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;

public class TrueLoveMagic extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "true_love_magic");

    public TrueLoveMagic() {
        super(ID, "True Love's Kiss", 30);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 12)) return false;

        boolean hadCurses = !data.getCurses().isEmpty();
        for (var curse : data.getCurses()) {
            data.removeCurse(curse.getCurseId());
        }

        player.heal(20.0F);
        data.addBlessing(new BlessingInstance(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "true_love_blessing"),
                "True Love's Blessing",
                1200000,
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "true_love")
        ));

        if (player.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 60; i++) {
                double angle = (i / 60.0) * Math.PI * 2;
                double radius = 1.0 + (i % 10) * 0.1;
                double x = player.getX() + Math.cos(angle) * radius;
                double z = player.getZ() + Math.sin(angle) * radius;
                serverLevel.sendParticles(ParticleTypes.HEART,
                        x, player.getY() + 1.0 + Math.sin(i * 0.5) * 0.5, z,
                        2, 0.05, 0.05, 0.05, 0.02);
            }
            serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(), player.getY() + 1.5, player.getZ(),
                    20, 0.8, 0.8, 0.8, 0.5);
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    player.getX(), player.getY() + 1, player.getZ(),
                    30, 0.5, 0.5, 0.5, 0.1);
        }

        player.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.5F);
        if (hadCurses) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§d§lTrue Love's Kiss breaks all curses! You are free!"));
        } else {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§d§lThe power of true love fills you with warmth and strength!"));
        }
        return true;
    }
}
