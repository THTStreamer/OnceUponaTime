package com.example.ouat.ritual;

import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public abstract class Ritual {
    protected final ResourceLocation ritualId;
    protected final String ritualName;
    protected final IngredientValidator ingredientValidator;
    protected final List<StructurePattern> structurePatterns;
    protected final int cooldownTicks;
    protected final int magicProficiencyRequired;
    protected final PlayerSupernaturalData.MagicalAlignment alignmentRequired;

    public Ritual(ResourceLocation ritualId, String ritualName, List<RitualIngredient> ingredients,
                  List<StructurePattern> structurePatterns, int cooldownTicks, int magicProficiencyRequired) {
        this(ritualId, ritualName, ingredients, structurePatterns, cooldownTicks, magicProficiencyRequired, null);
    }

    public Ritual(ResourceLocation ritualId, String ritualName, List<RitualIngredient> ingredients,
                  List<StructurePattern> structurePatterns, int cooldownTicks, int magicProficiencyRequired,
                  PlayerSupernaturalData.MagicalAlignment alignmentRequired) {
        this.ritualId = ritualId;
        this.ritualName = ritualName;
        this.ingredientValidator = new IngredientValidator(ingredients);
        this.structurePatterns = structurePatterns;
        this.cooldownTicks = cooldownTicks;
        this.magicProficiencyRequired = magicProficiencyRequired;
        this.alignmentRequired = alignmentRequired;
    }

    public ResourceLocation getRitualId() { return ritualId; }
    public String getRitualName() { return ritualName; }
    public int getCooldownTicks() { return cooldownTicks; }
    public int getMagicProficiencyRequired() { return magicProficiencyRequired; }

    public boolean canPerform(Player player, Level level, BlockPos center) {
        if (magicProficiencyRequired > 0) {
            if (!(player instanceof ServerPlayer serverPlayer)) return false;
            PlayerSupernaturalData data = serverPlayer.getData(PlayerSupernaturalData.TYPE);
            if (data == null || data.getMagicProficiency() < magicProficiencyRequired) return false;
        }
        if (alignmentRequired != null) {
            if (!(player instanceof ServerPlayer serverPlayer)) return false;
            PlayerSupernaturalData data = serverPlayer.getData(PlayerSupernaturalData.TYPE);
            PlayerSupernaturalData.MagicalAlignment current = data.getComputedAlignment();
            if (alignmentRequired == PlayerSupernaturalData.MagicalAlignment.LIGHT
                    && current != PlayerSupernaturalData.MagicalAlignment.LIGHT) {
                serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cThis ritual requires a Light alignment."));
                return false;
            }
        }
        if (!ingredientValidator.validate(player)) return false;
        if (!detectStructure(level, center)) return false;
        return true;
    }

    public boolean detectStructure(Level level, BlockPos center) {
        for (StructurePattern pattern : structurePatterns) {
            if (pattern.matches(level, center)) {
                return true;
            }
        }
        return false;
    }

    public boolean performRitual(Player player, Level level, BlockPos center) {
        if (!canPerform(player, level, center)) return false;

        if (!ingredientValidator.consumeIngredients(player)) return false;

        onRitualStart(player, level, center);
        executeRitualEffects(player, level, center);
        onRitualComplete(player, level, center);

        return true;
    }

    protected abstract void onRitualStart(Player player, Level level, BlockPos center);
    protected abstract void executeRitualEffects(Player player, Level level, BlockPos center);
    protected abstract void onRitualComplete(Player player, Level level, BlockPos center);

    public static class StructurePattern {
        private final List<BlockRequirement> blocks;
        private final BlockPos offset;

        public StructurePattern(List<BlockRequirement> blocks, BlockPos offset) {
            this.blocks = blocks;
            this.offset = offset;
        }

        public boolean matches(Level level, BlockPos center) {
            BlockPos actualCenter = center.offset(offset);
            for (BlockRequirement req : blocks) {
                BlockPos pos = actualCenter.offset(req.relativePos());
                BlockState state = level.getBlockState(pos);
                if (!req.matches(state)) {
                    return false;
                }
            }
            return true;
        }

        public List<BlockRequirement> getBlocks() { return List.copyOf(blocks); }
    }

    public static class BlockRequirement {
        private final BlockPos relativePos;
        private final java.util.function.Predicate<BlockState> stateMatcher;

        public BlockRequirement(BlockPos relativePos, java.util.function.Predicate<BlockState> stateMatcher) {
            this.relativePos = relativePos;
            this.stateMatcher = stateMatcher;
        }

        public BlockPos relativePos() { return relativePos; }
        public boolean matches(BlockState state) { return stateMatcher.test(state); }
    }
}
