package com.example.ouat.dimension;

import com.example.ouat.OnceUponATime;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class MirrorDimensionTeleporter {
    public static final ResourceKey<Level> MIRROR_DIMENSION =
            ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "mirror_dimension"));

    public static void sendToMirrorDimension(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return;

        ServerLevel mirrorLevel = server.getLevel(MIRROR_DIMENSION);
        if (mirrorLevel == null) return;

        // Create a safe platform in the mirror dimension
        BlockPos spawnPos = new BlockPos(0, 64, 0);

        // Build a small glass room
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                BlockPos floor = spawnPos.offset(x, -1, z);
                mirrorLevel.setBlockAndUpdate(floor, net.minecraft.world.level.block.Blocks.POLISHED_BLACKSTONE.defaultBlockState());
                BlockPos ceiling = spawnPos.offset(x, 4, z);
                mirrorLevel.setBlockAndUpdate(ceiling, net.minecraft.world.level.block.Blocks.BLACK_STAINED_GLASS.defaultBlockState());

                // Walls
                if (Math.abs(x) == 3 || Math.abs(z) == 3) {
                    for (int y = 0; y <= 3; y++) {
                        BlockPos wall = spawnPos.offset(x, y, z);
                        if (x == -3 || x == 3 || z == -3 || z == 3) {
                            mirrorLevel.setBlockAndUpdate(wall, net.minecraft.world.level.block.Blocks.BLACK_STAINED_GLASS.defaultBlockState());
                        }
                    }
                }
            }
        }

        // Place mirrors (glass panes) on walls for the trapped player to see through
        mirrorLevel.setBlockAndUpdate(spawnPos.offset(-3, 1, 0), net.minecraft.world.level.block.Blocks.GLASS_PANE.defaultBlockState());
        mirrorLevel.setBlockAndUpdate(spawnPos.offset(-3, 2, 0), net.minecraft.world.level.block.Blocks.GLASS_PANE.defaultBlockState());
        mirrorLevel.setBlockAndUpdate(spawnPos.offset(3, 1, 0), net.minecraft.world.level.block.Blocks.GLASS_PANE.defaultBlockState());
        mirrorLevel.setBlockAndUpdate(spawnPos.offset(3, 2, 0), net.minecraft.world.level.block.Blocks.GLASS_PANE.defaultBlockState());
        mirrorLevel.setBlockAndUpdate(spawnPos.offset(0, 1, -3), net.minecraft.world.level.block.Blocks.GLASS_PANE.defaultBlockState());
        mirrorLevel.setBlockAndUpdate(spawnPos.offset(0, 2, -3), net.minecraft.world.level.block.Blocks.GLASS_PANE.defaultBlockState());
        mirrorLevel.setBlockAndUpdate(spawnPos.offset(0, 1, 3), net.minecraft.world.level.block.Blocks.GLASS_PANE.defaultBlockState());
        mirrorLevel.setBlockAndUpdate(spawnPos.offset(0, 2, 3), net.minecraft.world.level.block.Blocks.GLASS_PANE.defaultBlockState());

        player.teleportTo(mirrorLevel, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, player.getYRot(), player.getXRot());
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§5§lYou are trapped in the Mirror Dimension! You can see the outside world through reflective surfaces..."));
    }

    public static void returnFromMirrorDimension(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return;

        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        BlockPos spawnPos = overworld.getSharedSpawnPos();
        player.teleportTo(overworld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, player.getYRot(), player.getXRot());
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§a§lYou are released from the Mirror Dimension!"));
    }
}
