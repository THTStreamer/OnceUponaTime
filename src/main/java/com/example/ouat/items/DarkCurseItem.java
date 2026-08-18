package com.example.ouat.items;

import com.example.ouat.OnceUponATime;
import com.example.ouat.dimensions.DimensionManager;
import com.example.ouat.particles.DarkSmokeEffect;
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
    private static final double CURSE_RADIUS = 4800.0;

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

        DarkSmokeEffect.startServer(serverLevel, serverPlayer);

        for (ServerPlayer target : affectedPlayers) {
            target.playSound(SoundEvents.WITHER_SPAWN, 2.0F, 0.5F);
            target.sendSystemMessage(Component.literal("§5§l☠ The Dark Curse is upon you! ☠"));
        }

        stack.shrink(1);

        OnceUponATime.LOGGER.info("Dark Curse cast by {} — {} players affected",
                serverPlayer.getName().getString(), affectedPlayers.size());

        return InteractionResultHolder.sidedSuccess(stack, false);
    }
}
