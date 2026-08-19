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
import net.minecraft.world.level.levelgen.Heightmap;

public class Banishment extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "banishment");

    public Banishment() {
        super(ID, "Banishment", 28, MagicType.DARK);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 12)) return false;

        net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 8.0);
        if (target == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNo target within range."));
            return false;
        }
        if (!(target instanceof ServerPlayer targetPlayer)) return false;

        if (player.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 40; i++) {
                double angle = (i / 40.0) * Math.PI * 2;
                double x = targetPlayer.getX() + Math.cos(angle) * 1.0;
                double z = targetPlayer.getZ() + Math.sin(angle) * 1.0;
                serverLevel.sendParticles(new DustColorTransitionOptions(
                        new org.joml.Vector3f(100.0f/255, 0, 150.0f/255),
                        new org.joml.Vector3f(0, 0, 0), 1.0F),
                        x, targetPlayer.getY() + 0.5 + (i % 10) * 0.2, z,
                        5, 0.1, 0.1, 0.1, 0.02);
            }
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    targetPlayer.getX(), targetPlayer.getY() + 1.5, targetPlayer.getZ(),
                    30, 0.3, 0.3, 0.3, 0.1);
        }

        int x = (int) (targetPlayer.getX() + targetPlayer.getRandom().nextIntBetweenInclusive(-500, 500));
        int z = (int) (targetPlayer.getZ() + targetPlayer.getRandom().nextIntBetweenInclusive(-500, 500));
        int y = targetPlayer.level().getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

        targetPlayer.teleportTo(x + 0.5, y + 1, z + 0.5);

        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 0.5F);
        targetPlayer.playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 0.3F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou banish " + targetPlayer.getName().getString() + " to the edges of the realm!"));
        targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou are banished by dark magic!"));
        return true;
    }
}
