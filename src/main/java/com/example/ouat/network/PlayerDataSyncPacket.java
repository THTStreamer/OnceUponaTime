package com.example.ouat.network;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlayerDataSyncPacket(
        String currentRole,
        String magicalAlignment,
        int magicProficiency,
        boolean hasHeldUniqueRole,
        int storyProgression
) implements CustomPacketPayload {

    public static final Type<PlayerDataSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "player_data_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerDataSyncPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, PlayerDataSyncPacket::currentRole,
                    ByteBufCodecs.STRING_UTF8, PlayerDataSyncPacket::magicalAlignment,
                    ByteBufCodecs.VAR_INT, PlayerDataSyncPacket::magicProficiency,
                    ByteBufCodecs.BOOL, PlayerDataSyncPacket::hasHeldUniqueRole,
                    ByteBufCodecs.VAR_INT, PlayerDataSyncPacket::storyProgression,
                    PlayerDataSyncPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PlayerDataSyncPacket fromData(PlayerSupernaturalData data) {
        return new PlayerDataSyncPacket(
                data.getCurrentRole() != null ? data.getCurrentRole().toString() : "",
                data.getMagicalAlignment().name(),
                data.getMagicProficiency(),
                data.hasHeldUniqueRole(),
                data.getStoryProgression()
        );
    }

    public static void handle(PlayerDataSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide()) {
                var player = Minecraft.getInstance().player;
                if (player != null) {
                    PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
                    if (packet.currentRole != null && !packet.currentRole.isEmpty()) {
                        data.setCurrentRole(ResourceLocation.parse(packet.currentRole));
                    } else {
                        data.setCurrentRole(null);
                    }
                    data.setMagicalAlignment(PlayerSupernaturalData.MagicalAlignment.valueOf(packet.magicalAlignment));
                    data.setMagicProficiency(packet.magicProficiency);
                    data.setHasHeldUniqueRole(packet.hasHeldUniqueRole);
                    data.setStoryProgression(packet.storyProgression);
                }
            } else {
                var player = context.player();
                if (player instanceof ServerPlayer serverPlayer) {
                    PlayerSupernaturalData data = serverPlayer.getData(PlayerSupernaturalData.TYPE);
                    PacketDistributor.sendToPlayer(serverPlayer, fromData(data));
                }
            }
        });
    }
}
