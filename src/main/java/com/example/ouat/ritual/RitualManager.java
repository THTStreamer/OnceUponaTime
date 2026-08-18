package com.example.ouat.ritual;

import com.example.ouat.OnceUponATime;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class RitualManager {
    private static final Map<ResourceLocation, Ritual> RITUALS = new HashMap<>();

    public static void register(Ritual ritual) {
        RITUALS.put(ritual.getRitualId(), ritual);
        OnceUponATime.LOGGER.info("Registered ritual: {}", ritual.getRitualId());
    }

    public static Ritual getRitual(ResourceLocation id) {
        return RITUALS.get(id);
    }

    public static boolean hasRitual(ResourceLocation id) {
        return RITUALS.containsKey(id);
    }

    public static Map<ResourceLocation, Ritual> getAllRituals() {
        return Map.copyOf(RITUALS);
    }
}
