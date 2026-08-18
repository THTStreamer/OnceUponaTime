package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;

public class Fireball extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "fireball");

    public Fireball() {
        super(ID, "Fireball", 15);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 6)) return false;

        double yRot = Math.toRadians(player.getYRot());
        double xRot = Math.toRadians(player.getXRot());
        Vec3 look = new Vec3(-Math.sin(yRot) * Math.cos(xRot), -Math.sin(xRot), Math.cos(yRot) * Math.cos(xRot));

        SmallFireball fireball = new SmallFireball(EntityType.SMALL_FIREBALL, player.level());
        fireball.setPos(player.getX() + look.x * 2, player.getEyeY() + look.y * 2, player.getZ() + look.z * 2);
        fireball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 0.0F);
        player.level().addFreshEntity(fireball);

        if (player.level() instanceof ServerLevel serverLevel) {
            Vec3 origin = player.position().add(0, player.getEyeHeight(), 0);
            for (double d = 0; d < 3; d += 0.2) {
                Vec3 point = origin.add(look.scale(d));
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        point.x, point.y, point.z,
                        5, 0.1, 0.1, 0.1, 0.02);
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        point.x, point.y, point.z,
                        3, 0.05, 0.05, 0.05, 0.01);
            }
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    player.getX() + look.x * 2, player.getY() + 1.5, player.getZ() + look.z * 2,
                    15, 0.3, 0.3, 0.3, 0.05);
        }

        player.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 1.0F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou conjure a fireball from the elements!"));
        return true;
    }
}
