package com.example.ouat.network;

import com.example.ouat.OnceUponATime;
import com.example.ouat.client.ConcealmentClientCache;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record ConcealmentStatePacket(Map<BlockPos, BlockState> blocks, boolean clear) implements CustomPacketPayload {

    public static final Type<ConcealmentStatePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "concealment_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConcealmentStatePacket> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public ConcealmentStatePacket decode(RegistryFriendlyByteBuf buf) {
                    boolean clear = buf.readBoolean();
                    int count = buf.readVarInt();
                    Map<BlockPos, BlockState> blocks = new HashMap<>();
                    for (int i = 0; i < count; i++) {
                        BlockPos pos = BlockPos.of(buf.readLong());
                        CompoundTag stateTag = buf.readNbt();
                        BlockState state = net.minecraft.nbt.NbtUtils.readBlockState(
                                buf.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.BLOCK), stateTag);
                        blocks.put(pos, state);
                    }
                    return new ConcealmentStatePacket(blocks, clear);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, ConcealmentStatePacket packet) {
                    buf.writeBoolean(packet.clear);
                    buf.writeVarInt(packet.blocks.size());
                    for (Map.Entry<BlockPos, BlockState> entry : packet.blocks.entrySet()) {
                        buf.writeLong(entry.getKey().asLong());
                        CompoundTag stateTag = NbtUtils.writeBlockState(entry.getValue());
                        buf.writeNbt(stateTag);
                    }
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ConcealmentStatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide()) {
                if (packet.clear) {
                    ConcealmentClientCache.get().clear();
                } else {
                    ConcealmentClientCache.get().setConcealedBlocks(packet.blocks);
                }
            }
        });
    }
}
