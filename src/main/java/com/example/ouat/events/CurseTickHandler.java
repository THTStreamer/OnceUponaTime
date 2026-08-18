package com.example.ouat.events;

import com.example.ouat.OnceUponATime;
import com.example.ouat.curses.CurseManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CurseTickHandler {

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            CurseManager.tickCurses(player);
        }
    }
}
