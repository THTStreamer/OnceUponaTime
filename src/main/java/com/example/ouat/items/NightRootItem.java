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

public class NightRootItem extends Item {
    public NightRootItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Night Root: absorbs darkness, removes curses
            PlayerSupernaturalData data = serverPlayer.getData(PlayerSupernaturalData.TYPE);
            boolean hadCurses = !data.getCurses().isEmpty();
            for (var curse : data.getCurses()) {
                data.removeCurse(curse.getCurseId());
            }
            serverPlayer.heal(10.0F);

            stack.shrink(1);

            serverPlayer.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.5F);
            if (hadCurses) {
                serverPlayer.sendSystemMessage(Component.literal("§aThe night root absorbs the darkness from your body!"));
            } else {
                serverPlayer.sendSystemMessage(Component.literal("§aThe night root cleanses your spirit."));
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§2§lNight Root").withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(Component.literal("§7Absorbs darkness and removes curses"));
        tooltip.add(Component.literal("§7One use only"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
