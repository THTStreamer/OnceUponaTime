package com.example.ouat.artifacts;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;

public class ArtifactManager {

    public static boolean pickupArtifact(ServerPlayer player, ResourceLocation artifactId) {
        ArtifactRegistry.ArtifactDefinition artifact = ArtifactRegistry.getArtifact(artifactId);
        if (artifact == null) {
            return false;
        }

        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (data.getHeldArtifacts().contains(artifactId)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYou already possess this artifact."));
            return false;
        }

        data.addHeldArtifact(artifactId);

        player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou have obtained: §e" + artifact.displayName() + "§a!"));

        if (artifact.unique()) {
            player.level().getServer().getPlayerList().broadcastSystemMessage(
                    net.minecraft.network.chat.Component.literal("§6§l" + player.getName().getString() + " has obtained the legendary " + artifact.displayName() + "!"),
                    false
            );
        }

        OnceUponATime.LOGGER.info("Player {} obtained artifact {}", player.getName().getString(), artifactId);
        return true;
    }

    public static boolean dropArtifact(ServerPlayer player, ResourceLocation artifactId) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!data.getHeldArtifacts().contains(artifactId)) {
            return false;
        }

        data.removeHeldArtifact(artifactId);

        player.playSound(SoundEvents.ITEM_BREAK, 1.0F, 1.0F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYou have lost an artifact."));

        return true;
    }

    public static boolean hasArtifact(ServerPlayer player, ResourceLocation artifactId) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        return data.getHeldArtifacts().contains(artifactId);
    }
}
