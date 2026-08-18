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

public class FairyDustItem extends Item {
    public FairyDustItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Fairy Dust: restores magic and grants flight temporarily
            com.example.ouat.data.PlayerSupernaturalData data = serverPlayer.getData(com.example.ouat.data.PlayerSupernaturalData.TYPE);
            data.addMagicProficiency(10);

            serverPlayer.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.LEVITATION, 200, 0));
            serverPlayer.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.SLOW_FALLING, 400, 0));
            serverPlayer.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.REGENERATION, 200, 1));

            stack.shrink(1);

            serverPlayer.playSound(SoundEvents.ELYTRA_FLYING, 1.5F, 1.5F);
            serverPlayer.sendSystemMessage(Component.literal("§b§lYou sprinkle fairy dust and feel light as a feather!"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§b§lFairy Dust").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("§7Restores magic and grants flight"));
        tooltip.add(Component.literal("§7One use only"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
