package com.example.ouat.client;

import net.minecraft.core.BlockPos;

import java.util.*;

public class WardClientData {
    private static final Map<String, List<WardBoundary>> wardBoundaries = new HashMap<>();

    public static void storeWard(String dimensionKey, WardBoundary boundary) {
        wardBoundaries.computeIfAbsent(dimensionKey, k -> new ArrayList<>()).add(boundary);
    }

    public static void clearDimension(String dimensionKey) {
        wardBoundaries.remove(dimensionKey);
    }

    public static void clearAll() {
        wardBoundaries.clear();
    }

    public static List<WardBoundary> getWards(String dimensionKey) {
        return wardBoundaries.getOrDefault(dimensionKey, Collections.emptyList());
    }

    public record WardBoundary(BlockPos min, BlockPos max, UUID owner, Set<BlockPos> interiorAir) {}
}
