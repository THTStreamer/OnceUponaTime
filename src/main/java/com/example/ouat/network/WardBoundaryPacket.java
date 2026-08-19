package com.example.ouat.network;

import com.example.ouat.OnceUponATime;
import com.example.ouat.client.WardClientData;
import com.example.ouat.client.WardClientData.WardBoundary;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.*;

public record WardBoundaryPacket(
        String dimensionKey,
        List<WardEntry> wards
) implements CustomPacketPayload {

    public static final Type<WardBoundaryPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "ward_boundary_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WardBoundaryPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, WardBoundaryPacket::dimensionKey,
                    ByteBufCodecs.collection(ArrayList::new, WardEntry.STREAM_CODEC), WardBoundaryPacket::wards,
                    WardBoundaryPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record WardEntry(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, UUID owner) {
        public static final StreamCodec<RegistryFriendlyByteBuf, WardEntry> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public WardEntry decode(RegistryFriendlyByteBuf buf) {
                int minX = buf.readVarInt();
                int minY = buf.readVarInt();
                int minZ = buf.readVarInt();
                int maxX = buf.readVarInt();
                int maxY = buf.readVarInt();
                int maxZ = buf.readVarInt();
                UUID owner = new UUID(buf.readLong(), buf.readLong());
                return new WardEntry(minX, minY, minZ, maxX, maxY, maxZ, owner);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, WardEntry entry) {
                buf.writeVarInt(entry.minX);
                buf.writeVarInt(entry.minY);
                buf.writeVarInt(entry.minZ);
                buf.writeVarInt(entry.maxX);
                buf.writeVarInt(entry.maxY);
                buf.writeVarInt(entry.maxZ);
                buf.writeLong(entry.owner.getMostSignificantBits());
                buf.writeLong(entry.owner.getLeastSignificantBits());
            }
        };

        public WardBoundary toBoundary() {
            return new WardBoundary(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ), owner);
        }
    }

    public static void handle(WardBoundaryPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player == null) return;
            WardClientData.clearDimension(packet.dimensionKey);
            for (WardEntry entry : packet.wards) {
                WardClientData.storeWard(packet.dimensionKey, entry.toBoundary());
            }
        });
    }

    public static WardBoundaryPacket fromServer(String dimensionKey, List<WardEntry> wards) {
        return new WardBoundaryPacket(dimensionKey, wards);
    }
}
