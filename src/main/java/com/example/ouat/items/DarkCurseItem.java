package com.example.ouat.items;

import com.example.ouat.OnceUponATime;
import com.example.ouat.dimensions.DimensionManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class DarkCurseItem extends Item {
    private static final double CURSE_RADIUS = 4800.0; // 300 chunks * 16 blocks

    public DarkCurseItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (level.isClientSide()) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        ServerLevel serverLevel = (ServerLevel) level;
        ServerPlayer serverPlayer = (ServerPlayer) player;

        // Get all players within 300 chunk radius
        List<ServerPlayer> affectedPlayers = new ArrayList<>();
        for (ServerPlayer target : serverLevel.getServer().getPlayerList().getPlayers()) {
            double distance = target.distanceTo(player);
            if (distance <= CURSE_RADIUS) {
                affectedPlayers.add(target);
            }
        }

        if (affectedPlayers.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.literal("§cThe Dark Curse found no souls to curse."));
            return InteractionResultHolder.fail(stack);
        }

        // Dramatic build-up at caster location
        serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH,
                player.getX(), player.getY() + 1, player.getZ(),
                200, 5.0, 3.0, 5.0, 0.1);
        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                player.getX(), player.getY() + 2, player.getZ(),
                100, 4.0, 2.0, 4.0, 0.05);

        // Play curse sound to all affected players
        for (ServerPlayer target : affectedPlayers) {
            target.playSound(SoundEvents.WITHER_SPAWN, 2.0F, 0.5F);
            target.sendSystemMessage(Component.literal("§5§l☠ The Dark Curse is upon you! ☠"));
        }

        // Teleport all affected players to Storybrooke
        int teleportCount = 0;
        for (ServerPlayer target : affectedPlayers) {
            // Stagger teleports slightly for dramatic effect
            serverLevel.getServer().execute(() -> {
                DimensionManager.teleportToDimension(target, DimensionManager.STORYBROOKE);

                // Arrival particles at Storybrooke
                ServerLevel storyLevel = target.server.getLevel(DimensionManager.STORYBROOKE);
                if (storyLevel != null) {
                    storyLevel.sendParticles(ParticleTypes.DRAGON_BREATH,
                            target.getX(), target.getY() + 1, target.getZ(),
                            50, 3.0, 2.0, 3.0, 0.05);
                }

                target.sendSystemMessage(Component.literal("§5§lYou have been transported to Storybrooke by the Dark Curse!"));
                target.sendSystemMessage(Component.literal("§7No more happy endings..."));
            });
            teleportCount++;
        }

        // Consume the item
        stack.shrink(1);

        OnceUponATime.LOGGER.info("Dark Curse cast by {} — {} players teleported to Storybrooke",
                serverPlayer.getName().getString(), teleportCount);

        return InteractionResultHolder.sidedSuccess(stack, false);
    }
}
