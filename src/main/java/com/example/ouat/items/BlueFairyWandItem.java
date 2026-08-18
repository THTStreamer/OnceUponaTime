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

public class BlueFairyWandItem extends Item {
    public BlueFairyWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Blue Fairy's Wand: heals and grants regeneration
            serverPlayer.heal(20.0F);
            serverPlayer.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.REGENERATION, 600, 2));
            serverPlayer.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.ABSORPTION, 1200, 2));

            serverPlayer.playSound(SoundEvents.TOTEM_USE, 1.5F, 1.5F);
            serverPlayer.sendSystemMessage(Component.literal("§b§lThe Blue Fairy's wand channels light magic, healing your wounds!"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§b§lBlue Fairy's Wand").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("§7The wand of the Blue Fairy"));
        tooltip.add(Component.literal("§7Right-click to heal and gain protection"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
