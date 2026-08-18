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

public class MaleficentStaffItem extends Item {
    public MaleficentStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Maleficent's Staff: dark magic power boost + fire
            serverPlayer.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.DAMAGE_BOOST, 600, 2));
            serverPlayer.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE, 600, 0));
            serverPlayer.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 400, 1));

            // Launch fireball
            net.minecraft.world.entity.projectile.SmallFireball fireball = new net.minecraft.world.entity.projectile.SmallFireball(
                    net.minecraft.world.entity.EntityType.SMALL_FIREBALL, serverPlayer.level());
            double yRot = Math.toRadians(serverPlayer.getYRot());
            double xRot = Math.toRadians(serverPlayer.getXRot());
            net.minecraft.world.phys.Vec3 look = new net.minecraft.world.phys.Vec3(
                    -Math.sin(yRot) * Math.cos(xRot), -Math.sin(xRot), Math.cos(yRot) * Math.cos(xRot));
            fireball.setPos(serverPlayer.getX() + look.x * 2, serverPlayer.getEyeY() + look.y * 2, serverPlayer.getZ() + look.z * 2);
            fireball.shootFromRotation(serverPlayer, serverPlayer.getXRot(), serverPlayer.getYRot(), 0.0F, 4.0F, 0.0F);
            serverPlayer.level().addFreshEntity(fireball);

            serverPlayer.playSound(SoundEvents.BLAZE_SHOOT, 1.5F, 0.8F);
            serverPlayer.sendSystemMessage(Component.literal("§4§lMaleficent's staff surges with dark power!"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, net.minecraft.world.entity.LivingEntity target, net.minecraft.world.entity.LivingEntity attacker) {
        // Dark fire damage
        target.hurt(target.damageSources().magic(), 18.0F);
        target.setRemainingFireTicks(100);

        if (!attacker.level().isClientSide() && attacker instanceof ServerPlayer player) {
            player.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 0.8F);
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§4§lMaleficent's Staff").withStyle(ChatFormatting.DARK_RED));
        tooltip.add(Component.literal("§7The staff of the Mistress of Evil"));
        tooltip.add(Component.literal("§7Right-click for dark power and fire"));
        tooltip.add(Component.literal("§7Attacks set targets on fire"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
