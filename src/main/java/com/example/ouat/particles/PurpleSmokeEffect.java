package com.example.ouat.particles;

import com.example.ouat.registry.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Random;

public class PurpleSmokeEffect {

    private static final float[][] PALETTE = {
            {0.129F, 0.0F, 0.239F},
            {0.231F, 0.031F, 0.439F},
            {0.420F, 0.098F, 0.710F},
            {0.553F, 0.239F, 0.878F},
            {0.651F, 0.357F, 1.0F},
            {0.8F, 0.2F, 0.6F},
            {0.3F, 0.1F, 0.9F},
            {0.9F, 0.3F, 0.7F}
    };

    private static final int TOTAL_TICKS = 32;

    public static void startServer(ServerLevel level, ServerPlayer player) {
        for (ServerPlayer viewer : level.getServer().getPlayerList().getPlayers()) {
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(viewer,
                    new com.example.ouat.network.PurpleSmokeSpawnPacket(
                            player.getX(), player.getY(), player.getZ()));
        }
    }

    public static void startClient(double x, double y, double z) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        new SmokeTimeline(level, x, y, z);
    }

    private static class SmokeTimeline {
        private final ClientLevel level;
        private final double centerX;
        private final double centerY;
        private final double centerZ;
        private final Random rng;
        private int tick = 0;
        private final float[] primaryColor;
        private final float[] secondaryColor;
        private final SpriteSet sprites;

        SmokeTimeline(ClientLevel level, double x, double y, double z) {
            this.level = level;
            this.centerX = x;
            this.centerY = y;
            this.centerZ = z;
            this.rng = new Random();
            this.primaryColor = PALETTE[rng.nextInt(PALETTE.length)];
            this.secondaryColor = PALETTE[rng.nextInt(PALETTE.length)];
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
            if (tick < 4) {
                spawnWisps();
            } else if (tick < 8) {
                spawnFeetSmoke();
            } else if (tick < 13) {
                spawnExpandingSmoke();
            } else if (tick < 18) {
                spawnTorsoClouds();
            } else if (tick < 23) {
                spawnDenseCore();
            } else if (tick < 27) {
                spawnMaxDensity();
            } else {
                spawnDispersal();
            }
        }

        private void spawnWisps() {
            for (int i = 0; i < 8; i++) {
                spawnOne(
                        0.2F + rng.nextFloat() * 0.5F,
                        15 + rng.nextInt(10),
                        pickColor(),
                        0.3F, 0.2F,
                        0.8F + rng.nextFloat() * 0.5F,
                        0.1F,
                        0.05F,
                        0.0F, 0.3F,
                        0.3F, 0.6F
                );
            }
        }

        private void spawnFeetSmoke() {
            for (int i = 0; i < 12; i++) {
                spawnOne(
                        0.6F + rng.nextFloat() * 0.8F,
                        20 + rng.nextInt(15),
                        blendColor(0.4F),
                        0.5F, 0.4F,
                        0.5F + rng.nextFloat() * 0.8F,
                        0.2F,
                        0.15F,
                        0.0F, 0.8F,
                        0.2F, 0.5F
                );
            }
        }

        private void spawnExpandingSmoke() {
            for (int i = 0; i < 15; i++) {
                spawnOne(
                        1.0F + rng.nextFloat() * 1.5F,
                        25 + rng.nextInt(15),
                        blendColor(0.5F),
                        0.8F + rng.nextFloat() * 1.2F, 0.3F,
                        0.4F + rng.nextFloat() * 0.6F,
                        0.15F,
                        0.2F,
                        0.0F, 1.2F,
                        0.2F, 0.55F
                );
            }
        }

        private void spawnTorsoClouds() {
            for (int i = 0; i < 12; i++) {
                float yOff = 0.8F + rng.nextFloat() * 1.0F;
                spawnOne(
                        2.0F + rng.nextFloat() * 2.0F,
                        30 + rng.nextInt(20),
                        blendColor(0.6F),
                        1.0F + rng.nextFloat() * 1.5F, 0.2F,
                        0.3F + rng.nextFloat() * 0.5F,
                        0.1F,
                        0.3F,
                        0.0F, yOff,
                        0.25F, 0.6F
                );
            }
        }

        private void spawnDenseCore() {
            for (int i = 0; i < 18; i++) {
                float yOff = 0.5F + rng.nextFloat() * 1.5F;
                spawnOne(
                        1.5F + rng.nextFloat() * 2.5F,
                        25 + rng.nextInt(15),
                        blendColor(0.7F),
                        0.5F + rng.nextFloat() * 1.0F, 0.15F,
                        0.5F + rng.nextFloat() * 0.8F,
                        0.05F,
                        0.25F,
                        0.0F, yOff,
                        0.3F, 0.65F
                );
            }
            for (int i = 0; i < 6; i++) {
                spawnOne(
                        3.0F + rng.nextFloat() * 2.0F,
                        20 + rng.nextInt(10),
                        new float[]{0.129F, 0.0F, 0.239F},
                        0.3F, 0.05F,
                        0.7F + rng.nextFloat() * 0.8F,
                        0.03F,
                        0.4F,
                        0.0F, 1.0F + rng.nextFloat() * 0.5F,
                        0.4F, 0.75F
                );
            }
        }

        private void spawnMaxDensity() {
            for (int i = 0; i < 15; i++) {
                float yOff = 0.3F + rng.nextFloat() * 2.0F;
                spawnOne(
                        2.5F + rng.nextFloat() * 2.5F,
                        30 + rng.nextInt(20),
                        blendColor(0.8F),
                        0.8F + rng.nextFloat() * 1.2F, 0.1F,
                        0.6F + rng.nextFloat() * 1.0F,
                        0.08F,
                        0.35F,
                        0.0F, yOff,
                        0.35F, 0.7F
                );
            }
        }

        private void spawnDispersal() {
            for (int i = 0; i < 10; i++) {
                spawnOne(
                        1.0F + rng.nextFloat() * 2.0F,
                        15 + rng.nextInt(10),
                        blendColor(0.5F),
                        0.4F + rng.nextFloat() * 0.8F, 0.1F,
                        0.6F + rng.nextFloat() * 1.2F,
                        0.2F,
                        0.15F,
                        0.0F, 0.5F + rng.nextFloat() * 1.5F,
                        0.2F, 0.5F
                );
            }
        }

        private void spawnOne(float scale, int lifetime, float[] color,
                              float orbitalRadius, float orbitalSpeed,
                              float verticalBobSpeed, float verticalBobAmount,
                              float turbulence,
                              float yMin, float yMax,
                              float growthPeak, float holdEnd) {
            if (sprites == null) return;

            float angle = rng.nextFloat() * (float) Math.PI * 2;
            float y = (float) centerY + yMin + rng.nextFloat() * (yMax - yMin);
            float noiseSeed = rng.nextFloat() * 100.0F;

            PurpleSmokeParticle particle = new PurpleSmokeParticle(
                    level,
                    centerX, y, centerZ,
                    scale, lifetime, color,
                    angle, orbitalRadius, orbitalSpeed,
                    verticalBobSpeed, verticalBobAmount,
                    turbulence, noiseSeed,
                    centerX, y, centerZ,
                    growthPeak, holdEnd,
                    sprites
            );

            Minecraft.getInstance().particleEngine.add(particle);
        }

        private float[] blendColor(float blend) {
            return new float[]{
                    primaryColor[0] + (secondaryColor[0] - primaryColor[0]) * blend,
                    primaryColor[1] + (secondaryColor[1] - primaryColor[1]) * blend,
                    primaryColor[2] + (secondaryColor[2] - primaryColor[2]) * blend
            };
        }

        private float[] pickColor() {
            return rng.nextBoolean() ? primaryColor : secondaryColor;
        }
    }
}
