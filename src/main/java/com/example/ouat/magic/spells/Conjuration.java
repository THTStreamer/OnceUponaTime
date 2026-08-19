package com.example.ouat.magic.spells;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.magic.Spell;
import com.example.ouat.magic.Spell.MagicType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Conjuration extends Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "conjuration");

    public Conjuration() {
        super(ID, "Conjuration", 12, MagicType.NEUTRAL);
    }

    @Override
    public boolean cast(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!canCast(player)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYour magic is not strong enough."));
            return false;
        }
        if (!consumeFood(player, 6)) return false;

        ItemStack conjured;
        int roll = player.getRandom().nextInt(5);
        switch (roll) {
            case 0 -> conjured = new ItemStack(Items.GOLDEN_APPLE, 1);
            case 1 -> conjured = new ItemStack(Items.ENDER_PEARL, 2);
            case 2 -> conjured = new ItemStack(Items.EXPERIENCE_BOTTLE, 3);
            case 3 -> conjured = new ItemStack(Items.GLOWSTONE_DUST, 4);
            default -> conjured = new ItemStack(Items.QUARTZ, 4);
        }

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                    player.getX(), player.getY() + 1.5, player.getZ(),
                    30, 0.3, 0.3, 0.3, 0.1);
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    player.getX(), player.getY() + 1, player.getZ(),
                    25, 0.5, 0.5, 0.5, 0.05);
        }

        ItemEntity drop = new ItemEntity(player.level(), player.getX(), player.getY() + 1.5, player.getZ(), conjured);
        player.level().addFreshEntity(drop);

        player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.5F);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou conjure " + conjured.getDisplayName().getString() + " from thin air!"));
        return true;
    }
}
