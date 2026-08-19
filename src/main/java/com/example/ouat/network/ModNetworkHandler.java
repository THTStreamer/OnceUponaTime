package com.example.ouat.network;

import com.example.ouat.OnceUponATime;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetworkHandler {
    public static void register() {
    }

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playBidirectional(
                PlayerDataSyncPacket.TYPE,
                PlayerDataSyncPacket.STREAM_CODEC,
                PlayerDataSyncPacket::handle
        );

        registrar.playBidirectional(
                RoleRegistrySyncPacket.TYPE,
                RoleRegistrySyncPacket.STREAM_CODEC,
                RoleRegistrySyncPacket::handle
        );

        registrar.playToClient(
                PurpleSmokeSpawnPacket.TYPE,
                PurpleSmokeSpawnPacket.STREAM_CODEC,
                PurpleSmokeSpawnPacket::handle
        );

        registrar.playToClient(
                DarkSmokeSpawnPacket.TYPE,
                DarkSmokeSpawnPacket.STREAM_CODEC,
                DarkSmokeSpawnPacket::handle
        );

        registrar.playToClient(
                WardBoundaryPacket.TYPE,
                WardBoundaryPacket.STREAM_CODEC,
                WardBoundaryPacket::handle
        );
    }
}
