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

public class SquidInkItem extends Item {
    public SquidInkItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Squid Ink: paralyze nearest magic user
            net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 5.0);
            if (target == null) {
                serverPlayer.sendSystemMessage(Component.literal("§cNo target nearby."));
                return InteractionResultHolder.fail(stack);
            }

            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 400, 10));
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.DIG_SLOWDOWN, 400, 10));
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.BLINDNESS, 200, 0));

            stack.shrink(1);

            serverPlayer.playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.0F, 0.5F);
            serverPlayer.sendSystemMessage(Component.literal("§4You coat " + target.getName().getString() + " in squid ink! Their magic is silenced!"));
            target.sendSystemMessage(Component.literal("§4Squid ink covers you, silencing your magic!"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§4§lSquid Ink").withStyle(ChatFormatting.DARK_RED));
        tooltip.add(Component.literal("§7Silences magic users"));
        tooltip.add(Component.literal("§7Right-click near a player to paralyze them"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
