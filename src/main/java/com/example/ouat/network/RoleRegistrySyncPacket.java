package com.example.ouat.network;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.UniqueRoleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RoleRegistrySyncPacket(
        boolean darkOneOccupied,
        boolean saviorOccupied,
        boolean truestBelieverOccupied,
        boolean authorOccupied,
        String darkOneName,
        String saviorName,
        String truestBelieverName,
        String authorName
) implements CustomPacketPayload {

    public static final Type<RoleRegistrySyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "role_registry_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RoleRegistrySyncPacket> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public RoleRegistrySyncPacket decode(RegistryFriendlyByteBuf buf) {
                    boolean darkOneOccupied = buf.readBoolean();
                    boolean saviorOccupied = buf.readBoolean();
                    boolean truestBelieverOccupied = buf.readBoolean();
                    boolean authorOccupied = buf.readBoolean();
                    String darkOneName = ByteBufCodecs.STRING_UTF8.decode(buf);
                    String saviorName = ByteBufCodecs.STRING_UTF8.decode(buf);
                    String truestBelieverName = ByteBufCodecs.STRING_UTF8.decode(buf);
                    String authorName = ByteBufCodecs.STRING_UTF8.decode(buf);
                    return new RoleRegistrySyncPacket(darkOneOccupied, saviorOccupied, truestBelieverOccupied, authorOccupied, darkOneName, saviorName, truestBelieverName, authorName);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, RoleRegistrySyncPacket packet) {
                    buf.writeBoolean(packet.darkOneOccupied);
                    buf.writeBoolean(packet.saviorOccupied);
                    buf.writeBoolean(packet.truestBelieverOccupied);
                    buf.writeBoolean(packet.authorOccupied);
                    ByteBufCodecs.STRING_UTF8.encode(buf, packet.darkOneName);
                    ByteBufCodecs.STRING_UTF8.encode(buf, packet.saviorName);
                    ByteBufCodecs.STRING_UTF8.encode(buf, packet.truestBelieverName);
                    ByteBufCodecs.STRING_UTF8.encode(buf, packet.authorName);
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static RoleRegistrySyncPacket fromRegistry(UniqueRoleRegistry registry, MinecraftServer server) {
        return new RoleRegistrySyncPacket(
                !registry.isRoleVacant(UniqueRoleRegistry.RoleType.DARK_ONE),
                !registry.isRoleVacant(UniqueRoleRegistry.RoleType.SAVIOR),
                !registry.isRoleVacant(UniqueRoleRegistry.RoleType.TRUEST_BELIEVER),
                !registry.isRoleVacant(UniqueRoleRegistry.RoleType.AUTHOR),
                registry.getHolderName(server, UniqueRoleRegistry.RoleType.DARK_ONE),
                registry.getHolderName(server, UniqueRoleRegistry.RoleType.SAVIOR),
                registry.getHolderName(server, UniqueRoleRegistry.RoleType.TRUEST_BELIEVER),
                registry.getHolderName(server, UniqueRoleRegistry.RoleType.AUTHOR)
        );
    }

    public static void handle(RoleRegistrySyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide()) {
                OnceUponATime.LOGGER.debug("Received role registry sync: Dark One={}, Savior={}, Truest Believer={}, Author={}",
                        packet.darkOneOccupied ? packet.darkOneName : "VACANT",
                        packet.saviorOccupied ? packet.saviorName : "VACANT",
                        packet.truestBelieverOccupied ? packet.truestBelieverName : "VACANT",
                        packet.authorOccupied ? packet.authorName : "VACANT");
            } else {
                var player = context.player();
                if (player instanceof ServerPlayer serverPlayer) {
                    MinecraftServer server = serverPlayer.server;
                    UniqueRoleRegistry registry = UniqueRoleRegistry.get(server);
                    PacketDistributor.sendToPlayer(serverPlayer, fromRegistry(registry, server));
                }
            }
        });
    }
}
