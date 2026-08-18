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

public class TrueLovePotionItem extends Item {
    public TrueLovePotionItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // True Love Potion: breaks all curses and heals completely
            com.example.ouat.data.PlayerSupernaturalData data = serverPlayer.getData(com.example.ouat.data.PlayerSupernaturalData.TYPE);
            boolean hadCurses = !data.getCurses().isEmpty();
            for (var curse : data.getCurses()) {
                data.removeCurse(curse.getCurseId());
            }

            serverPlayer.heal(20.0F);
            serverPlayer.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.REGENERATION, 600, 2));
            serverPlayer.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.ABSORPTION, 1200, 2));

            stack.shrink(1);

            serverPlayer.playSound(SoundEvents.TOTEM_USE, 2.0F, 1.5F);
            if (hadCurses) {
                serverPlayer.sendSystemMessage(Component.literal("§d§lTrue Love's Potion breaks all curses!"));
            } else {
                serverPlayer.sendSystemMessage(Component.literal("§d§lThe potion fills you with the power of true love!"));
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§d§lTrue Love Potion").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.literal("§7The most powerful potion in existence"));
        tooltip.add(Component.literal("§7Breaks all curses and heals completely"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
