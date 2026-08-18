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

public class EnchantedCompassItem extends Item {
    public EnchantedCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Enchanted Compass: teleports to spawn
            net.minecraft.server.level.ServerLevel targetLevel = serverPlayer.getServer().getLevel(serverPlayer.level().dimension());
            serverPlayer.teleportTo(
                    targetLevel.getSharedSpawnPos().getX() + 0.5,
                    targetLevel.getSharedSpawnPos().getY(),
                    targetLevel.getSharedSpawnPos().getZ() + 0.5
            );

            serverPlayer.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            serverPlayer.sendSystemMessage(Component.literal("§aThe enchanted compass guides you home!"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§a§lEnchanted Compass").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("§7Guides the holder to their desired location"));
        tooltip.add(Component.literal("§7Right-click to return to spawn"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
