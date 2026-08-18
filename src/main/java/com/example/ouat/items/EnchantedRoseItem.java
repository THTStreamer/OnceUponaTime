package com.example.ouat.items;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.BlessingInstance;
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

public class EnchantedRoseItem extends Item {
    public EnchantedRoseItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            int healed = 0;
            for (ServerPlayer nearby : level.getServer().getPlayerList().getPlayers()) {
                if (nearby.distanceTo(serverPlayer) <= 10.0) {
                    nearby.heal(6.0F);

                    PlayerSupernaturalData data = nearby.getData(PlayerSupernaturalData.TYPE);
                    if (!data.getCurses().isEmpty()) {
                        for (var curse : data.getCurses()) {
                            data.removeCurse(curse.getCurseId());
                        }
                        nearby.sendSystemMessage(Component.literal("§dThe Enchanted Rose breaks your curses!"));
                    }

                    data.addBlessing(new BlessingInstance(
                            ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "true_loves_blessing"),
                            "True Love's Blessing",
                            600000,
                            ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "enchanted_rose")
                    ));

                    nearby.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.5F);
                    nearby.sendSystemMessage(Component.literal("§d" + serverPlayer.getName().getString() + " bestows True Love's Blessing upon you!"));
                    healed++;
                }
            }

            if (healed > 0) {
                serverPlayer.sendSystemMessage(Component.literal("§dThe Enchanted Rose blesses " + healed + " nearby players!"));
                stack.shrink(1);
            } else {
                serverPlayer.sendSystemMessage(Component.literal("§cNo one is close enough for the rose's magic."));
                return InteractionResultHolder.fail(stack);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§d§lEnchanted Rose").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.literal("§7Right-click to heal and bless nearby players."));
        tooltip.add(Component.literal("§8Breaks curses and grants True Love's Blessing."));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
