package com.example.ouat.artifacts;

import com.example.ouat.OnceUponATime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ArtifactRegistry {
    private static final Map<ResourceLocation, ArtifactDefinition> ARTIFACTS = new HashMap<>();

    public static void register(ArtifactDefinition artifact) {
        ARTIFACTS.put(artifact.artifactId(), artifact);
        OnceUponATime.LOGGER.info("Registered artifact: {}", artifact.artifactId());
    }

    public static ArtifactDefinition getArtifact(ResourceLocation id) {
        return ARTIFACTS.get(id);
    }

    public static boolean hasArtifact(ResourceLocation id) {
        return ARTIFACTS.containsKey(id);
    }

    public static Map<ResourceLocation, ArtifactDefinition> getAllArtifacts() {
        return Map.copyOf(ARTIFACTS);
    }

    public static void initializeArtifacts() {
        register(new ArtifactDefinition(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "dark_one_dagger"),
                "Dark One Dagger",
                "The legendary dagger that can kill the Dark One",
                ArtifactRarity.LEGENDARY,
                true
        ));

        register(new ArtifactDefinition(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "authors_quill"),
                "Author's Quill",
                "A quill that can write reality",
                ArtifactRarity.LEGENDARY,
                true
        ));

        register(new ArtifactDefinition(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "authors_book"),
                "Author's Book",
                "The book where stories are written",
                ArtifactRarity.LEGENDARY,
                true
        ));

        register(new ArtifactDefinition(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "grimoire_light"),
                "Grimoire of Light",
                "A grimoire containing light magic",
                ArtifactRarity.RARE,
                false
        ));

        register(new ArtifactDefinition(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "grimoire_dark"),
                "Grimoire of Darkness",
                "A grimoire containing dark magic",
                ArtifactRarity.RARE,
                false
        ));

        register(new ArtifactDefinition(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "enchanted_rose"),
                "Enchanted Rose",
                "A rose that never fades",
                ArtifactRarity.RARE,
                false
        ));

        register(new ArtifactDefinition(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "magic_mirror"),
                "Magic Mirror",
                "A mirror that shows truth",
                ArtifactRarity.RARE,
                false
        ));

        register(new ArtifactDefinition(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "cursed_talisman"),
                "Cursed Talisman",
                "A talisman with dark power",
                ArtifactRarity.UNCOMMON,
                false
        ));
    }

    public record ArtifactDefinition(
            ResourceLocation artifactId,
            String displayName,
            String description,
            ArtifactRarity rarity,
            boolean unique
    ) {
    }

    public enum ArtifactRarity {
        COMMON,
        UNCOMMON,
        RARE,
        EPIC,
        LEGENDARY
    }
}
