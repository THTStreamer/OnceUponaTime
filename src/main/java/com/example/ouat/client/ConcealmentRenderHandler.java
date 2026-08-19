package com.example.ouat.client;

import com.example.ouat.OnceUponATime;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ConcealmentRenderHandler {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) return;

        ConcealmentClientCache cache = ConcealmentClientCache.get();
        if (cache.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        PoseStack poseStack = event.getPoseStack();
        double camX = event.getCamera().getPosition().x;
        double camY = event.getCamera().getPosition().y;
        double camZ = event.getCamera().getPosition().z;

        for (var entry : cache.getConcealedBlocks().entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();

            if (!level.isLoaded(pos)) continue;

            double dist = pos.distToCenterSqr(camX, camY, camZ);
            if (dist > 64 * 64) continue;

            poseStack.pushPose();
            poseStack.translate(pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ);

            blockRenderer.renderSingleBlock(
                    state,
                    poseStack,
                    bufferSource,
                    level.getLightEngine().getRawBrightness(pos, 0),
                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                    net.neoforged.neoforge.client.model.data.ModelData.EMPTY,
                    RenderType.cutout()
            );

            poseStack.popPose();
        }

        bufferSource.endBatch();
    }
}
