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

public class ExcaliburItem extends Item {
    public ExcaliburItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PlayerSupernaturalData data = serverPlayer.getData(PlayerSupernaturalData.TYPE);
            // Excalibur: Merlin's sword grants immense power
            data.addMagicProficiency(20);
            serverPlayer.heal(20.0F);
            serverPlayer.getAbilities().mayfly = true;
            serverPlayer.onUpdateAbilities();

            serverPlayer.playSound(SoundEvents.TOTEM_USE, 2.0F, 1.0F);
            serverPlayer.sendSystemMessage(Component.literal("§6§lYou wield Excalibur! The sword of kings grants you immense power!"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, net.minecraft.world.entity.LivingEntity target, net.minecraft.world.entity.LivingEntity attacker) {
        // Excalibur: enhanced damage
        target.hurt(target.damageSources().magic(), 25.0F);
        target.knockback(3.0, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());

        if (!attacker.level().isClientSide() && attacker instanceof ServerPlayer player) {
            player.playSound(SoundEvents.TOTEM_USE, 1.5F, 1.0F);
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§6§lExcalibur").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("§7The sword of the true king"));
        tooltip.add(Component.literal("§7Grants immense power to the wielder"));
        tooltip.add(Component.literal("§8Right-click to channel its power"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
