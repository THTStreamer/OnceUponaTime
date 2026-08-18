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

public class ChippedCupItem extends Item {
    public ChippedCupItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Chipped Cup: symbol of true love, breaks curses
            com.example.ouat.data.PlayerSupernaturalData data = serverPlayer.getData(com.example.ouat.data.PlayerSupernaturalData.TYPE);
            boolean hadCurses = !data.getCurses().isEmpty();
            for (var curse : data.getCurses()) {
                data.removeCurse(curse.getCurseId());
            }

            serverPlayer.heal(20.0F);
            serverPlayer.playSound(SoundEvents.TOTEM_USE, 1.5F, 1.5F);

            if (hadCurses) {
                serverPlayer.sendSystemMessage(Component.literal("§d§lThe chipped cup of true love breaks all curses!"));
            } else {
                serverPlayer.sendSystemMessage(Component.literal("§d§lThe chipped cup fills you with warmth and hope!"));
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§d§lChipped Cup").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.literal("§7The vessel of true love"));
        tooltip.add(Component.literal("§7Right-click to break all curses and heal"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
