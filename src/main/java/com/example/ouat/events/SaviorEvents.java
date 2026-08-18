package com.example.ouat.events;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.data.UniqueRoleRegistry;
import com.example.ouat.roles.SaviorRole;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.UUID;

import java.util.UUID;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class SaviorEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);

            if (SaviorRole.isSavior(data)) {
                UniqueRoleRegistry registry = UniqueRoleRegistry.get(player.level().getServer());
                UUID currentSavior = registry.getHolder(UniqueRoleRegistry.RoleType.SAVIOR);

                if (currentSavior == null || !currentSavior.equals(player.getUUID())) {
                    SaviorRole.removeRole(player);
                    OnceUponATime.LOGGER.warn("Player {} had Savior data but is not the registered Savior. Role removed.", player.getName().getString());
                } else {
                    SaviorRole.grantRole(player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;

        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (SaviorRole.isSavior(data)) {
            UniqueRoleRegistry registry = UniqueRoleRegistry.get(player.level().getServer());
            registry.releaseRole(UniqueRoleRegistry.RoleType.SAVIOR);
            SaviorRole.removeRole(player);

            player.level().getServer().getPlayerList().broadcastSystemMessage(
                    net.minecraft.network.chat.Component.literal("§e§lThe Savior has fallen! The light dims..."),
                    false
            );
        }
    }
}
