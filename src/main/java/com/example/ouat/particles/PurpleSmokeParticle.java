package com.example.ouat.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class PurpleSmokeParticle extends TextureSheetParticle {
    private static SpriteSet staticSprites;

    private final float baseScale;
    private final float maxScale;
    private final int lifetime;
    private final float orbitalAngle;
    private final float orbitalRadius;
    private final float orbitalSpeed;
    private final float verticalBobSpeed;
    private final float verticalBobAmount;
    private final float noiseOffsetX;
    private final float noiseOffsetY;
    private final float noiseOffsetZ;
    private final float turbulence;
    private final double centerX;
    private final double centerY;
    private final double centerZ;
    private final float growthPeak;
    private final float holdEnd;
    private final float startR;
    private final float startG;
    private final float startB;

    public PurpleSmokeParticle(ClientLevel level, double x, double y, double z,
                               float scale, int lifetime, float[] color,
                               float orbitalAngle, float orbitalRadius, float orbitalSpeed,
                               float verticalBobSpeed, float verticalBobAmount,
                               float turbulence, float noiseSeed,
                               double centerX, double centerY, double centerZ,
                               float growthPeak, float holdEnd,
                               SpriteSet sprites) {
        super(level, x, y, z, 0, 0, 0);
        if (sprites != null) {
            this.setSpriteFromAge(sprites);
        }

        this.baseScale = scale;
        this.maxScale = scale * 1.5F;
        this.lifetime = lifetime;
        this.age = 0;

        this.startR = color[0];
        this.startG = color[1];
        this.startB = color[2];
        this.rCol = color[0];
        this.gCol = color[1];
        this.bCol = color[2];

        this.orbitalAngle = orbitalAngle;
        this.orbitalRadius = orbitalRadius;
        this.orbitalSpeed = orbitalSpeed;
        this.verticalBobSpeed = verticalBobSpeed;
        this.verticalBobAmount = verticalBobAmount;
        this.turbulence = turbulence;
        this.noiseOffsetX = noiseSeed * 13.37F;
        this.noiseOffsetY = noiseSeed * 7.91F;
        this.noiseOffsetZ = noiseSeed * 23.14F;

        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;

        this.growthPeak = growthPeak;
        this.holdEnd = holdEnd;

        this.gravity = 0;
        this.hasPhysics = false;
    }

    public static void setSprites(SpriteSet sprites) {
        staticSprites = sprites;
    }

    public static SpriteSet getSprites() {
        return staticSprites;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        float t = (float) this.age / this.lifetime;

        float angle = this.orbitalAngle + this.age * this.orbitalSpeed;
        float noiseX = (float) (Math.sin(this.age * 0.1 + this.noiseOffsetX) * this.turbulence);
        float noiseY = (float) (Math.sin(this.age * 0.13 + this.noiseOffsetY) * this.turbulence * 0.4);
        float noiseZ = (float) (Math.cos(this.age * 0.11 + this.noiseOffsetZ) * this.turbulence);

        this.x = this.centerX + Math.cos(angle) * this.orbitalRadius + noiseX;
        this.z = this.centerZ + Math.sin(angle) * this.orbitalRadius + noiseZ;
        this.y = this.centerY + Math.sin(this.age * this.verticalBobSpeed) * this.verticalBobAmount + noiseY;

        this.quadSize = getScale(t);
        this.alpha = getAlpha(t);

        float colorPulse = (float) (Math.sin(this.age * 0.08 + this.noiseOffsetX) * 0.05);
        this.rCol = clamp(this.startR + colorPulse);
        this.gCol = clamp(this.startG + colorPulse * 0.5F);
        this.bCol = clamp(this.startB + colorPulse * 0.3F);
    }

    private float getScale(float t) {
        if (t < 0.15F) {
            return this.baseScale * (t / 0.15F);
        } else if (t < this.growthPeak) {
            float progress = (t - 0.15F) / (this.growthPeak - 0.15F);
            return this.baseScale + (this.maxScale - this.baseScale) * progress;
        } else if (t < this.holdEnd) {
            return this.maxScale;
        } else {
            float progress = (t - this.holdEnd) / (1.0F - this.holdEnd);
            return this.maxScale * (1.0F - progress * progress);
        }
    }

    private float getAlpha(float t) {
        if (t < 0.08F) {
            return t / 0.08F;
        } else if (t > 0.75F) {
            return Math.max(0.0F, (1.0F - t) / 0.25F);
        }
        return 1.0F;
    }

    private float clamp(float v) {
        return Math.min(1.0F, Math.max(0.0F, v));
    }

    @Override
    public float getQuadSize(float partialTick) {
        float t = Math.min(1.0F, (this.age + partialTick) / this.lifetime);
        return getScale(t);
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
            staticSprites = sprites;
        }

        @Override
        public PurpleSmokeParticle createParticle(SimpleParticleType type,
                                                   ClientLevel level, double x, double y, double z,
                                                   double xd, double yd, double zd) {
            return new PurpleSmokeParticle(level, x, y, z,
                    1.0F, 30, new float[]{0.5F, 0.1F, 0.8F},
                    0, 1.0F, 0.1F,
                    0.1F, 0.3F,
                    0.2F, 0.0F,
                    x, y, z,
                    0.5F, 0.7F,
                    this.sprites);
        }
    }
}
