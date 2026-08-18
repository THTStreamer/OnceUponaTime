package com.example.ouat.events;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.data.UniqueRoleRegistry;
import com.example.ouat.registry.ModDataComponents;
import com.example.ouat.registry.ModItems;
import com.example.ouat.roles.DarkOneRole;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.UUID;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class DarkOneEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);

            if (DarkOneRole.isDarkOne(data)) {
                UniqueRoleRegistry registry = UniqueRoleRegistry.get(player.level().getServer());
                UUID currentDarkOne = registry.getHolder(UniqueRoleRegistry.RoleType.DARK_ONE);

                if (currentDarkOne == null || !currentDarkOne.equals(player.getUUID())) {
                    DarkOneRole.removeRole(player);
                    OnceUponATime.LOGGER.warn("Player {} had Dark One data but is not the registered Dark One. Role removed.", player.getName().getString());
                } else {
                    DarkOneRole.grantRole(player);
                }
            }

            boolean hasDagger = false;
            for (ItemStack stack : player.getInventory().items) {
                if (stack.is(ModItems.DARK_ONE_DAGGER.value())) {
                    hasDagger = true;
                    break;
                }
            }
            if (!hasDagger) {
                UniqueRoleRegistry registry = UniqueRoleRegistry.get(player.level().getServer());
                UUID darkOneUUID = registry.getHolder(UniqueRoleRegistry.RoleType.DARK_ONE);

                if (darkOneUUID != null && darkOneUUID.equals(player.getUUID())) {
                    ItemStack daggerStack = new ItemStack(ModItems.DARK_ONE_DAGGER.get());
                    daggerStack.set(ModDataComponents.DARK_ONE_DAGGER.value(),
                            new ModDataComponents.DarkOneDaggerData(player.getUUID(), java.util.UUID.randomUUID(), true));
                    player.getInventory().add(daggerStack);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;

        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (DarkOneRole.isDarkOne(data)) {
            UniqueRoleRegistry registry = UniqueRoleRegistry.get(player.level().getServer());
            registry.releaseRole(UniqueRoleRegistry.RoleType.DARK_ONE);
            DarkOneRole.removeRole(player);

            player.level().getServer().getPlayerList().broadcastSystemMessage(
                    net.minecraft.network.chat.Component.literal("§4§lThe Dark One has fallen! The power is released!"),
                    false
            );

            for (ItemStack stack : player.getInventory().items) {
                if (stack.is(ModItems.DARK_ONE_DAGGER.value())) {
                    stack.set(ModDataComponents.DARK_ONE_DAGGER.value(),
                            new ModDataComponents.DarkOneDaggerData(java.util.UUID.randomUUID(), java.util.UUID.randomUUID(), false));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer target)) return;
        if (target.level().isClientSide()) return;

        PlayerSupernaturalData targetData = target.getData(PlayerSupernaturalData.TYPE);
        if (!DarkOneRole.isDarkOne(targetData)) return;

        net.minecraft.world.entity.Entity damageSourceEntity = event.getSource().getEntity();

        boolean killedByDagger = false;
        if (damageSourceEntity instanceof ServerPlayer attacker) {
            for (ItemStack stack : attacker.getInventory().items) {
                if (stack.is(ModItems.DARK_ONE_DAGGER.value())) {
                    ModDataComponents.DarkOneDaggerData daggerData = stack.get(ModDataComponents.DARK_ONE_DAGGER.value());
                    if (daggerData != null && daggerData.isAuthentic()) {
                        killedByDagger = true;
                        break;
                    }
                }
            }
        }

        if (!killedByDagger) {
            event.setNewDamage(0.0F);
            return;
        }

        if (damageSourceEntity instanceof ServerPlayer attacker) {
            PlayerSupernaturalData attackerData = attacker.getData(PlayerSupernaturalData.TYPE);
            if (DarkOneRole.isDarkOne(attackerData)) {
                event.setNewDamage(event.getNewDamage() * 1.25F);
            }
        }
    }
}
