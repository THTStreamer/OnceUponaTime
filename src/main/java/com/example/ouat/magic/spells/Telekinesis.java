package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Telekinesis extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "telekinesis");

    public Telekinesis() {
        super(ID, "Telekinesis", 10);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 4)) return false;

        double yRot = Math.toRadians(player.getYRot());
        double xRot = Math.toRadians(player.getXRot());
        Vec3 look = new Vec3(-Math.sin(yRot) * Math.cos(xRot), -Math.sin(xRot), Math.cos(yRot) * Math.cos(xRot));

        int pushed = 0;
        for (var entity : player.level().getEntities(player, player.getBoundingBox().inflate(10.0))) {
            if (entity instanceof Player) continue;
            Vec3 diff = entity.position().subtract(player.position());
            double dist = diff.length();
            if (dist > 0 && dist < 10) {
                entity.push(look.x * 3.0 / dist, 0.5, look.z * 3.0 / dist);
                pushed++;
            }
        }

        if (player.level() instanceof ServerLevel serverLevel) {
            Vec3 origin = player.position().add(0, player.getEyeHeight(), 0);
            for (double d = 0; d < 10; d += 0.5) {
                Vec3 point = origin.add(look.scale(d));
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                        point.x, point.y, point.z,
                        3, 0.1, 0.1, 0.1, 0.02);
            }
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    player.getX(), player.getY() + 1, player.getZ(),
                    20, 0.3, 0.3, 0.3, 0.02);
        }

        player.playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.0F, 1.5F);
        if (pushed > 0) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou push " + pushed + " entities with your mind!"));
        } else {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou reach out with your mind, but find nothing to move."));
        }
        return true;
    }
}
