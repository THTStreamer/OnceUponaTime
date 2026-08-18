package com.example.ouat.config;

import com.example.ouat.OnceUponATime;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {
    public static ServerConfig SERVER_CONFIG;

    public static void registerConfig(ModContainer modContainer) {
        SERVER_CONFIG = new ServerConfig(new ModConfigSpec.Builder());
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.SERVER, SERVER_CONFIG.getSpec());
    }

    public static class ServerConfig {
        private final ModConfigSpec spec;

        public final ModConfigSpec.IntValue darkOneRitualStructureSize;
        public final ModConfigSpec.IntValue darkOneDaggerDamage;
        public final ModConfigSpec.IntValue darkOneAbilityCooldown;
        public final ModConfigSpec.IntValue darkOneCompulsionCooldown;
        public final ModConfigSpec.IntValue darkOneCompulsionRange;

        public final ModConfigSpec.IntValue maxMagicProficiency;
        public final ModConfigSpec.IntValue magicLearningRate;
        public final ModConfigSpec.IntValue spellCooldownMultiplier;

        public final ModConfigSpec.IntValue saviorLightMagicDamage;
        public final ModConfigSpec.IntValue saviorCurseRemovalPower;
        public final ModConfigSpec.IntValue truestBeliefPower;
        public final ModConfigSpec.IntValue authorRealityAlterationCooldown;

        public final ModConfigSpec.IntValue grimoireSpawnChance;
        public final ModConfigSpec.IntValue grimoireMaxSpells;
        public final ModConfigSpec.IntValue grimoireDangerLevel;

        public final ModConfigSpec.IntValue ritualBaseCooldown;
        public final ModConfigSpec.IntValue ritualIngredientConsumptionChance;

        public final ModConfigSpec.IntValue curseDuration;
        public final ModConfigSpec.IntValue curseRemovalDifficulty;

        public ServerConfig(ModConfigSpec.Builder builder) {
            builder.push("dark_one");
            darkOneRitualStructureSize = builder
                    .comment("Size of the Dark One ritual structure detection range")
                    .defineInRange("ritualStructureSize", 5, 3, 10);
            darkOneDaggerDamage = builder
                    .comment("Damage dealt by the Dark One Dagger")
                    .defineInRange("daggerDamage", 20, 1, 100);
            darkOneAbilityCooldown = builder
                    .comment("Cooldown for Dark One abilities in ticks")
                    .defineInRange("abilityCooldown", 100, 20, 600);
            darkOneCompulsionCooldown = builder
                    .comment("Cooldown for dagger compulsion commands in ticks")
                    .defineInRange("compulsionCooldown", 1200, 100, 72000);
            darkOneCompulsionRange = builder
                    .comment("Maximum range for dagger compulsion commands")
                    .defineInRange("compulsionRange", 50, 10, 200);
            builder.pop();

            builder.push("magic");
            maxMagicProficiency = builder
                    .comment("Maximum magic proficiency level")
                    .defineInRange("maxProficiency", 100, 10, 1000);
            magicLearningRate = builder
                    .comment("Base rate of magic learning")
                    .defineInRange("learningRate", 1, 1, 10);
            spellCooldownMultiplier = builder
                    .comment("Multiplier for spell cooldowns")
                    .defineInRange("cooldownMultiplier", 1, 1, 10);
            builder.pop();

            builder.push("savior");
            saviorLightMagicDamage = builder
                    .comment("Damage dealt by Savior light magic")
                    .defineInRange("lightMagicDamage", 15, 1, 50);
            saviorCurseRemovalPower = builder
                    .comment("Power of Savior curse removal")
                    .defineInRange("curseRemovalPower", 100, 1, 1000);
            builder.pop();

            builder.push("truest_believer");
            truestBeliefPower = builder
                    .comment("Power of Truest Believer's reality-defying magic")
                    .defineInRange("beliefPower", 50, 1, 100);
            builder.pop();

            builder.push("author");
            authorRealityAlterationCooldown = builder
                    .comment("Cooldown for Author reality alteration in ticks")
                    .defineInRange("realityAlterationCooldown", 72000, 1000, 600000);
            builder.pop();

            builder.push("grimoire");
            grimoireSpawnChance = builder
                    .comment("Chance of finding a grimoire in structures (1 in N)")
                    .defineInRange("spawnChance", 100, 1, 10000);
            grimoireMaxSpells = builder
                    .comment("Maximum number of spells a grimoire can contain")
                    .defineInRange("maxSpells", 5, 1, 20);
            grimoireDangerLevel = builder
                    .comment("Danger level of absorbing a grimoire (1-10)")
                    .defineInRange("dangerLevel", 3, 1, 10);
            builder.pop();

            builder.push("ritual");
            ritualBaseCooldown = builder
                    .comment("Base cooldown for rituals in ticks")
                    .defineInRange("baseCooldown", 200, 20, 72000);
            ritualIngredientConsumptionChance = builder
                    .comment("Chance of consuming ritual ingredients (percentage)")
                    .defineInRange("ingredientConsumptionChance", 80, 0, 100);
            builder.pop();

            builder.push("curse");
            curseDuration = builder
                    .comment("Base duration for curses in ticks")
                    .defineInRange("baseDuration", 6000, 100, 600000);
            curseRemovalDifficulty = builder
                    .comment("Difficulty of removing curses (1-10)")
                    .defineInRange("removalDifficulty", 5, 1, 10);
            builder.pop();

            spec = builder.build();
        }

        public ModConfigSpec getSpec() {
            return spec;
        }
    }
}
