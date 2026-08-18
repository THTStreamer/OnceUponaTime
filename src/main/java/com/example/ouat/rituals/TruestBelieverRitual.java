package com.example.ouat.rituals;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.UniqueRoleRegistry;
import com.example.ouat.roles.TruestBelieverRole;
import com.example.ouat.ritual.Ritual;
import com.example.ouat.ritual.RitualIngredient;
import com.example.ouat.ritual.Ritual.StructurePattern;
import com.example.ouat.ritual.Ritual.BlockRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class TruestBelieverRitual extends Ritual {
    public static final ResourceLocation RITUAL_ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "truest_believer_awakening");

    public TruestBelieverRitual() {
        super(RITUAL_ID, "Truest Believer Awakening", createIngredients(), createStructurePatterns(), 144000, 50);
    }

    private static List<RitualIngredient> createIngredients() {
        List<RitualIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RitualIngredient("tear_of_true_love", "light", 1, true));
        ingredients.add(new RitualIngredient("essence_of_belief", "light", 3, true));
        ingredients.add(new RitualIngredient("heart_of_innocence", "light", 1, true));
        return ingredients;
    }

    private static List<StructurePattern> createStructurePatterns() {
        List<StructurePattern> patterns = new ArrayList<>();

        List<BlockRequirement> blocks = new ArrayList<>();
        blocks.add(new BlockRequirement(new BlockPos(0, 0, 0), state -> state.is(Blocks.PURPUR_BLOCK)));
        blocks.add(new BlockRequirement(new BlockPos(0, 1, 0), state -> state.is(Blocks.PURPUR_BLOCK)));
        blocks.add(new BlockRequirement(new BlockPos(0, 2, 0), state -> state.is(Blocks.PURPUR_BLOCK)));
        blocks.add(new BlockRequirement(new BlockPos(0, 3, 0), state -> state.is(Blocks.PURPUR_BLOCK)));
        blocks.add(new BlockRequirement(new BlockPos(0, 4, 0), state -> state.is(Blocks.PURPUR_BLOCK)));
        blocks.add(new BlockRequirement(new BlockPos(0, 5, 0), state -> state.is(Blocks.DIAMOND_BLOCK)));
        blocks.add(new BlockRequirement(new BlockPos(-1, 0, 0), state -> state.is(Blocks.END_ROD)));
        blocks.add(new BlockRequirement(new BlockPos(1, 0, 0), state -> state.is(Blocks.END_ROD)));
        blocks.add(new BlockRequirement(new BlockPos(0, 0, -1), state -> state.is(Blocks.END_ROD)));
        blocks.add(new BlockRequirement(new BlockPos(0, 0, 1), state -> state.is(Blocks.END_ROD)));

        patterns.add(new StructurePattern(blocks, new BlockPos(0, -1, 0)));

        return patterns;
    }

    @Override
    protected void onRitualStart(Player player, Level level, BlockPos center) {
        level.playSound(null, center, SoundEvents.ENDER_DRAGON_FLAP, SoundSource.AMBIENT, 0.5F, 1.5F);

        for (int i = 0; i < 80; i++) {
            double x = center.getX() + (level.random.nextDouble() - 0.5) * 8;
            double y = center.getY() + level.random.nextDouble() * 6;
            double z = center.getZ() + (level.random.nextDouble() - 0.5) * 8;
            level.addParticle(net.minecraft.core.particles.ParticleTypes.DRAGON_BREATH, x, y, z, 0, 0.05, 0);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§d§lReality bends... The Truest Believer begins to awaken!"));
        }
    }

    @Override
    protected void executeRitualEffects(Player player, Level level, BlockPos center) {
        for (int tick = 0; tick < 300; tick++) {
            final int currentTick = tick;
            level.getServer().tell(new net.minecraft.server.TickTask(
                    level.getServer().getTickCount() + currentTick,
                    () -> {
                        if (currentTick % 15 == 0) {
                            double x = center.getX() + (level.random.nextDouble() - 0.5) * 5;
                            double y = center.getY() + 2 + level.random.nextDouble() * 5;
                            double z = center.getZ() + (level.random.nextDouble() - 0.5) * 5;
                            level.addParticle(net.minecraft.core.particles.ParticleTypes.END_ROD, x, y, z, 0, 0.05, 0);
                            level.addParticle(net.minecraft.core.particles.ParticleTypes.PORTAL, x, y, z, 0, 0, 0);
                        }

                        if (currentTick == 0) {
                            level.playSound(null, center, SoundEvents.ENDER_EYE_DEATH, SoundSource.AMBIENT, 1.0F, 1.0F);
                        }

                        if (currentTick == 150) {
                            level.playSound(null, center, SoundEvents.END_PORTAL_SPAWN, SoundSource.AMBIENT, 1.5F, 1.2F);
                        }

                        if (currentTick == 299) {
                            for (int i = 0; i < 120; i++) {
                                double x = center.getX() + (level.random.nextDouble() - 0.5) * 10;
                                double y = center.getY() + level.random.nextDouble() * 8;
                                double z = center.getZ() + (level.random.nextDouble() - 0.5) * 10;
                                level.addParticle(net.minecraft.core.particles.ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
                            }
                            level.playSound(null, center, SoundEvents.TOTEM_USE, SoundSource.AMBIENT, 2.0F, 1.5F);
                        }
                    }
            ));
        }
    }

    @Override
    protected void onRitualComplete(Player player, Level level, BlockPos center) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        UniqueRoleRegistry registry = UniqueRoleRegistry.get(level.getServer());

        if (!registry.isRoleVacant(UniqueRoleRegistry.RoleType.TRUEST_BELIEVER)) {
            serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c§lThe Truest Believer already exists! The ritual fails!"));
            return;
        }

        registry.claimRole(serverPlayer.getUUID(), UniqueRoleRegistry.RoleType.TRUEST_BELIEVER);
        TruestBelieverRole.grantRole(serverPlayer);

        serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§d§lYou have become the Truest Believer!"));
        serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§d§lYour belief can defy reality itself."));

        for (ServerPlayer otherPlayer : level.getServer().getPlayerList().getPlayers()) {
            if (otherPlayer != serverPlayer && otherPlayer.distanceTo(serverPlayer) < 100) {
                otherPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§d§lReality shimmers... " + serverPlayer.getName().getString() + " has become the Truest Believer!"));
            }
        }

        level.playSound(null, center, SoundEvents.TOTEM_USE, SoundSource.AMBIENT, 2.0F, 1.5F);
    }
}
