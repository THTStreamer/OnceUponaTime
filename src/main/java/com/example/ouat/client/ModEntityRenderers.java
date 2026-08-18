package com.example.ouat.client;

import com.example.ouat.OnceUponATime;
import com.example.ouat.registry.ModEntities;
import com.example.ouat.registry.ModMenus;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        EntityRendererProvider zombieProvider = (EntityRendererProvider.Context ctx) -> new ZombieRenderer(ctx);
        event.registerEntityRenderer(ModEntities.EVIL_QUEEN.get(), zombieProvider);
        event.registerEntityRenderer(ModEntities.FOREST_FAIRY.get(), zombieProvider);
        event.registerEntityRenderer(ModEntities.LOST_SOUL.get(), zombieProvider);
        event.registerEntityRenderer(ModEntities.LOST_BOY.get(), zombieProvider);
        event.registerEntityRenderer(ModEntities.MAD_HATTER.get(), zombieProvider);
        event.registerEntityRenderer(ModEntities.DARK_SWARM.get(), zombieProvider);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.POTION_BREWER.get(), PotionBrewerScreen::new);
    }
}
