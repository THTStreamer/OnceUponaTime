package com.example.ouat.items;

import com.example.ouat.OnceUponATime;
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

public class DreamCatcherItem extends Item {
    public DreamCatcherItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Dream Catcher: view nearest player's info
            net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 8.0);
            if (target == null) {
                serverPlayer.sendSystemMessage(Component.literal("§7No target nearby to view memories of."));
                return InteractionResultHolder.fail(stack);
            }

            com.example.ouat.data.PlayerSupernaturalData targetData = target.getData(com.example.ouat.data.PlayerSupernaturalData.TYPE);

            serverPlayer.sendSystemMessage(Component.literal("§d§lYou peer into " + target.getName().getString() + "'s memories..."));
            serverPlayer.sendSystemMessage(Component.literal("§7Magic Proficiency: " + targetData.getMagicProficiency()));
            serverPlayer.sendSystemMessage(Component.literal("§7Spells Known: " + targetData.getLearnedSpells().size()));
            serverPlayer.sendSystemMessage(Component.literal("§7Curses: " + targetData.getCurses().size()));

            serverPlayer.playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.5F, 1.0F);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§5§lDream Catcher").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.literal("§7Views the memories of others"));
        tooltip.add(Component.literal("§7Right-click near a player to view their info"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
