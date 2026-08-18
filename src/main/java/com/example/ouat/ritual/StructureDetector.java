package com.example.ouat.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class StructureDetector {
    private final List<StructureTemplate> templates;
    private final int detectionRange;

    public StructureDetector(int detectionRange) {
        this.templates = new ArrayList<>();
        this.detectionRange = detectionRange;
    }

    public void addTemplate(StructureTemplate template) {
        templates.add(template);
    }

    public StructureMatch findStructure(Level level, BlockPos center) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            for (StructureTemplate template : templates) {
                StructureMatch match = template.matchesAt(level, center, direction);
                if (match != null) {
                    return match;
                }
            }
        }
        return null;
    }

    public boolean hasStructureAt(Level level, BlockPos center) {
        return findStructure(level, center) != null;
    }

    public static class StructureTemplate {
        private final List<BlockRequirement> requirements;

        public StructureTemplate(List<BlockRequirement> requirements) {
            this.requirements = requirements;
        }

        public StructureMatch matchesAt(Level level, BlockPos center, Direction facing) {
            for (BlockRequirement req : requirements) {
                BlockPos pos = rotateOffset(req.relativePos(), facing);
                BlockPos actualPos = center.offset(pos);
                BlockState state = level.getBlockState(actualPos);
                if (!req.matches(state)) {
                    return null;
                }
            }
            return new StructureMatch(center, facing, this);
        }

        private BlockPos rotateOffset(BlockPos offset, Direction facing) {
            return switch (facing) {
                case NORTH -> offset;
                case SOUTH -> new BlockPos(-offset.getX(), offset.getY(), -offset.getZ());
                case EAST -> new BlockPos(-offset.getZ(), offset.getY(), offset.getX());
                case WEST -> new BlockPos(offset.getZ(), offset.getY(), -offset.getX());
                default -> offset;
            };
        }

        public List<BlockRequirement> getRequirements() { return List.copyOf(requirements); }
    }

    public static class BlockRequirement {
        private final BlockPos relativePos;
        private final java.util.function.Predicate<BlockState> matcher;
        private final String description;

        public BlockRequirement(BlockPos relativePos, java.util.function.Predicate<BlockState> matcher, String description) {
            this.relativePos = relativePos;
            this.matcher = matcher;
            this.description = description;
        }

        public BlockPos relativePos() { return relativePos; }
        public boolean matches(BlockState state) { return matcher.test(state); }
        public String getDescription() { return description; }
    }

    public static class StructureMatch {
        private final BlockPos center;
        private final Direction facing;
        private final StructureTemplate template;

        public StructureMatch(BlockPos center, Direction facing, StructureTemplate template) {
            this.center = center;
            this.facing = facing;
            this.template = template;
        }

        public BlockPos getCenter() { return center; }
        public Direction getFacing() { return facing; }
        public StructureTemplate getTemplate() { return template; }
    }
}
