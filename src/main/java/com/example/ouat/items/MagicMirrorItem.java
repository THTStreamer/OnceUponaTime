package com.example.ouat.items;

import com.example.ouat.OnceUponATime;
import com.example.ouat.client.gui.MagicMirrorScreen;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.dimension.MirrorDimensionTeleporter;
import com.example.ouat.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.phys.AABB;

import java.util.List;

public class MagicMirrorItem extends Item {
    public MagicMirrorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        // Sneak + right-click: trap a nearby player in the mirror
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                var mirrorData = stack.get(ModDataComponents.MIRROR_DATA.value());
                boolean hasTrapped = mirrorData != null && mirrorData.trappedEntity() != null;

                if (hasTrapped) {
                    serverPlayer.sendSystemMessage(Component.literal("§7The mirror already contains a trapped soul. Right-click to scry, or attack to crush."));
                    return InteractionResultHolder.fail(stack);
                }

                // Find nearest player within 4 blocks
                AABB scanBox = player.getBoundingBox().inflate(4.0);
                Player target = null;
                double closestDist = Double.MAX_VALUE;
                for (Player p : level.players()) {
                    if (p != player && p.isAlive() && scanBox.contains(p.position())) {
                        double dist = player.distanceTo(p);
                        if (dist < closestDist) {
                            closestDist = dist;
                            target = p;
                        }
                    }
                }

                if (target == null) {
                    serverPlayer.sendSystemMessage(Component.literal("§7No one is nearby to trap."));
                    return InteractionResultHolder.fail(stack);
                }

                if (target instanceof ServerPlayer targetPlayer) {
                    PlayerSupernaturalData casterData = serverPlayer.getData(PlayerSupernaturalData.TYPE);
                    PlayerSupernaturalData targetData = targetPlayer.getData(PlayerSupernaturalData.TYPE);
                    int casterPower = casterData.getMagicProficiency();
                    int targetPower = targetData.getMagicProficiency();

                    if (targetPower > casterPower + 10) {
                        serverPlayer.hurt(serverPlayer.damageSources().magic(), 8.0F);
                        serverPlayer.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                net.minecraft.world.effect.MobEffects.CONFUSION, 200, 0));
                        stack.shrink(1);
                        serverPlayer.playSound(SoundEvents.GLASS_BREAK, 2.0F, 0.5F);
                        serverPlayer.sendSystemMessage(Component.literal("§4§lThe mirror shatters! The target's power was too great!"));
                        targetPlayer.sendSystemMessage(Component.literal("§aThe mirror attack backfires!"));
                        return InteractionResultHolder.fail(stack);
                    }

                    if (targetPower > casterPower - 5 && serverPlayer.getRandom().nextBoolean()) {
                        serverPlayer.sendSystemMessage(Component.literal("§c" + targetPlayer.getName().getString() + " resists the mirror's pull!"));
                        targetPlayer.sendSystemMessage(Component.literal("§aYou resist the mirror's magic!"));
                        return InteractionResultHolder.fail(stack);
                    }

                    // Trap in Mirror Dimension
                    stack.set(ModDataComponents.MIRROR_DATA.value(),
                            new ModDataComponents.MagicMirrorData(targetPlayer.getUUID()));
                    MirrorDimensionTeleporter.sendToMirrorDimension(targetPlayer);
                    serverPlayer.playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 2.0F, 0.5F);
                    serverPlayer.sendSystemMessage(Component.literal("§5§l" + targetPlayer.getName().getString() + " is trapped in the Mirror Dimension!"));
                    return InteractionResultHolder.sidedSuccess(stack, false);
                }
            }
            return InteractionResultHolder.fail(stack);
        }

        // Normal right-click: open GUI to scry (only if mirror has trapped data)
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            var mirrorData = stack.get(ModDataComponents.MIRROR_DATA.value());
            if (mirrorData != null && mirrorData.trappedEntity() != null) {
                serverPlayer.sendSystemMessage(Component.literal("§5The mirror shimmers... you gaze upon the mortal realm."));
            } else {
                serverPlayer.sendSystemMessage(Component.literal("§7The mirror is empty. Sneak + right-click a player to trap them."));
                return InteractionResultHolder.fail(stack);
            }
            serverPlayer.playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.0F, 1.0F);
        }

        // Client-side: only open GUI if mirror has trapped data
        if (level.isClientSide()) {
            var mirrorData = stack.get(ModDataComponents.MIRROR_DATA.value());
            if (mirrorData != null && mirrorData.trappedEntity() != null) {
                List<Player> players = new java.util.ArrayList<>(level.players());
                players.removeIf(p -> p == player);
                Minecraft.getInstance().setScreen(new MagicMirrorScreen(players));
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, net.minecraft.world.entity.LivingEntity target, net.minecraft.world.entity.LivingEntity attacker) {
        if (!attacker.level().isClientSide() && attacker instanceof ServerPlayer player && target instanceof ServerPlayer targetPlayer) {
            if (player.isShiftKeyDown()) {
                PlayerSupernaturalData casterData = player.getData(PlayerSupernaturalData.TYPE);
                PlayerSupernaturalData targetData = targetPlayer.getData(PlayerSupernaturalData.TYPE);

                int casterPower = casterData.getMagicProficiency();
                int targetPower = targetData.getMagicProficiency();

                if (targetPower > casterPower + 10) {
                    player.hurt(player.damageSources().magic(), 8.0F);
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.CONFUSION, 200, 0));
                    stack.shrink(1);
                    player.playSound(SoundEvents.GLASS_BREAK, 2.0F, 0.5F);
                    player.sendSystemMessage(Component.literal("§4§lThe mirror shatters! The target's power was too great!"));
                    targetPlayer.sendSystemMessage(Component.literal("§aThe mirror attack backfires!"));
                    return true;
                }

                if (targetPower > casterPower - 5) {
                    if (player.getRandom().nextBoolean()) {
                        player.sendSystemMessage(Component.literal("§c" + targetPlayer.getName().getString() + " resists the mirror's pull!"));
                        targetPlayer.sendSystemMessage(Component.literal("§aYou resist the mirror's magic!"));
                        return true;
                    }
                }

                // Trap target in Mirror Dimension
                stack.set(ModDataComponents.MIRROR_DATA.value(),
                        new ModDataComponents.MagicMirrorData(targetPlayer.getUUID()));
                MirrorDimensionTeleporter.sendToMirrorDimension(targetPlayer);
                player.playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 2.0F, 0.5F);
                player.sendSystemMessage(Component.literal("§5§l" + targetPlayer.getName().getString() + " is trapped in the Mirror Dimension!"));
                return true;
            }
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        var mirrorData = stack.get(ModDataComponents.MIRROR_DATA.value());
        tooltip.add(Component.literal("§5§lMagic Mirror").withStyle(ChatFormatting.DARK_PURPLE));
        if (mirrorData != null && mirrorData.trappedEntity() != null) {
            tooltip.add(Component.literal("§5Contains: A trapped soul"));
            tooltip.add(Component.literal("§7Right-click to open the mirror and view others."));
            tooltip.add(Component.literal("§7Sneak + right-click to trap another player."));
        } else {
            tooltip.add(Component.literal("§7Empty. Sneak + right-click a player to trap them."));
            tooltip.add(Component.literal("§8They will be sent to the Mirror Dimension!"));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
