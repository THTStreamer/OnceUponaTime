package com.example.ouat.items;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.registry.ModDataComponents;
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
import java.util.UUID;

public class StolenHeartItem extends Item {
    public StolenHeartItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            var heartData = stack.get(ModDataComponents.STOLEN_HEART.value());
            if (heartData == null || heartData.victimName() == null) {
                serverPlayer.sendSystemMessage(Component.literal("§cThis heart has no owner."));
                return InteractionResultHolder.fail(stack);
            }

            UUID victimUUID = heartData.victimUUID();
            if (victimUUID == null) {
                serverPlayer.sendSystemMessage(Component.literal("§cThis heart has no soul bound to it."));
                return InteractionResultHolder.fail(stack);
            }

            ServerPlayer victim = level.getServer().getPlayerList().getPlayer(victimUUID);

            if (victim != null && victim.isAlive()) {
                // Return the heart — clear the ripped flag
                PlayerSupernaturalData victimData = victim.getData(PlayerSupernaturalData.TYPE);
                victimData.setHeartRipped(false, null);

                victim.playSound(SoundEvents.TOTEM_USE, 2.0F, 1.0F);
                victim.sendSystemMessage(Component.literal("§d§lYour heart is returned to your chest! You feel whole again."));
                serverPlayer.sendSystemMessage(Component.literal("§dYou return " + heartData.victimName() + "'s heart."));
            } else {
                // Victim offline — just free their soul
                serverPlayer.sendSystemMessage(Component.literal("§7The victim is not nearby. The heart dissolves into light, freeing their soul."));
            }

            stack.shrink(1);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        var heartData = stack.get(ModDataComponents.STOLEN_HEART.value());
        if (heartData != null && heartData.victimName() != null) {
            tooltip.add(Component.literal("§4§lStolen Heart").withStyle(ChatFormatting.DARK_RED));
            tooltip.add(Component.literal("§7Belongs to: §c" + heartData.victimName()));
            tooltip.add(Component.literal("§8Right-click to return the heart."));
            tooltip.add(Component.literal("§8Or sneak + attack to destroy it..."));
        } else {
            tooltip.add(Component.literal("§4§lEmpty Heart Container"));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, net.minecraft.world.entity.LivingEntity target, net.minecraft.world.entity.LivingEntity attacker) {
        // Sneak + attack = destroy the heart, freeing the victim
        if (!attacker.level().isClientSide() && attacker instanceof ServerPlayer player) {
            var heartData = stack.get(ModDataComponents.STOLEN_HEART.value());
            if (heartData != null && heartData.victimUUID() != null) {
                ServerPlayer victim = player.level().getServer().getPlayerList().getPlayer(heartData.victimUUID());
                if (victim != null && victim.isAlive()) {
                    // Clear the ripped flag — heart is destroyed, victim is freed
                    PlayerSupernaturalData victimData = victim.getData(PlayerSupernaturalData.TYPE);
                    victimData.setHeartRipped(false, null);

                    victim.sendSystemMessage(Component.literal("§d§lYour heart is destroyed! You feel strangely... free."));
                    player.sendSystemMessage(Component.literal("§4You destroy " + heartData.victimName() + "'s heart. They are freed."));
                } else {
                    player.sendSystemMessage(Component.literal("§7The heart crumbles to dust. The soul is freed."));
                }
                stack.shrink(1);
            }
        }
        return true;
    }
}
