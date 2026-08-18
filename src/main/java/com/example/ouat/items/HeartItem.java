package com.example.ouat.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HeartItem extends Item {
    public HeartItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
