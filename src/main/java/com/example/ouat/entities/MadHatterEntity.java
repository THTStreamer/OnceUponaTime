package com.example.ouat.entities;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MadHatterEntity extends Zombie {

    @SuppressWarnings("unchecked")
    private static final Holder<MobEffect>[] CHAOS_EFFECTS = new Holder[]{
            MobEffects.MOVEMENT_SPEED,
            MobEffects.JUMP,
            MobEffects.DIG_SPEED,
            MobEffects.BLINDNESS,
            MobEffects.CONFUSION,
            MobEffects.POISON,
            MobEffects.LEVITATION
    };

    public MadHatterEntity(EntityType<? extends MadHatterEntity> type, Level level) {
        super(type, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide && this.random.nextInt(4) == 0) {
            this.level().addParticle(ParticleTypes.REVERSE_PORTAL,
                    this.getRandomX(0.6), this.getRandomY() + 1.8, this.getRandomZ(0.6),
                    (this.random.nextDouble() - 0.5) * 0.1, 0.03, (this.random.nextDouble() - 0.5) * 0.1);
        }

        if (!this.level().isClientSide && this.tickCount % 80 == 0) {
            var players = this.level().getEntitiesOfClass(Player.class,
                    this.getBoundingBox().inflate(5.0), p -> true);
            for (Player player : players) {
                Holder<MobEffect> effect = CHAOS_EFFECTS[this.random.nextInt(CHAOS_EFFECTS.length)];
                int amplifier = this.random.nextInt(3);
                player.addEffect(new MobEffectInstance(effect, 80, amplifier));
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VINDICATOR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VINDICATOR_DEATH;
    }
}
