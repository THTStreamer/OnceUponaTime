package com.example.ouat.dimensions;

import com.example.ouat.OnceUponATime;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MagicalPortal {
    private static final Map<ResourceLocation, PortalData> PORTALS = new HashMap<>();

    public static void registerPortal(ResourceLocation portalId, ResourceKey<Level> targetDimension, BlockPos targetPos) {
        PORTALS.put(portalId, new PortalData(targetDimension, targetPos));
        OnceUponATime.LOGGER.info("Registered magical portal: {} -> {}", portalId, targetDimension.location());
    }

    public static boolean usePortal(Entity entity, ResourceLocation portalId) {
        PortalData portal = PORTALS.get(portalId);
        if (portal == null) {
            if (entity instanceof Player player) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cThe portal does not respond."));
            }
            return false;
        }

        if (!(entity instanceof ServerPlayer player)) return false;

        ServerLevel targetLevel = entity.getServer().getLevel(portal.targetDimension());
        if (targetLevel == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cThe destination cannot be reached."));
            return false;
        }

        player.stopRiding();
        player.teleportTo(targetLevel, portal.targetPos().getX() + 0.5,
                portal.targetPos().getY(), portal.targetPos().getZ() + 0.5,
                Set.of(RelativeMovement.X, RelativeMovement.Y, RelativeMovement.Z),
                player.getYRot(), player.getXRot());

        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou step through the magical portal..."));

        return true;
    }

    private record PortalData(ResourceKey<Level> targetDimension, BlockPos targetPos) {
    }

    public static void initializeDefaultPortals() {
        registerPortal(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "storybrooke_portal"),
                DimensionManager.STORYBROOKE,
                BlockPos.ZERO
        );

        registerPortal(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "enchanted_forest_portal"),
                DimensionManager.ENCHANTED_FOREST,
                BlockPos.ZERO
        );

        registerPortal(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "underworld_portal"),
                DimensionManager.UNDERWORLD,
                BlockPos.ZERO
        );

        registerPortal(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "neverland_portal"),
                DimensionManager.NEVERLAND,
                BlockPos.ZERO
        );

        registerPortal(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "wonderland_portal"),
                DimensionManager.WONDERLAND,
                BlockPos.ZERO
        );
    }
}
