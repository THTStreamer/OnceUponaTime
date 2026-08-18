package com.example.ouat.items;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.data.UniqueRoleRegistry;
import com.example.ouat.registry.ModDataComponents;
import com.example.ouat.roles.DarkOneRole;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class DarkOneDaggerItem extends Item {
    public DarkOneDaggerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        ModDataComponents.DarkOneDaggerData data = stack.get(ModDataComponents.DARK_ONE_DAGGER.value());

        if (data == null) {
            return InteractionResultHolder.fail(stack);
        }

        if (!data.isAuthentic()) {
            player.sendSystemMessage(Component.literal("§cThis dagger is not authentic."));
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            UniqueRoleRegistry registry = UniqueRoleRegistry.get(level.getServer());
            UUID darkOneUUID = registry.getHolder(UniqueRoleRegistry.RoleType.DARK_ONE);

            if (darkOneUUID == null) {
                serverPlayer.sendSystemMessage(Component.literal("§cThere is no Dark One to command."));
                return InteractionResultHolder.fail(stack);
            }

            if (darkOneUUID.equals(serverPlayer.getUUID())) {
                serverPlayer.sendSystemMessage(Component.literal("§cYou cannot command yourself with the dagger."));
                return InteractionResultHolder.fail(stack);
            }

            ServerPlayer darkOne = level.getServer().getPlayerList().getPlayer(darkOneUUID);
            if (darkOne == null) {
                serverPlayer.sendSystemMessage(Component.literal("§cThe Dark One is not online."));
                return InteractionResultHolder.fail(stack);
            }

            double distance = serverPlayer.distanceTo(darkOne);
            if (distance > 50) {
                serverPlayer.sendSystemMessage(Component.literal("§cThe Dark One is too far away. Distance: " + (int) distance + " blocks."));
                return InteractionResultHolder.fail(stack);
            }

            darkOne.teleportTo(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ());
            darkOne.sendSystemMessage(Component.literal("§4§lYou have been summoned by the dagger!"));
            serverPlayer.sendSystemMessage(Component.literal("§aThe Dark One has been summoned."));

            level.playSound(null, serverPlayer.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.5F);

            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide() && target instanceof ServerPlayer targetPlayer) {
            ModDataComponents.DarkOneDaggerData data = stack.get(ModDataComponents.DARK_ONE_DAGGER.value());
            if (data == null || !data.isAuthentic()) return true;

            if (!data.boundOwner().equals(targetPlayer.getUUID())) return true;

            UniqueRoleRegistry registry = UniqueRoleRegistry.get(attacker.level().getServer());
            UUID darkOneUUID = registry.getHolder(UniqueRoleRegistry.RoleType.DARK_ONE);

            if (darkOneUUID != null && darkOneUUID.equals(targetPlayer.getUUID())) {
                targetPlayer.kill();

                if (attacker instanceof ServerPlayer newDarkOne) {
                    registry.transferRole(newDarkOne.getUUID(), UniqueRoleRegistry.RoleType.DARK_ONE);
                    DarkOneRole.removeRole(targetPlayer);
                    DarkOneRole.grantRole(newDarkOne);

                    ModDataComponents.DarkOneDaggerData newData = new ModDataComponents.DarkOneDaggerData(
                            newDarkOne.getUUID(),
                            data.instanceId(),
                            true
                    );
                    stack.set(ModDataComponents.DARK_ONE_DAGGER.value(), newData);

                    newDarkOne.sendSystemMessage(Component.literal("§4§lYou have killed the Dark One and become the new Dark One!"));
                    targetPlayer.sendSystemMessage(Component.literal("§4§lYou have been killed by the dagger... You are no longer the Dark One."));

                    for (ServerPlayer otherPlayer : attacker.level().getServer().getPlayerList().getPlayers()) {
                        if (otherPlayer != newDarkOne && otherPlayer != targetPlayer) {
                            otherPlayer.sendSystemMessage(Component.literal(
                                    "§4§l" + targetPlayer.getName().getString() + " has been slain! " +
                                            newDarkOne.getName().getString() + " is now the Dark One!"));
                        }
                    }

                    attacker.level().playSound(null, targetPlayer.blockPosition(),
                            SoundEvents.WITHER_DEATH, SoundSource.HOSTILE, 2.0F, 0.5F);
                }
            }
        }

        return true;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        ModDataComponents.DarkOneDaggerData data = stack.get(ModDataComponents.DARK_ONE_DAGGER.value());
        return data != null && data.isAuthentic();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ModDataComponents.DarkOneDaggerData data = stack.get(ModDataComponents.DARK_ONE_DAGGER.value());
        if (data != null) {
            tooltip.add(Component.literal("§4§lDark One Dagger").withStyle(ChatFormatting.DARK_RED));
            tooltip.add(Component.literal("§7Bound to: " + (data.isAuthentic() ? "The Dark One" : "Unknown")).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("§8The dagger that can kill the Dark One...").withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.literal("§8Or transfer the power to another.").withStyle(ChatFormatting.DARK_GRAY));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide() && entity instanceof ServerPlayer player) {
            ModDataComponents.DarkOneDaggerData data = stack.get(ModDataComponents.DARK_ONE_DAGGER.value());
            if (data == null || !data.isAuthentic()) return;

            UniqueRoleRegistry registry = UniqueRoleRegistry.get(level.getServer());
            UUID currentDarkOne = registry.getHolder(UniqueRoleRegistry.RoleType.DARK_ONE);

            if (currentDarkOne == null) {
                stack.set(ModDataComponents.DARK_ONE_DAGGER.value(),
                        new ModDataComponents.DarkOneDaggerData(data.boundOwner(), data.instanceId(), false));
            }
        }
    }
}
