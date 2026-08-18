package com.example.ouat.particles;

import com.example.ouat.OnceUponATime;
import com.example.ouat.registry.ModParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModParticleProviders {

    @SubscribeEvent
    public static void register(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SMOKE_SOFT.get(), PurpleSmokeParticle.Provider::new);
    }
}
