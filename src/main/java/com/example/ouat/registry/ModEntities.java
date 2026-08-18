package com.example.ouat.registry;

import com.example.ouat.OnceUponATime;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, OnceUponATime.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<com.example.ouat.entities.EvilQueenEntity>> EVIL_QUEEN =
            ENTITIES.register("evil_queen", () ->
                    EntityType.Builder.of(com.example.ouat.entities.EvilQueenEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.8F)
                            .clientTrackingRange(8)
                            .build("evil_queen"));

    public static final DeferredHolder<EntityType<?>, EntityType<com.example.ouat.entities.ForestFairyEntity>> FOREST_FAIRY =
            ENTITIES.register("forest_fairy", () ->
                    EntityType.Builder.of(com.example.ouat.entities.ForestFairyEntity::new, MobCategory.CREATURE)
                            .sized(0.4F, 0.8F)
                            .clientTrackingRange(6)
                            .build("forest_fairy"));

    public static final DeferredHolder<EntityType<?>, EntityType<com.example.ouat.entities.LostSoulEntity>> LOST_SOUL =
            ENTITIES.register("lost_soul", () ->
                    EntityType.Builder.of(com.example.ouat.entities.LostSoulEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.8F)
                            .clientTrackingRange(8)
                            .build("lost_soul"));

    public static final DeferredHolder<EntityType<?>, EntityType<com.example.ouat.entities.LostBoyEntity>> LOST_BOY =
            ENTITIES.register("lost_boy", () ->
                    EntityType.Builder.of(com.example.ouat.entities.LostBoyEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.2F)
                            .clientTrackingRange(8)
                            .build("lost_boy"));

    public static final DeferredHolder<EntityType<?>, EntityType<com.example.ouat.entities.MadHatterEntity>> MAD_HATTER =
            ENTITIES.register("mad_hatter", () ->
                    EntityType.Builder.of(com.example.ouat.entities.MadHatterEntity::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.8F)
                            .clientTrackingRange(8)
                            .build("mad_hatter"));

    public static final DeferredHolder<EntityType<?>, EntityType<com.example.ouat.entities.DarkSwarmEntity>> DARK_SWARM =
            ENTITIES.register("dark_swarm", () ->
                    EntityType.Builder.of(com.example.ouat.entities.DarkSwarmEntity::new, MobCategory.MONSTER)
                            .sized(1.2F, 1.8F)
                            .clientTrackingRange(10)
                            .build("dark_swarm"));

    public static void register(IEventBus modBus) {
        ENTITIES.register(modBus);
    }
}
