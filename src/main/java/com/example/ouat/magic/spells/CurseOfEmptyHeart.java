package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.CurseInstance;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import com.example.ouat.magic.Spell.MagicType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class CurseOfEmptyHeart extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "curse_of_empty_heart");

    public CurseOfEmptyHeart() {
        super(ID, "Curse of the Empty Heart", 30, MagicType.DARK);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 12)) return false;

        // Cora's curse: makes target forget loved ones
        net.minecraft.world.entity.player.Player target = player.level().getNearestPlayer(player, 8.0);
        if (target == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNo target within range."));
            return false;
        }
        if (!(target instanceof ServerPlayer targetPlayer)) return false;

        // Forget: weakness + mining fatigue + confusion (forgetting how to fight)
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 800, 2));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 800, 2));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 800, 0));
        targetPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));

        PlayerSupernaturalData targetData = targetPlayer.getData(PlayerSupernaturalData.TYPE);
        // Remove some of their spells (forgetting)
        int spellsToForget = Math.min(3, targetData.getLearnedSpells().size());
        for (int i = 0; i < spellsToForget; i++) {
            if (!targetData.getLearnedSpells().isEmpty()) {
                targetData.getLearnedSpells().remove(
                        targetData.getLearnedSpells().size() - 1
                );
            }
        }

        targetData.addCurse(new CurseInstance(
                ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "empty_heart"),
                "Curse of the Empty Heart", 60000, player.getUUID()
        ));

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    targetPlayer.getX(), targetPlayer.getY() + 1, targetPlayer.getZ(),
                    40, 0.3, 0.3, 0.3, 0.05);
            serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                    targetPlayer.getX(), targetPlayer.getY() + 2, targetPlayer.getZ(),
                    20, 0.5, 0.5, 0.5, 0.1);
        }

        player.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.5F);
        targetPlayer.playSound(SoundEvents.WITHER_DEATH, 1.0F, 0.3F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou curse " + targetPlayer.getName().getString() + " to forget those they love!"));
        targetPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYour memories of loved ones begin to fade..."));
        return true;
    }
}
