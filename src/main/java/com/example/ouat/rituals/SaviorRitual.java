package com.example.ouat.rituals;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.data.UniqueRoleRegistry;
import com.example.ouat.registry.ModItems;
import com.example.ouat.roles.SaviorRole;
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

public class SaviorRitual extends Ritual {
    public static final ResourceLocation RITUAL_ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "savior_awakening");

    public SaviorRitual() {
        super(RITUAL_ID, "Savior Awakening", createIngredients(), createStructurePatterns(), 72000, 25,
                PlayerSupernaturalData.MagicalAlignment.LIGHT);
    }

    private static List<RitualIngredient> createIngredients() {
        List<RitualIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RitualIngredient("shard_of_light", "light", 3, true));
        ingredients.add(new RitualIngredient("essence_of_hope", "light", 1, true));
        ingredients.add(new RitualIngredient("crystal_of_purity", "light", 1, true));
        return ingredients;
    }

    private static List<StructurePattern> createStructurePatterns() {
        List<StructurePattern> patterns = new ArrayList<>();

        List<BlockRequirement> blocks = new ArrayList<>();
        blocks.add(new BlockRequirement(new BlockPos(0, 0, 0), state -> state.is(Blocks.GOLD_BLOCK)));
        blocks.add(new BlockRequirement(new BlockPos(0, 1, 0), state -> state.is(Blocks.QUARTZ_BLOCK)));
        blocks.add(new BlockRequirement(new BlockPos(0, 2, 0), state -> state.is(Blocks.QUARTZ_BLOCK)));
        blocks.add(new BlockRequirement(new BlockPos(0, 3, 0), state -> state.is(Blocks.QUARTZ_BLOCK)));
        blocks.add(new BlockRequirement(new BlockPos(0, 4, 0), state -> state.is(Blocks.DIAMOND_BLOCK)));
        blocks.add(new BlockRequirement(new BlockPos(-1, 0, 0), state -> state.is(Blocks.TORCH) || state.is(Blocks.WALL_TORCH)));
        blocks.add(new BlockRequirement(new BlockPos(1, 0, 0), state -> state.is(Blocks.TORCH) || state.is(Blocks.WALL_TORCH)));
        blocks.add(new BlockRequirement(new BlockPos(0, 0, -1), state -> state.is(Blocks.TORCH) || state.is(Blocks.WALL_TORCH)));
        blocks.add(new BlockRequirement(new BlockPos(0, 0, 1), state -> state.is(Blocks.TORCH) || state.is(Blocks.WALL_TORCH)));

        patterns.add(new StructurePattern(blocks, new BlockPos(0, 0, 0)));

        return patterns;
    }

    @Override
    protected void onRitualStart(Player player, Level level, BlockPos center) {
        level.playSound(null, center, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.AMBIENT, 1.0F, 1.5F);

        for (int i = 0; i < 50; i++) {
            double x = center.getX() + (level.random.nextDouble() - 0.5) * 6;
            double y = center.getY() + level.random.nextDouble() * 5;
            double z = center.getZ() + (level.random.nextDouble() - 0.5) * 6;
            level.addParticle(net.minecraft.core.particles.ParticleTypes.ENCHANT, x, y, z, 0, 0.1, 0);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§e§lThe light begins to gather... The Savior awakens!"));
        }
    }

    @Override
    protected void executeRitualEffects(Player player, Level level, BlockPos center) {
        for (int tick = 0; tick < 200; tick++) {
            final int currentTick = tick;
            level.getServer().tell(new net.minecraft.server.TickTask(
                    level.getServer().getTickCount() + currentTick,
                    () -> {
                        if (currentTick % 10 == 0) {
                            double x = center.getX() + (level.random.nextDouble() - 0.5) * 4;
                            double y = center.getY() + 2 + level.random.nextDouble() * 4;
                            double z = center.getZ() + (level.random.nextDouble() - 0.5) * 4;
                            level.addParticle(net.minecraft.core.particles.ParticleTypes.END_ROD, x, y, z, 0, 0.05, 0);
                            level.addParticle(net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING, x, y, z, 0, 0, 0);
                        }

                        if (currentTick == 0) {
                            level.playSound(null, center, SoundEvents.TOTEM_USE, SoundSource.AMBIENT, 1.0F, 1.0F);
                        }

                        if (currentTick == 100) {
                            level.playSound(null, center, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.AMBIENT, 1.5F, 1.2F);
                        }

                        if (currentTick == 199) {
                            for (int i = 0; i < 100; i++) {
                                double x = center.getX() + (level.random.nextDouble() - 0.5) * 8;
                                double y = center.getY() + level.random.nextDouble() * 6;
                                double z = center.getZ() + (level.random.nextDouble() - 0.5) * 8;
                                level.addParticle(net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING, x, y, z, 0, 0, 0);
                            }
                            level.playSound(null, center, SoundEvents.TOTEM_USE, SoundSource.AMBIENT, 2.0F, 1.0F);
                        }
                    }
            ));
        }
    }

    @Override
    protected void onRitualComplete(Player player, Level level, BlockPos center) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        UniqueRoleRegistry registry = UniqueRoleRegistry.get(level.getServer());

        if (!registry.isRoleVacant(UniqueRoleRegistry.RoleType.SAVIOR)) {
            serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c§lThe Savior already exists! The ritual fails!"));
            return;
        }

        registry.claimRole(serverPlayer.getUUID(), UniqueRoleRegistry.RoleType.SAVIOR);
        SaviorRole.grantRole(serverPlayer);

        serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§e§lYou have become the Savior!"));
        serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§e§lThe light of hope now shines through you."));

        for (ServerPlayer otherPlayer : level.getServer().getPlayerList().getPlayers()) {
            if (otherPlayer != serverPlayer && otherPlayer.distanceTo(serverPlayer) < 100) {
                otherPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§e§lA brilliant light has appeared... " + serverPlayer.getName().getString() + " has become the Savior!"));
            }
        }

        level.playSound(null, center, SoundEvents.TOTEM_USE, SoundSource.AMBIENT, 2.0F, 1.0F);
    }
}
