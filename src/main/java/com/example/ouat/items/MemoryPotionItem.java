package com.example.ouat.items;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class MemoryPotionItem extends Item {
    public MemoryPotionItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Memory Potion: restores proficiency
            PlayerSupernaturalData data = serverPlayer.getData(PlayerSupernaturalData.TYPE);
            data.addMagicProficiency(15);

            stack.shrink(1);

            serverPlayer.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.5F, 1.5F);
            serverPlayer.sendSystemMessage(Component.literal("§a§lYou drink the memory potion and recall forgotten magic!"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§a§lMemory Potion").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("§7Restores magical knowledge"));
        tooltip.add(Component.literal("§7One drink only"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
