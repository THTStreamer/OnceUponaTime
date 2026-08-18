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
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;

public class MagicBeanItem extends Item {
    public MagicBeanItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Magic Bean: creates a portal to a random far location
            int x = (int) (player.getX() + player.getRandom().nextIntBetweenInclusive(-500, 500));
            int z = (int) (player.getZ() + player.getRandom().nextIntBetweenInclusive(-500, 500));
            int y = player.level().getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

            player.teleportTo(x + 0.5, y + 1, z + 0.5);
            stack.shrink(1);

            serverPlayer.playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 0.5F);
            serverPlayer.sendSystemMessage(Component.literal("§aYou plant the magic bean and a portal opens!"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§a§lMagic Bean").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("§7Plant to open a portal to another realm"));
        tooltip.add(Component.literal("§7One use only"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
