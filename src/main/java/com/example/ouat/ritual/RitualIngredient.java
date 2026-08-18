package com.example.ouat.ritual;

import com.example.ouat.registry.ModDataComponents;
import net.minecraft.world.item.ItemStack;

public class RitualIngredient {
    private final String ingredientId;
    private final String magicType;
    private final int quantity;
    private final boolean consumedOnUse;

    public RitualIngredient(String ingredientId, String magicType, int quantity, boolean consumedOnUse) {
        this.ingredientId = ingredientId;
        this.magicType = magicType;
        this.quantity = quantity;
        this.consumedOnUse = consumedOnUse;
    }

    public String getIngredientId() { return ingredientId; }
    public String getMagicType() { return magicType; }
    public int getQuantity() { return quantity; }
    public boolean isConsumedOnUse() { return consumedOnUse; }

    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ModDataComponents.RitualIngredientData data = stack.get(ModDataComponents.RITUAL_INGREDIENT.value());
        if (data == null) return false;
        return data.ingredientId().equals(ingredientId) && data.magicType().equals(magicType);
    }

    public boolean isPresentInInventory(net.minecraft.world.entity.player.Player player) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (matches(stack)) {
                count += stack.getCount();
            }
        }
        return count >= quantity;
    }

    public boolean consumeFromInventory(net.minecraft.world.entity.player.Player player) {
        if (!isPresentInInventory(player)) return false;
        if (!consumedOnUse) return true;

        int remaining = quantity;
        for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (matches(stack)) {
                int toRemove = Math.min(stack.getCount(), remaining);
                stack.shrink(toRemove);
                remaining -= toRemove;
            }
        }
        return remaining == 0;
    }
}
