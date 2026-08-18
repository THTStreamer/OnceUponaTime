package com.example.ouat.blocks;

import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.ritual.Ritual;
import com.example.ouat.ritual.RitualManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Map;

public class RitualAltarBlock extends Block {
    public RitualAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player,
                                              InteractionHand hand,
                                              BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;

        // The altar is the bottom block. The ritual structure sits on top.
        // Ritual offset is (0, -1, 0) from center, so center = pos.above()
        BlockPos center = pos.above();

        for (Map.Entry<net.minecraft.resources.ResourceLocation, Ritual> entry :
                RitualManager.getAllRituals().entrySet()) {

            Ritual ritual = entry.getValue();

            if (ritual.canPerform(serverPlayer, level, center)) {
                boolean success = ritual.performRitual(serverPlayer, level, center);
                if (success) {
                    return ItemInteractionResult.sidedSuccess(false);
                }
            }
        }

        serverPlayer.sendSystemMessage(Component.literal(
                "§7No ritual structure detected. Build a ritual structure on top of this altar."));
        return ItemInteractionResult.sidedSuccess(false);
    }
}
