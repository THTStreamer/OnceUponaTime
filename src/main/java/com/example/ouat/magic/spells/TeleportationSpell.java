package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.levelgen.Heightmap;

public class TeleportationSpell extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "teleportation");

    public TeleportationSpell() {
        super(ID, "Teleportation", 20);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 8)) return false;

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                    player.getX(), player.getY() + 1, player.getZ(),
                    50, 0.5, 0.5, 0.5, 0.3);
            serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    30, 0.3, 0.3, 0.3, 0.1);
        }

        int x = (int) (player.getX() + player.getRandom().nextIntBetweenInclusive(-50, 50));
        int z = (int) (player.getZ() + player.getRandom().nextIntBetweenInclusive(-50, 50));
        int y = player.level().getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

        player.teleportTo(x + 0.5, y + 1, z + 0.5);

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                    player.getX(), player.getY() + 1, player.getZ(),
                    50, 0.5, 0.5, 0.5, 0.3);
        }

        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 0.5F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§bYou vanish in a mist and reappear elsewhere!"));
        return true;
    }
}
