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

public class GrimoireItem extends Item {
    private final String grimoireType;
    private final int proficiencyBoost;

    public GrimoireItem(Properties properties, String grimoireType, int proficiencyBoost) {
        super(properties);
        this.grimoireType = grimoireType;
        this.proficiencyBoost = proficiencyBoost;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PlayerSupernaturalData data = serverPlayer.getData(PlayerSupernaturalData.TYPE);

            if (!data.isMagicallyGifted()) {
                data.setMagicalAlignment(PlayerSupernaturalData.MagicalAlignment.NEUTRAL);
            }

            // In OUAT, grimoires contain knowledge that strengthens magical ability
            // They don't "teach" specific spells - they deepen understanding of magic
            data.addMagicProficiency(proficiencyBoost);

            serverPlayer.sendSystemMessage(Component.literal("§aYou study the " + grimoireType + " Grimoire and gain magical understanding!"));
            serverPlayer.sendSystemMessage(Component.literal("§aYour magic proficiency increases by " + proficiencyBoost + "!"));
            serverPlayer.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§6§lGrimoire of " + grimoireType).withStyle(ChatFormatting.BOLD));
        tooltip.add(Component.literal("§7Right-click to study and gain magical proficiency."));
        tooltip.add(Component.literal("§8In OUAT, grimoires deepen magical understanding."));
        tooltip.add(Component.literal("§8Magic comes from within, not from books."));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
