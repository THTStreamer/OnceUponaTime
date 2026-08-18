package com.example.ouat.roles;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AuthorRole {
    public static final ResourceLocation ROLE_ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "author");
    public static final ResourceLocation MAX_HEALTH_MODIFIER = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "author_health");
    public static final ResourceLocation ATTACK_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "author_attack_speed");

    public static void grantRole(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        data.setCurrentRole(ROLE_ID);
        data.setMagicalAlignment(PlayerSupernaturalData.MagicalAlignment.NEUTRAL);
        data.addHeldRole(ROLE_ID);

        applyAuthorAttributes(player);

        OnceUponATime.LOGGER.info("Player {} has become the Author", player.getName().getString());
    }

    public static void removeRole(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        data.setCurrentRole(null);

        removeAuthorAttributes(player);

        OnceUponATime.LOGGER.info("Player {} is no longer the Author", player.getName().getString());
    }

    private static void applyAuthorAttributes(ServerPlayer player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(MAX_HEALTH_MODIFIER) == null) {
            maxHealth.addPermanentModifier(new AttributeModifier(
                    MAX_HEALTH_MODIFIER, 4.0, AttributeModifier.Operation.ADD_VALUE));
        }

        AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeed != null && attackSpeed.getModifier(ATTACK_SPEED_MODIFIER) == null) {
            attackSpeed.addPermanentModifier(new AttributeModifier(
                    ATTACK_SPEED_MODIFIER, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    private static void removeAuthorAttributes(ServerPlayer player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.removeModifier(MAX_HEALTH_MODIFIER);
        }

        AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeed != null) {
            attackSpeed.removeModifier(ATTACK_SPEED_MODIFIER);
        }
    }

    public static boolean isAuthor(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        return ROLE_ID.equals(data.getCurrentRole());
    }

    public static boolean isAuthor(PlayerSupernaturalData data) {
        return ROLE_ID.equals(data.getCurrentRole());
    }

    public static boolean canAlterReality(ServerPlayer player) {
        return isAuthor(player);
    }

    public static boolean canWriteFate(ServerPlayer player) {
        return isAuthor(player);
    }
}
