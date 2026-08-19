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

public class MemoryRestoration extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "memory_restoration");

    public MemoryRestoration() {
        super(ID, "Memory Restoration", 25, MagicType.LIGHT);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 10)) return false;

        var curses = data.getCurses();
        if (!curses.isEmpty()) {
            for (var curse : curses) {
                data.removeCurse(curse.getCurseId());
            }
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYour memories flood back! All curses are lifted!"));
        } else {
            data.addMagicProficiency(2);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou remember a fragment of forgotten magic..."));
        }

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    player.getX(), player.getY() + 2, player.getZ(),
                    40, 0.5, 0.5, 0.5, 0.05);
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    player.getX(), player.getY() + 1, player.getZ(),
                    25, 0.3, 0.3, 0.3, 0.02);
        }

        player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.5F);
        return true;
    }
}
