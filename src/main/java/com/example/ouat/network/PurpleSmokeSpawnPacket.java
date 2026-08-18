package com.example.ouat.network;

import com.example.ouat.OnceUponATime;
import com.example.ouat.particles.PurpleSmokeEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PurpleSmokeSpawnPacket(double x, double y, double z) implements CustomPacketPayload {

    public static final Type<PurpleSmokeSpawnPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "purple_smoke_spawn"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PurpleSmokeSpawnPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.DOUBLE, PurpleSmokeSpawnPacket::x,
                    ByteBufCodecs.DOUBLE, PurpleSmokeSpawnPacket::y,
                    ByteBufCodecs.DOUBLE, PurpleSmokeSpawnPacket::z,
                    PurpleSmokeSpawnPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PurpleSmokeSpawnPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide()) {
                PurpleSmokeEffect.startClient(packet.x, packet.y, packet.z);
            }
        });
    }
}
