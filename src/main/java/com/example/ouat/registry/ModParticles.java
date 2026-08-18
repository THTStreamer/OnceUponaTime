package com.example.ouat.registry;

import com.example.ouat.OnceUponATime;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, OnceUponATime.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SMOKE_SOFT =
            PARTICLE_TYPES.register("smoke_soft", () -> new SimpleParticleType(false));

    public static void register(IEventBus modBus) {
        PARTICLE_TYPES.register(modBus);
    }
}
