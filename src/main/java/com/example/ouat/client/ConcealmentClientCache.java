package com.example.ouat.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class ConcealmentClientCache {
    private static final ConcealmentClientCache INSTANCE = new ConcealmentClientCache();
    private Map<BlockPos, BlockState> concealedBlocks = new HashMap<>();

    private ConcealmentClientCache() {}

    public static ConcealmentClientCache get() {
        return INSTANCE;
    }

    public void setConcealedBlocks(Map<BlockPos, BlockState> blocks) {
        this.concealedBlocks = new HashMap<>(blocks);
    }

    public Map<BlockPos, BlockState> getConcealedBlocks() {
        return concealedBlocks;
    }

    public BlockState getBlockState(BlockPos pos) {
        return concealedBlocks.get(pos);
    }

    public boolean hasConcealedBlock(BlockPos pos) {
        return concealedBlocks.containsKey(pos);
    }

    public boolean isEmpty() {
        return concealedBlocks.isEmpty();
    }

    public void clear() {
        this.concealedBlocks.clear();
    }
}
