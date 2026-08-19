package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.data.PlayerSupernaturalData.PortalLocation;
import com.example.ouat.magic.Spell;
import com.example.ouat.magic.Spell.MagicType;
import com.example.ouat.particles.PurpleSmokeEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;

import java.util.Map;

public class PortalCreation extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "portal_creation");

    public PortalCreation() {
        super(ID, "Portal Creation", 30, MagicType.NEUTRAL);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        return cast(player, null);
    }

    @Override
    public boolean cast(ServerPlayer player, String argument) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(Component.literal("§cYour magic is not strong enough."));
            return false;
        }

        String currentDim = player.level().dimension().location().toString();

        // "list" — show all saved portals in current dimension
        if ("list".equalsIgnoreCase(argument)) {
            showPortalList(player, data, currentDim);
            return true;
        }

        // "remove <name>" — remove a saved portal
        if ("remove".equalsIgnoreCase(argument) || (argument != null && argument.toLowerCase().startsWith("remove "))) {
            String removeName = argument;
            if (argument.toLowerCase().startsWith("remove ")) {
                removeName = argument.substring(7).trim();
            }
            if (removeName.isEmpty()) {
                player.sendSystemMessage(Component.literal("§cUsage: /ouat cast portal_creation remove <name>"));
                return false;
            }
            if (data.removePortal(removeName)) {
                player.sendSystemMessage(Component.literal("§6Portal §e" + removeName + " §6removed."));
            } else {
                player.sendSystemMessage(Component.literal("§cNo portal named §e" + removeName + " §cfound."));
            }
            return true;
        }

        // Named argument provided
        if (argument != null && !argument.isBlank()) {
            String name = argument.trim();

            // If portal with this name exists in current dimension — teleport to it
            if (data.hasPortalNamed(name)) {
                PortalLocation portal = data.getPortal(name);
                if (!portal.dimension().equals(currentDim)) {
                    player.sendSystemMessage(Component.literal("§cYou can only teleport to portals in the same dimension. Go to " + getDimensionDisplayName(portal.dimension()) + " first."));
                    return false;
                }
                if (!consumeFood(player, 12)) return false;
                return teleportToPortal(player, data, name);
            }

            // Portal name doesn't exist — SAVE current location with this name
            if (!consumeFood(player, 12)) return false;
            return saveCurrentLocation(player, data, name, currentDim);
        }

        // No argument — save as auto-named portal
        if (!consumeFood(player, 12)) return false;
        String autoName = "Portal " + (data.getSavedPortals().size() + 1);
        return saveCurrentLocation(player, data, autoName, currentDim);
    }

    private boolean saveCurrentLocation(ServerPlayer player, PlayerSupernaturalData data, String name, String currentDim) {
        data.savePortal(name, currentDim,
                player.getX(), player.getY(), player.getZ()
        );

        if (player.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 40; i++) {
                double angle = (i / 40.0) * Math.PI * 2;
                double radius = 1.5;
                double x = player.getX() + Math.cos(angle) * radius;
                double z = player.getZ() + Math.sin(angle) * radius;
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                        x, player.getY() + 1.0 + Math.sin(i * 0.3) * 0.3, z,
                        3, 0.1, 0.1, 0.1, 0.02);
            }
            serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(), player.getY() + 1, player.getZ(),
                    20, 0.3, 0.3, 0.3, 0.1);
        }

        player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.5F, 1.2F);
        player.sendSystemMessage(Component.literal("§6§lPortal destination saved: §e" + name));
        player.sendSystemMessage(Component.literal("§7" + getDimensionDisplayName(currentDim) + " (" + (int) player.getX() + ", " + (int) player.getY() + ", " + (int) player.getZ() + ")"));
        player.sendSystemMessage(Component.literal("§7Teleport: §e/ouat cast portal_creation " + name));
        return true;
    }

    private boolean teleportToPortal(ServerPlayer player, PlayerSupernaturalData data, String name) {
        PortalLocation portal = data.getPortal(name);

        if (player.level() instanceof ServerLevel serverLevel) {
            PurpleSmokeEffect.startServer(serverLevel, player);
        }

        player.teleportTo(player.server.getLevel(
                        net.minecraft.resources.ResourceKey.create(
                                net.minecraft.core.registries.Registries.DIMENSION,
                                ResourceLocation.parse(portal.dimension()))),
                portal.x(), portal.y(), portal.z(),
                player.getYRot(), player.getXRot());

        if (player.level() instanceof ServerLevel arrivalLevel) {
            PurpleSmokeEffect.startServer(arrivalLevel, player);
        }

        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 0.5F);
        player.sendSystemMessage(Component.literal("§5§lYou step through the portal and arrive at §d" + name + "§5!"));
        return true;
    }

    private void showPortalList(ServerPlayer player, PlayerSupernaturalData data, String currentDim) {
        Map<String, PortalLocation> allPortals = data.getSavedPortals();
        Map<String, PortalLocation> sameDim = new java.util.LinkedHashMap<>();
        Map<String, PortalLocation> otherDim = new java.util.LinkedHashMap<>();

        for (var entry : allPortals.entrySet()) {
            if (entry.getValue().dimension().equals(currentDim)) {
                sameDim.put(entry.getKey(), entry.getValue());
            } else {
                otherDim.put(entry.getKey(), entry.getValue());
            }
        }

        player.sendSystemMessage(Component.literal("§6§lYour Portal Destinations §7(" + allPortals.size() + " total):"));

        if (!sameDim.isEmpty()) {
            player.sendSystemMessage(Component.literal("§a§lIn this dimension (teleportable):"));
            for (var entry : sameDim.entrySet()) {
                PortalLocation p = entry.getValue();
                player.sendSystemMessage(Component.literal("  §e" + entry.getKey() + " §7- (" + (int) p.x() + ", " + (int) p.y() + ", " + (int) p.z() + ")"));
            }
        }

        if (!otherDim.isEmpty()) {
            player.sendSystemMessage(Component.literal("§c§lIn other dimensions (go there to teleport):"));
            for (var entry : otherDim.entrySet()) {
                PortalLocation p = entry.getValue();
                player.sendSystemMessage(Component.literal("  §e" + entry.getKey() + " §7- " + getDimensionDisplayName(p.dimension())
                        + " (" + (int) p.x() + ", " + (int) p.y() + ", " + (int) p.z() + ")"));
            }
        }

        if (allPortals.isEmpty()) {
            player.sendSystemMessage(Component.literal("§7No portals saved yet."));
        }

        player.sendSystemMessage(Component.literal("§7Teleport: §e/ouat cast portal_creation <name>"));
        player.sendSystemMessage(Component.literal("§7Save new: §e/ouat cast portal_creation <name> §7(or no name for auto-name)"));
        player.sendSystemMessage(Component.literal("§7Remove: §e/ouat cast portal_creation remove <name>"));
    }

    private String getDimensionDisplayName(String dimLoc) {
        String path = dimLoc.contains(":") ? dimLoc.split(":")[1] : dimLoc;
        return switch (path) {
            case "storybrooke" -> "Storybrooke";
            case "enchanted_forest" -> "Enchanted Forest";
            case "underworld" -> "Underworld";
            case "neverland" -> "Neverland";
            case "wonderland" -> "Wonderland";
            case "mirror_dimension" -> "Mirror Dimension";
            default -> "Overworld";
        };
    }
}
