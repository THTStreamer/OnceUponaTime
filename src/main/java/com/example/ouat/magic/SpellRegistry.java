package com.example.ouat.magic;

import com.example.ouat.OnceUponATime;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class SpellRegistry {
    private static final Map<ResourceLocation, Spell> SPELLS = new HashMap<>();

    public static void register(Spell spell) {
        SPELLS.put(spell.getSpellId(), spell);
        OnceUponATime.LOGGER.info("Registered spell: {}", spell.getSpellId());
    }

    public static Spell getSpell(ResourceLocation id) {
        return SPELLS.get(id);
    }

    public static boolean hasSpell(ResourceLocation id) {
        return SPELLS.containsKey(id);
    }

    public static Map<ResourceLocation, Spell> getAllSpells() {
        return Map.copyOf(SPELLS);
    }
}
