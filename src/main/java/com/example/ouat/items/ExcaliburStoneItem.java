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

public class ExcaliburStoneItem extends Item {
    public ExcaliburStoneItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Excalibur Stone: when Excalibur is pulled, grants hero status
            com.example.ouat.data.PlayerSupernaturalData data = serverPlayer.getData(com.example.ouat.data.PlayerSupernaturalData.TYPE);
            data.addMagicProficiency(15);
            serverPlayer.heal(20.0F);
            serverPlayer.getAbilities().mayfly = true;
            serverPlayer.onUpdateAbilities();

            serverPlayer.playSound(SoundEvents.TOTEM_USE, 2.0F, 1.0F);
            serverPlayer.sendSystemMessage(Component.literal("§6§lYou pull Excalibur from the stone! You are the true king!"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§6§lExcalibur Stone").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("§7The stone holding Excalibur"));
        tooltip.add(Component.literal("§7Right-click to pull the sword and become king"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
