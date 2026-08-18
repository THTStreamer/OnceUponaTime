package com.example.ouat.roles;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class TruestBelieverRole {
    public static final ResourceLocation ROLE_ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "truest_believer");
    public static final ResourceLocation MAX_HEALTH_MODIFIER = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "believer_health");
    public static final ResourceLocation MOVEMENT_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "believer_speed");
    public static final ResourceLocation LUCK_MODIFIER = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "believer_luck");

    public static void grantRole(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        data.setCurrentRole(ROLE_ID);
        data.setMagicalAlignment(PlayerSupernaturalData.MagicalAlignment.LIGHT);
        data.addHeldRole(ROLE_ID);

        applyBelieverAttributes(player);

        OnceUponATime.LOGGER.info("Player {} has become the Truest Believer", player.getName().getString());
    }

    public static void removeRole(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        data.setCurrentRole(null);

        removeBelieverAttributes(player);

        OnceUponATime.LOGGER.info("Player {} is no longer the Truest Believer", player.getName().getString());
    }

    private static void applyBelieverAttributes(ServerPlayer player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(MAX_HEALTH_MODIFIER) == null) {
            maxHealth.addPermanentModifier(new AttributeModifier(
                    MAX_HEALTH_MODIFIER, 6.0, AttributeModifier.Operation.ADD_VALUE));
        }

        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null && movementSpeed.getModifier(MOVEMENT_SPEED_MODIFIER) == null) {
            movementSpeed.addPermanentModifier(new AttributeModifier(
                    MOVEMENT_SPEED_MODIFIER, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }

        AttributeInstance luck = player.getAttribute(Attributes.LUCK);
        if (luck != null && luck.getModifier(LUCK_MODIFIER) == null) {
            luck.addPermanentModifier(new AttributeModifier(
                    LUCK_MODIFIER, 2.0, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void removeBelieverAttributes(ServerPlayer player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.removeModifier(MAX_HEALTH_MODIFIER);
        }

        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(MOVEMENT_SPEED_MODIFIER);
        }

        AttributeInstance luck = player.getAttribute(Attributes.LUCK);
        if (luck != null) {
            luck.removeModifier(LUCK_MODIFIER);
        }
    }

    public static boolean isTruestBeliever(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        return ROLE_ID.equals(data.getCurrentRole());
    }

    public static boolean isTruestBeliever(PlayerSupernaturalData data) {
        return ROLE_ID.equals(data.getCurrentRole());
    }

    public static boolean canDefyReality(ServerPlayer player) {
        return isTruestBeliever(player);
    }

    public static boolean canRestoreBrokenMagic(ServerPlayer player) {
        return isTruestBeliever(player);
    }
}
