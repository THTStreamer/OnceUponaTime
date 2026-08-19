package com.example.ouat.client;

import com.example.ouat.OnceUponATime;
import com.example.ouat.client.WardClientData.WardBoundary;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class WardRenderOverlay {

    private static final float[] WARD_COLOR = {1.0f, 0.85f, 0.3f}; // golden amber
    private static final float ALPHA = 0.6f;

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        String dimKey = mc.level.dimension().location().toString();
        List<WardBoundary> wards = WardClientData.getWards(dimKey);
        if (wards.isEmpty()) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());

        for (WardBoundary ward : wards) {
            drawBoundingBox(poseStack, buffer, ward);
        }

        bufferSource.endBatch(RenderType.lines());
    }

    private static void drawBoundingBox(PoseStack poseStack, VertexConsumer buffer, WardBoundary ward) {
        double camX = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().x;
        double camY = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y;
        double camZ = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().z;

        double minX = ward.min().getX() - camX;
        double minY = ward.min().getY() - camY;
        double minZ = ward.min().getZ() - camZ;
        double maxX = ward.max().getX() + 1.0 - camX;
        double maxY = ward.max().getY() + 1.0 - camY;
        double maxZ = ward.max().getZ() + 1.0 - camZ;

        float r = WARD_COLOR[0];
        float g = WARD_COLOR[1];
        float b = WARD_COLOR[2];

        LevelRenderer.renderLineBox(poseStack, buffer,
                minX, minY, minZ,
                maxX, maxY, maxZ,
                r, g, b, ALPHA);
    }
}
