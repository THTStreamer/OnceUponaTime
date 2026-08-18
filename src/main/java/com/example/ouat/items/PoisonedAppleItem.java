package com.example.ouat.items;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.CurseInstance;
import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

public class PoisonedAppleItem extends Item {
    public PoisonedAppleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Poisoned Apple: sleeping curse
            net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 5.0);
            if (target == null) {
                serverPlayer.sendSystemMessage(Component.literal("§cNo target nearby."));
                return InteractionResultHolder.fail(stack);
            }

            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.BLINDNESS, 600, 0));
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 600, 2));
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.WITHER, 400, 1));

            PlayerSupernaturalData targetData = target.getData(PlayerSupernaturalData.TYPE);
            targetData.addCurse(new CurseInstance(
                    ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "sleeping_curse"),
                    "Sleeping Curse", 60000, serverPlayer.getUUID()
            ));

            stack.shrink(1);

            serverPlayer.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.5F);
            target.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.3F);
            serverPlayer.sendSystemMessage(Component.literal("§4§lYou offer the poisoned apple to " + target.getName().getString() + "!"));
            target.sendSystemMessage(Component.literal("§4§lYou bite the apple and fall into an enchanted sleep..."));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§4§lPoisoned Apple").withStyle(ChatFormatting.DARK_RED));
        tooltip.add(Component.literal("§7A cursed apple from the Queen's garden"));
        tooltip.add(Component.literal("§7Right-click near a player to put them to sleep"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
