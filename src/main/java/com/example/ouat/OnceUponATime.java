package com.example.ouat;

import com.example.ouat.commands.OuATCommands;
import com.example.ouat.config.ModConfig;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.data.PlayerSupernaturalDataProvider;
import com.example.ouat.data.UniqueRoleRegistry;
import com.example.ouat.entities.*;
import com.example.ouat.magic.SpellRegistry;
import com.example.ouat.magic.spells.*;
import com.example.ouat.network.ModNetworkHandler;
import com.example.ouat.registry.ModBlocks;
import com.example.ouat.registry.ModCreativeTabs;
import com.example.ouat.registry.ModDataComponents;
import com.example.ouat.registry.ModEntities;
import com.example.ouat.registry.ModItems;
import com.example.ouat.registry.ModMenus;
import com.example.ouat.registry.ModParticles;
import com.example.ouat.artifacts.ArtifactRegistry;
import com.example.ouat.dimensions.MagicalPortal;
import com.example.ouat.ritual.RitualManager;
import com.example.ouat.rituals.AuthorRitual;
import com.example.ouat.rituals.DarkOneRitual;
import com.example.ouat.rituals.SaviorRitual;
import com.example.ouat.rituals.TruestBelieverRitual;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(OnceUponATime.MOD_ID)
public class OnceUponATime {
    public static final String MOD_ID = "onceuponatime";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);

    public OnceUponATime(IEventBus modBus, ModContainer modContainer) {
        ModItems.ITEMS.register(modBus);
        ModBlocks.register(modBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modBus);
        ModDataComponents.COMPONENTS.register(modBus);
        ATTACHMENT_TYPES.register(modBus);
        ModEntities.register(modBus);
        ModMenus.register(modBus);
        ModParticles.register(modBus);

        ATTACHMENT_TYPES.register("player_supernatural_data", () -> PlayerSupernaturalData.TYPE);

        modBus.addListener(this::commonSetup);
        modBus.addListener(this::onNewRegistry);
        modBus.addListener(ModItems::addDefaultComponents);
        modBus.addListener(this::onRegisterSpawnPlacements);
        modBus.addListener(this::onEntityAttributes);
        modBus.addListener(com.example.ouat.particles.ModParticleProviders::register);

        NeoForge.EVENT_BUS.addListener(OuATCommands::onCommandsRegister);
        NeoForge.EVENT_BUS.addListener(UniqueRoleRegistry::onServerStarting);
        NeoForge.EVENT_BUS.addListener(UniqueRoleRegistry::onServerStopping);
        NeoForge.EVENT_BUS.addListener(PlayerSupernaturalDataProvider::onPlayerClone);

        ModNetworkHandler.register();

        ModConfig.registerConfig(modContainer);

        LOGGER.info("Once Upon a Time mod initialized");
    }

    private void commonSetup(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            RitualManager.register(new DarkOneRitual());
            RitualManager.register(new SaviorRitual());
            RitualManager.register(new TruestBelieverRitual());
            RitualManager.register(new AuthorRitual());

            ArtifactRegistry.initializeArtifacts();
            MagicalPortal.initializeDefaultPortals();

            // Healing & Light
            SpellRegistry.register(new HealingLight());
            SpellRegistry.register(new LightBlast());
            SpellRegistry.register(new ProtectionSpell());
            SpellRegistry.register(new MemoryRestoration());
            SpellRegistry.register(new TrueLoveMagic());

            // Dark Magic
            SpellRegistry.register(new SleepingCurse());
            SpellRegistry.register(new Fireball());
            SpellRegistry.register(new HeartRipping());
            SpellRegistry.register(new HeartProtection());
            SpellRegistry.register(new HeartRelease());
            SpellRegistry.register(new MemoryWipe());
            SpellRegistry.register(new DarkOnesPower());
            SpellRegistry.register(new CurseOfCorruption());

            // Transformation & Body
            SpellRegistry.register(new ShapeshiftingSpell());
            SpellRegistry.register(new Immobilization());
            SpellRegistry.register(new AgeManipulation());
            SpellRegistry.register(new Invisibility());
            SpellRegistry.register(new Levitation());

            // Travel & Portal
            SpellRegistry.register(new TeleportationSpell());
            SpellRegistry.register(new PortalCreation());
            SpellRegistry.register(new Banishment());

            // Utility
            SpellRegistry.register(new Telekinesis());
            SpellRegistry.register(new WeatherControl());
            SpellRegistry.register(new Conjuration());

            // Season 4+ Spells (Frozen, Dark One, etc.)
            SpellRegistry.register(new SpellOfShatteredSight());
            SpellRegistry.register(new FrozenHeartCurse());
            SpellRegistry.register(new DreamcatcherSpell());
            SpellRegistry.register(new NightRootAbsorption());
            SpellRegistry.register(new SquidInkParalysis());
            SpellRegistry.register(new CurseOfEmptyHeart());
            SpellRegistry.register(new CurseOfTheSavior());

            // Utility & Concealment
            SpellRegistry.register(new ConcealSpell());

            LOGGER.info("Once Upon a Time common setup complete");
        });
    }

    private void onNewRegistry(net.neoforged.neoforge.registries.NewRegistryEvent event) {
    }

    private void onEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.EVIL_QUEEN.get(), EvilQueenEntity.createAttributes().build());
        event.put(ModEntities.FOREST_FAIRY.get(), ForestFairyEntity.createAttributes().build());
        event.put(ModEntities.LOST_SOUL.get(), LostSoulEntity.createAttributes().build());
        event.put(ModEntities.LOST_BOY.get(), LostBoyEntity.createAttributes().build());
        event.put(ModEntities.MAD_HATTER.get(), MadHatterEntity.createAttributes().build());
        event.put(ModEntities.DARK_SWARM.get(), DarkSwarmEntity.createAttributes().build());
    }

    private void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.EVIL_QUEEN.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.FOREST_FAIRY.get(), SpawnPlacementTypes.NO_RESTRICTIONS,
                Heightmap.Types.MOTION_BLOCKING, ForestFairyEntity::checkForestFairySpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.LOST_SOUL.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.LOST_BOY.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.MAD_HATTER.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.DARK_SWARM.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
