package com.example.ouat.items;

import com.example.ouat.OnceUponATime;
import com.example.ouat.menu.PotionBrewerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PotionBrewerItem extends Item {

    public PotionBrewerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, inventory, p) -> new PotionBrewerMenu(containerId, inventory),
                    Component.literal("Potion Brewer")
            ), player.blockPosition());
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
