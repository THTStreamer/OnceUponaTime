package com.example.ouat.roles;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.data.UniqueRoleRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class DarkOneRole {
    public static final ResourceLocation ROLE_ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "dark_one");
    public static final ResourceLocation ATTACK_DAMAGE_MODIFIER = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "dark_one_attack");
    public static final ResourceLocation MAX_HEALTH_MODIFIER = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "dark_one_health");
    public static final ResourceLocation MOVEMENT_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "dark_one_speed");

    public static void grantRole(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        data.setCurrentRole(ROLE_ID);
        data.setMagicalAlignment(PlayerSupernaturalData.MagicalAlignment.DARK);
        data.addHeldRole(ROLE_ID);

        applyDarkOneAttributes(player);

        OnceUponATime.LOGGER.info("Player {} has become the Dark One", player.getName().getString());
    }

    public static void removeRole(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        data.setCurrentRole(null);

        removeDarkOneAttributes(player);

        OnceUponATime.LOGGER.info("Player {} is no longer the Dark One", player.getName().getString());
    }

    private static void applyDarkOneAttributes(ServerPlayer player) {
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            if (attackDamage.getModifier(ATTACK_DAMAGE_MODIFIER) == null) {
                attackDamage.addPermanentModifier(new AttributeModifier(
                        ATTACK_DAMAGE_MODIFIER, 6.0, AttributeModifier.Operation.ADD_VALUE));
            }
        }

        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            if (maxHealth.getModifier(MAX_HEALTH_MODIFIER) == null) {
                maxHealth.addPermanentModifier(new AttributeModifier(
                        MAX_HEALTH_MODIFIER, 10.0, AttributeModifier.Operation.ADD_VALUE));
            }
        }

        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            if (movementSpeed.getModifier(MOVEMENT_SPEED_MODIFIER) == null) {
                movementSpeed.addPermanentModifier(new AttributeModifier(
                        MOVEMENT_SPEED_MODIFIER, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    private static void removeDarkOneAttributes(ServerPlayer player) {
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.removeModifier(ATTACK_DAMAGE_MODIFIER);
        }

        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.removeModifier(MAX_HEALTH_MODIFIER);
        }

        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(MOVEMENT_SPEED_MODIFIER);
        }
    }

    public static boolean isDarkOne(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        return ROLE_ID.equals(data.getCurrentRole());
    }

    public static boolean isDarkOne(PlayerSupernaturalData data) {
        return ROLE_ID.equals(data.getCurrentRole());
    }
}
