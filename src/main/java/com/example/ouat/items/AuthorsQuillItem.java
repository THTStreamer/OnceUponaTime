package com.example.ouat.items;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.data.UniqueRoleRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;
import java.util.UUID;

public class AuthorsQuillItem extends Item {
    public AuthorsQuillItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            UniqueRoleRegistry registry = UniqueRoleRegistry.get(level.getServer());
            UUID authorUUID = registry.getHolder(UniqueRoleRegistry.RoleType.AUTHOR);

            if (authorUUID == null || !authorUUID.equals(serverPlayer.getUUID())) {
                serverPlayer.sendSystemMessage(Component.literal("§cOnly the Author can use this quill."));
                return InteractionResultHolder.fail(stack);
            }

            PlayerSupernaturalData data = serverPlayer.getData(PlayerSupernaturalData.TYPE);
            int story = data.getStoryProgression() % 4;

            ServerLevel serverLevel = (ServerLevel) level;
            switch (story) {
                case 0 -> {
                    serverLevel.setDayTime(serverLevel.getDayTime() + 6000);
                    serverPlayer.sendSystemMessage(Component.literal("§6§lThe Author rewrites time... Night falls!"));
                    serverLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 0.5F, 1.0F);
                }
                case 1 -> {
                    BlockPos pos = serverPlayer.blockPosition();
                    int x = serverPlayer.getRandom().nextIntBetweenInclusive(-100, 100);
                    int z = serverPlayer.getRandom().nextIntBetweenInclusive(-100, 100);
                    int y = serverLevel.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                    serverPlayer.teleportTo(x + 0.5, y + 1, z + 0.5);
                    serverPlayer.sendSystemMessage(Component.literal("§6§lThe Author rewrites fate... You are transported!"));
                    serverLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.5F);
                }
                case 2 -> {
                    serverLevel.setWeatherParameters(0, 6000, true, false);
                    serverPlayer.sendSystemMessage(Component.literal("§6§lThe Author conjures a storm!"));
                    serverLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 2.0F, 0.5F);
                }
                case 3 -> {
                    for (ServerPlayer online : level.getServer().getPlayerList().getPlayers()) {
                        online.heal(20.0F);
                        online.sendSystemMessage(Component.literal("§6§lThe Author writes a happy ending... Everyone is healed!"));
                    }
                    serverLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }

            data.advanceStoryProgression();
            if (stack.isDamageableItem()) {
                stack.hurtAndBreak(1, serverPlayer, serverPlayer.getEquipmentSlotForItem(stack));
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§6§lAuthor's Quill").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("§7Right-click to rewrite reality."));
        tooltip.add(Component.literal("§8Only the Author can use this."));
        tooltip.add(Component.literal("§8Cycles: Time → Travel → Weather → Healing"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
