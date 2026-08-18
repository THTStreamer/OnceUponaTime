package com.example.ouat.rituals;

import com.example.ouat.OnceUponATime;
import com.example.ouat.config.ModConfig;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.data.UniqueRoleRegistry;
import com.example.ouat.registry.ModDataComponents;
import com.example.ouat.registry.ModItems;
import com.example.ouat.roles.DarkOneRole;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class DarkOneRitual extends Ritual {
    public static final ResourceLocation RITUAL_ID = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "dark_one_ascension");

    private static final int ASCENSION_DURATION_TICKS = 200;
    private static final int COMPAULSION_COOLDOWN = 1200;

    public DarkOneRitual() {
        super(RITUAL_ID, "Dark One Ascension", createIngredients(), createStructurePatterns(), 72000, 0);
    }

    private static List<RitualIngredient> createIngredients() {
        List<RitualIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RitualIngredient("shard_of_dark_power", "dark", 1, true));
        ingredients.add(new RitualIngredient("essence_of_shadow", "dark", 3, true));
        ingredients.add(new RitualIngredient("heart_of_darkness", "dark", 1, true));
        return ingredients;
    }

    private static List<StructurePattern> createStructurePatterns() {
        List<StructurePattern> patterns = new ArrayList<>();

        List<BlockRequirement> blocks = new ArrayList<>();
        blocks.add(new BlockRequirement(new BlockPos(0, 0, 0), state -> state.is(Blocks.OBSIDIAN)));
        blocks.add(new BlockRequirement(new BlockPos(0, 1, 0), state -> state.is(Blocks.OBSIDIAN)));
        blocks.add(new BlockRequirement(new BlockPos(0, 2, 0), state -> state.is(Blocks.OBSIDIAN)));
        blocks.add(new BlockRequirement(new BlockPos(0, 3, 0), state -> state.is(Blocks.DIAMOND_BLOCK)));
        blocks.add(new BlockRequirement(new BlockPos(-1, 0, 0), state -> state.is(Blocks.REDSTONE_TORCH) || state.is(Blocks.REDSTONE_WALL_TORCH)));
        blocks.add(new BlockRequirement(new BlockPos(1, 0, 0), state -> state.is(Blocks.REDSTONE_TORCH) || state.is(Blocks.REDSTONE_WALL_TORCH)));
        blocks.add(new BlockRequirement(new BlockPos(0, 0, -1), state -> state.is(Blocks.REDSTONE_TORCH) || state.is(Blocks.REDSTONE_WALL_TORCH)));
        blocks.add(new BlockRequirement(new BlockPos(0, 0, 1), state -> state.is(Blocks.REDSTONE_TORCH) || state.is(Blocks.REDSTONE_WALL_TORCH)));

        patterns.add(new StructurePattern(blocks, new BlockPos(0, 0, 0)));

        return patterns;
    }

    @Override
    protected void onRitualStart(Player player, Level level, BlockPos center) {
        level.playSound(null, center, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0F, 0.5F);

        for (int i = 0; i < 50; i++) {
            double x = center.getX() + (level.random.nextDouble() - 0.5) * 6;
            double y = center.getY() + level.random.nextDouble() * 4;
            double z = center.getZ() + (level.random.nextDouble() - 0.5) * 6;
            level.addParticle(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE, x, y, z, 0, 0.1, 0);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lThe darkness stirs... The ritual has begun!"));
        }
    }

    @Override
    protected void executeRitualEffects(Player player, Level level, BlockPos center) {
        for (int tick = 0; tick < ASCENSION_DURATION_TICKS; tick++) {
            final int currentTick = tick;
            level.getServer().tell(new net.minecraft.server.TickTask(
                    level.getServer().getTickCount() + currentTick,
                    () -> {
                        if (currentTick % 20 == 0) {
                            double x = center.getX() + (level.random.nextDouble() - 0.5) * 4;
                            double y = center.getY() + 2 + level.random.nextDouble() * 3;
                            double z = center.getZ() + (level.random.nextDouble() - 0.5) * 4;
                            level.addParticle(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, 0.05, 0);
                            level.addParticle(net.minecraft.core.particles.ParticleTypes.REVERSE_PORTAL, x, y, z, 0, 0, 0);
                        }

                        if (currentTick == 0) {
                            level.playSound(null, center, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 0.5F, 0.5F);
                        }

                        if (currentTick == ASCENSION_DURATION_TICKS / 2) {
                            level.playSound(null, center, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 1.0F, 0.3F);
                        }

                        if (currentTick == ASCENSION_DURATION_TICKS - 1) {
                            for (int i = 0; i < 100; i++) {
                                double x = center.getX() + (level.random.nextDouble() - 0.5) * 8;
                                double y = center.getY() + level.random.nextDouble() * 6;
                                double z = center.getZ() + (level.random.nextDouble() - 0.5) * 8;
                                level.addParticle(net.minecraft.core.particles.ParticleTypes.EXPLOSION, x, y, z, 0, 0, 0);
                            }
                            level.playSound(null, center, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.0F, 0.5F);
                        }
                    }
            ));
        }
    }

    @Override
    protected void onRitualComplete(Player player, Level level, BlockPos center) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        UniqueRoleRegistry registry = UniqueRoleRegistry.get(level.getServer());

        if (!registry.isRoleVacant(UniqueRoleRegistry.RoleType.DARK_ONE)) {
            serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c§lThe Dark One already exists! The ritual fails!"));
            return;
        }

        registry.claimRole(serverPlayer.getUUID(), UniqueRoleRegistry.RoleType.DARK_ONE);
        DarkOneRole.grantRole(serverPlayer);

        ItemStack dagger = new ItemStack(ModItems.DARK_ONE_DAGGER.get());
        dagger.set(ModDataComponents.DARK_ONE_DAGGER.value(),
                new ModDataComponents.DarkOneDaggerData(serverPlayer.getUUID(), java.util.UUID.randomUUID(), true));
        serverPlayer.getInventory().add(dagger);

        serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou have become the Dark One!"));
        serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYour Dark One's Dagger has been bound to you."));

        Vec3 pos = serverPlayer.position();
        for (ServerPlayer otherPlayer : level.getServer().getPlayerList().getPlayers()) {
            if (otherPlayer != serverPlayer && otherPlayer.distanceTo(serverPlayer) < 100) {
                otherPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§4§lA terrible darkness has descended upon the world... " + serverPlayer.getName().getString() + " has become the Dark One!"));
            }
        }

        level.playSound(null, center, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 2.0F, 0.3F);

        for (int i = 0; i < 30; i++) {
            double x = serverPlayer.getX() + (level.random.nextDouble() - 0.5) * 4;
            double y = serverPlayer.getY() + level.random.nextDouble() * 3;
            double z = serverPlayer.getZ() + (level.random.nextDouble() - 0.5) * 4;
            level.addParticle(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, 0.1, 0);
        }
    }
}
