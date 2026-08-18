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

public class MagicGlobeItem extends Item {
    public MagicGlobeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Magic Globe: reveals all players on the server
            serverPlayer.sendSystemMessage(Component.literal("§b§lYou gaze into the magic globe..."));
            for (ServerPlayer other : serverPlayer.level().getServer().getPlayerList().getPlayers()) {
                serverPlayer.sendSystemMessage(Component.literal("§7- " + other.getName().getString() +
                        " at " + (int) other.getX() + ", " + (int) other.getY() + ", " + (int) other.getZ()));
            }

            serverPlayer.playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.5F, 1.0F);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§b§lMagic Globe").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("§7Shows all players in the realm"));
        tooltip.add(Component.literal("§7Right-click to see player locations"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
