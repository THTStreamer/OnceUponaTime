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

public class EnchantedCandleItem extends Item {
    public EnchantedCandleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Enchanted Candle: takes life to save another (heal nearby player)
            net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 5.0);
            if (target == null) {
                serverPlayer.sendSystemMessage(Component.literal("§cNo nearby player to save."));
                return InteractionResultHolder.fail(stack);
            }

            // Sacrifice own health to heal target
            serverPlayer.hurt(serverPlayer.damageSources().magic(), 6.0F);
            target.heal(12.0F);
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.REGENERATION, 400, 1));

            stack.shrink(1);

            serverPlayer.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);
            target.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.5F);
            serverPlayer.sendSystemMessage(Component.literal("§6You light the enchanted candle, sacrificing your life force to save " + target.getName().getString() + "!"));
            target.sendSystemMessage(Component.literal("§6" + serverPlayer.getName().getString() + " sacrifices their life force to save you!"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§6§lEnchanted Candle").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("§7Takes life to save another"));
        tooltip.add(Component.literal("§7Right-click near a player to heal them at your cost"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
