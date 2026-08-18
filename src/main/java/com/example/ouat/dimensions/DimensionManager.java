package com.example.ouat.dimensions;

import com.example.ouat.OnceUponATime;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.RelativeMovement;

import java.util.Set;

public class DimensionManager {
    public static final ResourceKey<Level> STORYBROOKE = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "storybrooke")
    );

    public static final ResourceKey<Level> ENCHANTED_FOREST = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "enchanted_forest")
    );

    public static final ResourceKey<Level> UNDERWORLD = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "underworld")
    );

    public static final ResourceKey<Level> NEVERLAND = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "neverland")
    );

    public static final ResourceKey<Level> WONDERLAND = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "wonderland")
    );

    public static boolean teleportToDimension(ServerPlayer player, ResourceKey<Level> dimensionKey) {
        ServerLevel targetLevel = player.server.getLevel(dimensionKey);
        if (targetLevel == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cThe dimension could not be found."));
            return false;
        }

        player.stopRiding();
        player.teleportTo(targetLevel, player.getX(), player.getY(), player.getZ(),
                Set.of(RelativeMovement.X, RelativeMovement.Y, RelativeMovement.Z), player.getYRot(), player.getXRot());

        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);

        String dimensionName = getDimensionName(dimensionKey);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou have traveled to " + dimensionName + "!"));

        OnceUponATime.LOGGER.info("Player {} teleported to {}", player.getName().getString(), dimensionName);
        return true;
    }

    public static boolean teleportToOverworld(ServerPlayer player) {
        ServerLevel overworld = player.server.getLevel(ServerLevel.OVERWORLD);
        if (overworld == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cThe overworld could not be found."));
            return false;
        }

        player.teleportTo(overworld, player.getX(), player.getY(), player.getZ(),
                Set.of(RelativeMovement.X, RelativeMovement.Y, RelativeMovement.Z), player.getYRot(), player.getXRot());

        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou have returned to the Overworld."));

        return true;
    }

    private static String getDimensionName(ResourceKey<Level> dimensionKey) {
        if (dimensionKey == STORYBROOKE) return "Storybrooke";
        if (dimensionKey == ENCHANTED_FOREST) return "The Enchanted Forest";
        if (dimensionKey == UNDERWORLD) return "The Underworld";
        if (dimensionKey == NEVERLAND) return "Neverland";
        if (dimensionKey == WONDERLAND) return "Wonderland";
        return "Unknown Dimension";
    }

    public static boolean isOuATDimension(ResourceKey<Level> dimensionKey) {
        return dimensionKey == STORYBROOKE ||
                dimensionKey == ENCHANTED_FOREST ||
                dimensionKey == UNDERWORLD ||
                dimensionKey == NEVERLAND ||
                dimensionKey == WONDERLAND;
    }
}
