package com.example.ouat.items;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.CurseInstance;
import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class CursedTalismanItem extends Item {
    public CursedTalismanItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            Player target = level.getNearestPlayer(serverPlayer, 5.0);
            if (target == null) {
                serverPlayer.sendSystemMessage(Component.literal("§cNo target within range. Look at a player within 5 blocks."));
                return InteractionResultHolder.fail(stack);
            }

            if (!(target instanceof ServerPlayer targetPlayer)) {
                serverPlayer.sendSystemMessage(Component.literal("§cTarget must be a player."));
                return InteractionResultHolder.fail(stack);
            }

            PlayerSupernaturalData targetData = targetPlayer.getData(PlayerSupernaturalData.TYPE);
            targetData.addCurse(new CurseInstance(
                    ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "talisman_curse"),
                    "Cursed Talisman",
                    120000,
                    serverPlayer.getUUID()
            ));

            targetPlayer.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.WEAKNESS, 2400, 1));
            targetPlayer.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 2400, 1));

            targetPlayer.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.5F);
            targetPlayer.sendSystemMessage(Component.literal("§4§lYou feel cursed by a dark talisman!"));
            serverPlayer.sendSystemMessage(Component.literal("§4§lYou curse " + targetPlayer.getName().getString() + " with the talisman!"));

            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§4§lCursed Talisman").withStyle(ChatFormatting.DARK_RED));
        tooltip.add(Component.literal("§7Right-click to curse a nearby player."));
        tooltip.add(Component.literal("§8Applies: Weakness + Slowness for 2 minutes."));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
