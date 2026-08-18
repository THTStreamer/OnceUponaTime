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

public class FlyingBroomstickItem extends Item {
    public FlyingBroomstickItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Flying Broomstick: grants flight
            serverPlayer.getAbilities().mayfly = true;
            serverPlayer.onUpdateAbilities();
            serverPlayer.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 600, 1));

            serverPlayer.playSound(SoundEvents.ELYTRA_FLYING, 1.5F, 1.0F);
            serverPlayer.sendSystemMessage(Component.literal("§5§lYou mount the broomstick and take to the skies!"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§5§lFlying Broomstick").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.literal("§7A witch's enchanted broomstick"));
        tooltip.add(Component.literal("§7Right-click to fly for 5 minutes"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
