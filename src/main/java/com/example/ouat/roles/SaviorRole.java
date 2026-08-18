package com.example.ouat.roles;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.data.UniqueRoleRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SaviorRole {
    public static final ResourceLocation ROLE_ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "savior");
    public static final ResourceLocation MAX_HEALTH_MODIFIER = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "savior_health");
    public static final ResourceLocation ARMOR_MODIFIER = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "savior_armor");
    public static final ResourceLocation KNOCKBACK_RESISTANCE = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "savior_knockback");

    public static void grantRole(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        data.setCurrentRole(ROLE_ID);
        data.setMagicalAlignment(PlayerSupernaturalData.MagicalAlignment.LIGHT);
        data.addHeldRole(ROLE_ID);

        applySaviorAttributes(player);

        OnceUponATime.LOGGER.info("Player {} has become the Savior", player.getName().getString());
    }

    public static void removeRole(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        data.setCurrentRole(null);

        removeSaviorAttributes(player);

        OnceUponATime.LOGGER.info("Player {} is no longer the Savior", player.getName().getString());
    }

    private static void applySaviorAttributes(ServerPlayer player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(MAX_HEALTH_MODIFIER) == null) {
            maxHealth.addPermanentModifier(new AttributeModifier(
                    MAX_HEALTH_MODIFIER, 8.0, AttributeModifier.Operation.ADD_VALUE));
        }

        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor != null && armor.getModifier(ARMOR_MODIFIER) == null) {
            armor.addPermanentModifier(new AttributeModifier(
                    ARMOR_MODIFIER, 4.0, AttributeModifier.Operation.ADD_VALUE));
        }

        AttributeInstance knockback = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (knockback != null && knockback.getModifier(KNOCKBACK_RESISTANCE) == null) {
            knockback.addPermanentModifier(new AttributeModifier(
                    KNOCKBACK_RESISTANCE, 0.3, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void removeSaviorAttributes(ServerPlayer player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.removeModifier(MAX_HEALTH_MODIFIER);
        }

        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            armor.removeModifier(ARMOR_MODIFIER);
        }

        AttributeInstance knockback = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (knockback != null) {
            knockback.removeModifier(KNOCKBACK_RESISTANCE);
        }
    }

    public static boolean isSavior(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        return ROLE_ID.equals(data.getCurrentRole());
    }

    public static boolean isSavior(PlayerSupernaturalData data) {
        return ROLE_ID.equals(data.getCurrentRole());
    }

    public static boolean canPurifyCurses(ServerPlayer player) {
        return isSavior(player);
    }

    public static boolean canBreakDarkMagic(ServerPlayer player) {
        return isSavior(player);
    }
}
