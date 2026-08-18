package com.example.ouat.events;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.data.UniqueRoleRegistry;
import com.example.ouat.roles.TruestBelieverRole;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.UUID;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class TruestBelieverEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);

            if (TruestBelieverRole.isTruestBeliever(data)) {
                UniqueRoleRegistry registry = UniqueRoleRegistry.get(player.level().getServer());
                UUID currentBeliever = registry.getHolder(UniqueRoleRegistry.RoleType.TRUEST_BELIEVER);

                if (currentBeliever == null || !currentBeliever.equals(player.getUUID())) {
                    TruestBelieverRole.removeRole(player);
                    OnceUponATime.LOGGER.warn("Player {} had Truest Believer data but is not the registered Truest Believer. Role removed.", player.getName().getString());
                } else {
                    TruestBelieverRole.grantRole(player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;

        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (TruestBelieverRole.isTruestBeliever(data)) {
            UniqueRoleRegistry registry = UniqueRoleRegistry.get(player.level().getServer());
            registry.releaseRole(UniqueRoleRegistry.RoleType.TRUEST_BELIEVER);
            TruestBelieverRole.removeRole(player);

            player.level().getServer().getPlayerList().broadcastSystemMessage(
                    net.minecraft.network.chat.Component.literal("§d§lThe Truest Believer has fallen... Reality trembles."),
                    false
            );
        }
    }
}
