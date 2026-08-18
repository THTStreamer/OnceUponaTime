package com.example.ouat.particles;

import com.example.ouat.network.DarkSmokeSpawnPacket;
import com.example.ouat.registry.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DarkSmokeEffect {

    private static final double CURSE_RADIUS = 4800.0;
    private static final int WAVE_START_TICK = 15;
    private static final double WAVE_SPEED = 100.0;
    private static final int TOTAL_TICKS = 75;

    private static final float[][] GRAY_PALETTE = {
            {0.08F, 0.08F, 0.08F},
            {0.12F, 0.12F, 0.12F},
            {0.18F, 0.18F, 0.18F},
            {0.25F, 0.25F, 0.25F},
            {0.33F, 0.33F, 0.33F},
            {0.40F, 0.40F, 0.40F},
    };

    public static void startServer(ServerLevel level, ServerPlayer caster) {
        List<ServerPlayer> targets = new ArrayList<>();
        for (ServerPlayer target : level.getServer().getPlayerList().getPlayers()) {
            double distance = target.distanceTo(caster);
            if (distance <= CURSE_RADIUS) {
                targets.add(target);
            }
        }

        if (targets.isEmpty()) return;

        DarkSmokeSpawnPacket packet = new DarkSmokeSpawnPacket(
                caster.getX(), caster.getY(), caster.getZ(), targets.size());

        for (ServerPlayer viewer : level.getServer().getPlayerList().getPlayers()) {
            if (viewer.distanceTo(caster) <= CURSE_RADIUS + 200) {
                PacketDistributor.sendToPlayer(viewer, packet);
            }
        }

        MinecraftServer server = level.getServer();
        int maxDelay = WAVE_START_TICK + 2;

        for (ServerPlayer target : targets) {
            if (target == caster) continue;
            double distance = target.distanceTo(caster);
            int delayTicks = WAVE_START_TICK + (int) (distance / WAVE_SPEED);
            delayTicks = Math.max(delayTicks, WAVE_START_TICK + 2);
            if (delayTicks > maxDelay) maxDelay = delayTicks;

            final int finalDelay = delayTicks;
            server.tell(new TickTask(server.getTickCount() + delayTicks, () -> {
                if (target.isAlive()) {
                    com.example.ouat.dimensions.DimensionManager.teleportToDimension(
                            target, com.example.ouat.dimensions.DimensionManager.STORYBROOKE);

                    ServerLevel storyLevel = target.server.getLevel(
                            com.example.ouat.dimensions.DimensionManager.STORYBROOKE);
                    if (storyLevel != null) {
                        for (int i = 0; i < 80; i++) {
                            double angle = target.level().random.nextDouble() * Math.PI * 2;
                            double r = target.level().random.nextDouble() * 4.0;
                            storyLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE,
                                    target.getX() + Math.cos(angle) * r,
                                    target.getY() + target.level().random.nextDouble() * 3.0,
                                    target.getZ() + Math.sin(angle) * r,
                                    3, 0.3, 0.3, 0.3, 0.02);
                        }
                    }

                    target.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§5§lThe Dark Curse has transported you to Storybrooke!"));
                    target.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§8No more happy endings..."));
                }
            }));
        }

        int casterDelay = maxDelay + 10;
        server.tell(new TickTask(server.getTickCount() + casterDelay, () -> {
            if (caster.isAlive()) {
                com.example.ouat.dimensions.DimensionManager.teleportToDimension(
                        caster, com.example.ouat.dimensions.DimensionManager.STORYBROOKE);

                ServerLevel storyLevel = caster.server.getLevel(
                        com.example.ouat.dimensions.DimensionManager.STORYBROOKE);
                if (storyLevel != null) {
                    for (int i = 0; i < 100; i++) {
                        double angle = caster.level().random.nextDouble() * Math.PI * 2;
                        double r = caster.level().random.nextDouble() * 4.0;
                        storyLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE,
                                caster.getX() + Math.cos(angle) * r,
                                caster.getY() + caster.level().random.nextDouble() * 3.0,
                                caster.getZ() + Math.sin(angle) * r,
                                3, 0.3, 0.3, 0.3, 0.02);
                    }
                }

                caster.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§5§lThe Dark Curse is complete. You are transported to Storybrooke."));
                caster.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§8No more happy endings..."));
            }
        }));
    }

    public static void startClient(double x, double y, double z, int targetCount) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        new DarkSmokeTimeline(level, x, y, z, targetCount);
    }

    private static class DarkSmokeTimeline {
        private final ClientLevel level;
        private final double casterX;
        private final double casterY;
        private final double casterZ;
        private final Random rng;
        private int tick = 0;
        private final SpriteSet sprites;

        DarkSmokeTimeline(ClientLevel level, double x, double y, double z, int targetCount) {
            this.level = level;
            this.casterX = x;
            this.casterY = y;
            this.casterZ = z;
            this.rng = new Random();
            this.sprites = PurpleSmokeParticle.getSprites();
            scheduleNext();
        }

        private void scheduleNext() {
            MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
            if (server == null) return;
            server.tell(new TickTask(server.getTickCount() + 1, this::doTick));
        }

        private void doTick() {
            spawnPhase();
            tick++;
            if (tick < TOTAL_TICKS) {
                scheduleNext();
            }
        }

        private void spawnPhase() {
            if (tick < WAVE_START_TICK) {
                spawnBuildup();
            } else if (tick < WAVE_START_TICK + 5) {
                spawnEruption();
            } else if (tick < 65) {
                spawnWave();
            } else {
                spawnDissipation();
            }
        }

        private void spawnBuildup() {
            float intensity = (float) tick / WAVE_START_TICK;
            int count = (int) (8 + intensity * 25);

            for (int i = 0; i < count; i++) {
                float angle = rng.nextFloat() * (float) Math.PI * 2;
                float radius = 0.5F + rng.nextFloat() * (1.5F + intensity * 2.0F);
                float height = (float) casterY + 0.5F + rng.nextFloat() * (1.5F + intensity * 2.0F);
                float[] color = pickGray(0, 3);

                spawnSmokeParticle(
                        0.8F + rng.nextFloat() * 1.2F + intensity * 1.5F,
                        20 + rng.nextInt(15),
                        color,
                        angle, radius, 0.15F + rng.nextFloat() * 0.2F,
                        0.08F + rng.nextFloat() * 0.12F,
                        0.15F + intensity * 0.2F,
                        casterX, height, casterZ,
                        0.3F, 0.6F
                );
            }

            if (tick % 2 == 0) {
                for (int i = 0; i < 4; i++) {
                    float angle = rng.nextFloat() * (float) Math.PI * 2;
                    float radius = 0.3F + rng.nextFloat() * 0.8F;
                    float[] color = {0.05F, 0.05F, 0.05F};
                    spawnSmokeParticle(
                            1.5F + rng.nextFloat() * 1.0F,
                            15 + rng.nextInt(10),
                            color,
                            angle, radius, 0.3F,
                            0.05F,
                            0.1F,
                            casterX, casterY + 1.0F + rng.nextFloat(), casterZ,
                            0.2F, 0.5F
                    );
                }
            }
        }

        private void spawnEruption() {
            int tickInEruption = tick - WAVE_START_TICK;
            float explosionProgress = tickInEruption / 5.0F;

            for (int i = 0; i < 40; i++) {
                float angle = rng.nextFloat() * (float) Math.PI * 2;
                float radius = explosionProgress * (2.0F + rng.nextFloat() * 6.0F);
                float height = (float) casterY + 0.5F + explosionProgress * (rng.nextFloat() * 5.0F - 1.0F);
                float[] color = pickGray(0, 4);

                spawnSmokeParticle(
                        2.0F + rng.nextFloat() * 3.0F,
                        25 + rng.nextInt(15),
                        color,
                        angle, radius, 0.05F,
                        0.02F,
                        0.3F + rng.nextFloat() * 0.3F,
                        casterX, height, casterZ,
                        0.2F, 0.5F
                );
            }

            if (tickInEruption == 0) {
                for (int i = 0; i < 60; i++) {
                    float angle = rng.nextFloat() * (float) Math.PI * 2;
                    float radius = rng.nextFloat() * 3.0F;
                    float height = (float) casterY + rng.nextFloat() * 3.0F;
                    float[] color = {0.15F, 0.15F, 0.15F};
                    spawnSmokeParticle(
                            3.0F + rng.nextFloat() * 4.0F,
                            30 + rng.nextInt(20),
                            color,
                            angle, radius, 0.02F,
                            0.01F,
                            0.4F,
                            casterX, height, casterZ,
                            0.15F, 0.4F
                    );
                }
            }
        }

        private void spawnWave() {
            int waveTick = tick - WAVE_START_TICK - 5;
            float radius = (float) (waveTick * WAVE_SPEED);

            int ringCount = Math.min(60, 20 + (int) (radius / 200));
            float ringThickness = 8.0F + radius * 0.01F;

            for (int i = 0; i < ringCount; i++) {
                float angle = rng.nextFloat() * (float) Math.PI * 2;
                float r = radius + (rng.nextFloat() - 0.5F) * ringThickness;
                float height = (float) casterY + rng.nextFloat() * 5.0F - 1.0F;
                float[] color = pickGray(1, 5);

                spawnSmokeParticle(
                        2.0F + rng.nextFloat() * 3.0F + Math.min(radius * 0.002F, 3.0F),
                        30 + rng.nextInt(20),
                        color,
                        angle, r, 0.01F,
                        0.0F,
                        0.2F + rng.nextFloat() * 0.3F,
                        casterX, height, casterZ,
                        0.3F, 0.7F
                );
            }

            float trailRadius = radius - ringThickness * 2;
            if (trailRadius > 0) {
                for (int i = 0; i < 20; i++) {
                    float angle = rng.nextFloat() * (float) Math.PI * 2;
                    float r = trailRadius + rng.nextFloat() * ringThickness;
                    float height = (float) casterY + rng.nextFloat() * 4.0F;
                    float[] color = pickGray(2, 5);
                    spawnSmokeParticle(
                            3.0F + rng.nextFloat() * 2.0F,
                            20 + rng.nextInt(10),
                            color,
                            angle, r, 0.01F,
                            0.0F,
                            0.1F,
                            casterX, height, casterZ,
                            0.4F, 0.8F
                    );
                }
            }
        }

        private void spawnDissipation() {
            for (int i = 0; i < 8; i++) {
                float angle = rng.nextFloat() * (float) Math.PI * 2;
                float radius = 5.0F + rng.nextFloat() * 20.0F;
                float height = (float) casterY + rng.nextFloat() * 4.0F;
                float[] color = pickGray(3, 5);
                spawnSmokeParticle(
                        1.5F + rng.nextFloat() * 2.0F,
                        15 + rng.nextInt(8),
                        color,
                        angle, radius, 0.05F,
                        0.1F,
                        0.1F,
                        casterX, height, casterZ,
                        0.3F, 0.6F
                );
            }
        }

        private void spawnSmokeParticle(float scale, int lifetime, float[] color,
                                        float angle, float radius, float orbitalSpeed,
                                        float verticalBobAmount, float turbulence,
                                        double cx, double cy, double cz,
                                        float growthPeak, float holdEnd) {
            if (sprites == null) return;

            double px = cx + Math.cos(angle) * radius;
            double pz = cz + Math.sin(angle) * radius;

            PurpleSmokeParticle particle = new PurpleSmokeParticle(
                    level,
                    px, cy, pz,
                    scale, lifetime, color,
                    angle, radius, orbitalSpeed,
                    0.1F + rng.nextFloat() * 0.1F, verticalBobAmount,
                    turbulence, rng.nextFloat() * 100.0F,
                    cx, cy, cz,
                    growthPeak, holdEnd,
                    sprites
            );

            Minecraft.getInstance().particleEngine.add(particle);
        }

        private float[] pickGray(int minIdx, int maxIdx) {
            int idx = minIdx + rng.nextInt(maxIdx - minIdx);
            idx = Math.min(idx, GRAY_PALETTE.length - 1);
            float[] base = GRAY_PALETTE[idx];
            float variation = (rng.nextFloat() - 0.5F) * 0.04F;
            return new float[]{
                    Math.min(1.0F, Math.max(0.0F, base[0] + variation)),
                    Math.min(1.0F, Math.max(0.0F, base[1] + variation)),
                    Math.min(1.0F, Math.max(0.0F, base[2] + variation))
            };
        }
    }
}
