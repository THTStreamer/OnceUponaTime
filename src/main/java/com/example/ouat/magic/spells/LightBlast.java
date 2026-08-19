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
import net.minecraft.world.phys.Vec3;

public class LightBlast extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "light_blast");

    public LightBlast() {
        super(ID, "Light Blast", 15, MagicType.LIGHT);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 4)) return false;

        net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 12.0);
        if (target == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNo target within range."));
            return false;
        }
        if (!(target instanceof ServerPlayer targetPlayer)) return false;

        targetPlayer.hurt(targetPlayer.damageSources().magic(), 10.0F);
        targetPlayer.knockback(1.5, player.getX() - targetPlayer.getX(), player.getZ() - targetPlayer.getZ());

        if (player.level() instanceof ServerLevel serverLevel) {
            Vec3 origin = player.position().add(0, player.getEyeHeight(), 0);
            Vec3 targetPos = targetPlayer.position().add(0, targetPlayer.getEyeHeight(), 0);
            Vec3 dir = targetPos.subtract(origin).normalize();

            for (double d = 0; d < 5; d += 0.3) {
                Vec3 point = origin.add(dir.scale(d));
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                        point.x, point.y, point.z,
                        3, 0.05, 0.05, 0.05, 0.02);
            }

            serverLevel.sendParticles(ParticleTypes.FLASH,
                    targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                    5, 0.3, 0.3, 0.3, 0.1);
        }

        player.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 1.0F, 2.0F);
        targetPlayer.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 1.0F, 0.5F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou blast " + targetPlayer.getName().getString() + " with a surge of light!"));
        targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYou are struck by a blast of light!"));
        return true;
    }
}
