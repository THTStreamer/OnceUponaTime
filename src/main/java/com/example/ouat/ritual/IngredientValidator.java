package com.example.ouat.ritual;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class IngredientValidator {
    private final List<RitualIngredient> requiredIngredients;

    public IngredientValidator(List<RitualIngredient> requiredIngredients) {
        this.requiredIngredients = new ArrayList<>(requiredIngredients);
    }

    public boolean validate(Player player) {
        for (RitualIngredient ingredient : requiredIngredients) {
            if (!ingredient.isPresentInInventory(player)) {
                return false;
            }
        }
        return true;
    }

    public List<RitualIngredient> getMissingIngredients(Player player) {
        List<RitualIngredient> missing = new ArrayList<>();
        for (RitualIngredient ingredient : requiredIngredients) {
            if (!ingredient.isPresentInInventory(player)) {
                missing.add(ingredient);
            }
        }
        return missing;
    }

    public boolean consumeIngredients(Player player) {
        if (!validate(player)) return false;
        for (RitualIngredient ingredient : requiredIngredients) {
            if (!ingredient.consumeFromInventory(player)) {
                return false;
            }
        }
        return true;
    }

    public List<RitualIngredient> getRequiredIngredients() {
        return List.copyOf(requiredIngredients);
    }
}
