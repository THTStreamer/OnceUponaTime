package com.example.ouat.rituals;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.UniqueRoleRegistry;
import com.example.ouat.roles.AuthorRole;
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

public class AuthorRitual extends Ritual {
    public static final ResourceLocation RITUAL_ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "author_awakening");

    public AuthorRitual() {
        super(RITUAL_ID, "Author Awakening", createIngredients(), createStructurePatterns(), 216000, 75);
    }

    private static List<RitualIngredient> createIngredients() {
        List<RitualIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RitualIngredient("ink_of_creation", "dark", 1, true));
        ingredients.add(new RitualIngredient("quill_of_fate", "dark", 1, true));
        ingredients.add(new RitualIngredient("page_of_destiny", "dark", 3, true));
        return ingredients;
    }

    private static List<StructurePattern> createStructurePatterns() {
        List<StructurePattern> patterns = new ArrayList<>();

        List<BlockRequirement> blocks = new ArrayList<>();
        blocks.add(new BlockRequirement(new BlockPos(0, 0, 0), state -> state.is(Blocks.BOOKSHELF)));
        blocks.add(new BlockRequirement(new BlockPos(0, 1, 0), state -> state.is(Blocks.BOOKSHELF)));
        blocks.add(new BlockRequirement(new BlockPos(0, 2, 0), state -> state.is(Blocks.BOOKSHELF)));
        blocks.add(new BlockRequirement(new BlockPos(0, 3, 0), state -> state.is(Blocks.BOOKSHELF)));
        blocks.add(new BlockRequirement(new BlockPos(0, 4, 0), state -> state.is(Blocks.BOOKSHELF)));
        blocks.add(new BlockRequirement(new BlockPos(0, 5, 0), state -> state.is(Blocks.BOOKSHELF)));
        blocks.add(new BlockRequirement(new BlockPos(0, 6, 0), state -> state.is(Blocks.DIAMOND_BLOCK)));
        blocks.add(new BlockRequirement(new BlockPos(-1, 0, 0), state -> state.is(Blocks.ENCHANTING_TABLE)));
        blocks.add(new BlockRequirement(new BlockPos(1, 0, 0), state -> state.is(Blocks.ENCHANTING_TABLE)));
        blocks.add(new BlockRequirement(new BlockPos(0, 0, -1), state -> state.is(Blocks.ENCHANTING_TABLE)));
        blocks.add(new BlockRequirement(new BlockPos(0, 0, 1), state -> state.is(Blocks.ENCHANTING_TABLE)));

        patterns.add(new StructurePattern(blocks, new BlockPos(0, 0, 0)));

        return patterns;
    }

    @Override
    protected void onRitualStart(Player player, Level level, BlockPos center) {
        level.playSound(null, center, SoundEvents.BOOK_PAGE_TURN, SoundSource.AMBIENT, 1.0F, 0.5F);

        for (int i = 0; i < 60; i++) {
            double x = center.getX() + (level.random.nextDouble() - 0.5) * 6;
            double y = center.getY() + level.random.nextDouble() * 7;
            double z = center.getZ() + (level.random.nextDouble() - 0.5) * 6;
            level.addParticle(net.minecraft.core.particles.ParticleTypes.ENCHANT, x, y, z, 0, 0.1, 0);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§5§lThe story begins to write itself... The Author awakens!"));
        }
    }

    @Override
    protected void executeRitualEffects(Player player, Level level, BlockPos center) {
        for (int tick = 0; tick < 400; tick++) {
            final int currentTick = tick;
            level.getServer().tell(new net.minecraft.server.TickTask(
                    level.getServer().getTickCount() + currentTick,
                    () -> {
                        if (currentTick % 20 == 0) {
                            double x = center.getX() + (level.random.nextDouble() - 0.5) * 5;
                            double y = center.getY() + 2 + level.random.nextDouble() * 6;
                            double z = center.getZ() + (level.random.nextDouble() - 0.5) * 5;
                            level.addParticle(net.minecraft.core.particles.ParticleTypes.ENCHANT, x, y, z, 0, 0.05, 0);
                            level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
                        }

                        if (currentTick == 0) {
                            level.playSound(null, center, SoundEvents.BOOK_PAGE_TURN, SoundSource.AMBIENT, 1.5F, 0.5F);
                        }

                        if (currentTick == 200) {
                            level.playSound(null, center, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.AMBIENT, 1.5F, 1.0F);
                        }

                        if (currentTick == 399) {
                            for (int i = 0; i < 150; i++) {
                                double x = center.getX() + (level.random.nextDouble() - 0.5) * 10;
                                double y = center.getY() + level.random.nextDouble() * 8;
                                double z = center.getZ() + (level.random.nextDouble() - 0.5) * 10;
                                level.addParticle(net.minecraft.core.particles.ParticleTypes.ENCHANT, x, y, z, 0, 0, 0);
                            }
                            level.playSound(null, center, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.AMBIENT, 2.0F, 0.5F);
                        }
                    }
            ));
        }
    }

    @Override
    protected void onRitualComplete(Player player, Level level, BlockPos center) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        UniqueRoleRegistry registry = UniqueRoleRegistry.get(level.getServer());

        if (!registry.isRoleVacant(UniqueRoleRegistry.RoleType.AUTHOR)) {
            serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c§lThe Author already exists! The ritual fails!"));
            return;
        }

        registry.claimRole(serverPlayer.getUUID(), UniqueRoleRegistry.RoleType.AUTHOR);
        AuthorRole.grantRole(serverPlayer);

        serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§5§lYou have become the Author!"));
        serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§5§lThe pen is now in your hand. The story awaits."));

        for (ServerPlayer otherPlayer : level.getServer().getPlayerList().getPlayers()) {
            if (otherPlayer != serverPlayer && otherPlayer.distanceTo(serverPlayer) < 100) {
                otherPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§5§lThe pages flutter... " + serverPlayer.getName().getString() + " has become the Author!"));
            }
        }

        level.playSound(null, center, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.AMBIENT, 2.0F, 0.5F);
    }
}
