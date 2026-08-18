package com.example.ouat.network;

import com.example.ouat.OnceUponATime;
import com.example.ouat.particles.DarkSmokeEffect;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DarkSmokeSpawnPacket(double x, double y, double z, int targetCount) implements CustomPacketPayload {

    public static final Type<DarkSmokeSpawnPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "dark_smoke_spawn"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DarkSmokeSpawnPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.DOUBLE, DarkSmokeSpawnPacket::x,
                    ByteBufCodecs.DOUBLE, DarkSmokeSpawnPacket::y,
                    ByteBufCodecs.DOUBLE, DarkSmokeSpawnPacket::z,
                    ByteBufCodecs.VAR_INT, DarkSmokeSpawnPacket::targetCount,
                    DarkSmokeSpawnPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DarkSmokeSpawnPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide()) {
                DarkSmokeEffect.startClient(packet.x, packet.y, packet.z, packet.targetCount);
            }
        });
    }
}
