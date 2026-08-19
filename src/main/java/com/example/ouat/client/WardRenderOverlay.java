package com.example.ouat.client;

import com.example.ouat.OnceUponATime;
import com.example.ouat.client.WardClientData.WardBoundary;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class WardRenderOverlay {

    private static ResourceLocation wardTexture;
    private static boolean textureCreated = false;

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        ensureTexture(mc);

        String dimKey = mc.level.dimension().location().toString();
        List<WardBoundary> wards = WardClientData.getWards(dimKey);
        if (wards.isEmpty()) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        for (WardBoundary ward : wards) {
            renderWardBarrier(poseStack, bufferSource, ward);
        }
    }

    private static void ensureTexture(Minecraft mc) {
        if (textureCreated) return;
        textureCreated = true;
        try {
            NativeImage image = new NativeImage(16, 16, false);
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    image.setPixelRGBA(x, y, 0xFFFFFFFF);
                }
            }
            DynamicTexture tex = new DynamicTexture(image);
            wardTexture = mc.getTextureManager().register("ouat_ward_barrier", tex);
        } catch (Exception e) {
            OnceUponATime.LOGGER.error("Failed to create ward barrier texture", e);
        }
    }

    private static void renderWardBarrier(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, WardBoundary ward) {
        Minecraft mc = Minecraft.getInstance();
        double camX = mc.gameRenderer.getMainCamera().getPosition().x;
        double camY = mc.gameRenderer.getMainCamera().getPosition().y;
        double camZ = mc.gameRenderer.getMainCamera().getPosition().z;

        float x0 = (float)(ward.min().getX() - camX);
        float y0 = (float)(ward.min().getY() - camY);
        float z0 = (float)(ward.min().getZ() - camZ);
        float x1 = (float)(ward.max().getX() + 1.0 - camX);
        float y1 = (float)(ward.max().getY() + 1.0 - camY);
        float z1 = (float)(ward.max().getZ() + 1.0 - camZ);

        int r = 255, g = 217, b = 77, a = 64;

        if (wardTexture != null) {
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucent(wardTexture));
            PoseStack.Pose pose = poseStack.last();

            addQuad(buffer, pose, x0,y0,z1, x1,y0,z1, x1,y0,z0, x0,y0,z0, 0,-1,0, r,g,b,a);
            addQuad(buffer, pose, x0,y1,z0, x1,y1,z0, x1,y1,z1, x0,y1,z1, 0,1,0, r,g,b,a);
            addQuad(buffer, pose, x0,y0,z0, x1,y0,z0, x1,y1,z0, x0,y1,z0, 0,0,-1, r,g,b,a);
            addQuad(buffer, pose, x0,y0,z1, x0,y1,z1, x1,y1,z1, x1,y0,z1, 0,0,1, r,g,b,a);
            addQuad(buffer, pose, x0,y0,z0, x0,y0,z1, x0,y1,z1, x0,y1,z0, -1,0,0, r,g,b,a);
            addQuad(buffer, pose, x1,y0,z1, x1,y0,z0, x1,y1,z0, x1,y1,z1, 1,0,0, r,g,b,a);
        }

        VertexConsumer lineBuffer = bufferSource.getBuffer(RenderType.lines());
        Matrix4f m = poseStack.last().pose();
        drawLineBox(lineBuffer, m, x0, y0, z0, x1, y1, z1, r, g, b, 255);
    }

    private static void addQuad(VertexConsumer buffer, PoseStack.Pose pose,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float nx, float ny, float nz,
            int r, int g, int b, int a) {
        buffer.addVertex(pose, x0, y0, z0).setColor(r,g,b,a).setUv(0,0).setOverlay(655360).setLight(0xF000F0).setNormal(pose, nx, ny, nz);
        buffer.addVertex(pose, x1, y1, z1).setColor(r,g,b,a).setUv(1,0).setOverlay(655360).setLight(0xF000F0).setNormal(pose, nx, ny, nz);
        buffer.addVertex(pose, x2, y2, z2).setColor(r,g,b,a).setUv(1,1).setOverlay(655360).setLight(0xF000F0).setNormal(pose, nx, ny, nz);
        buffer.addVertex(pose, x3, y3, z3).setColor(r,g,b,a).setUv(0,1).setOverlay(655360).setLight(0xF000F0).setNormal(pose, nx, ny, nz);
    }

    private static void drawLineBox(VertexConsumer buffer, Matrix4f m,
            float x0, float y0, float z0, float x1, float y1, float z1,
            int r, int g, int b, int a) {
        addLine(buffer, m, x0,y0,z0, x1,y0,z0, r,g,b,a);
        addLine(buffer, m, x0,y0,z0, x0,y1,z0, r,g,b,a);
        addLine(buffer, m, x0,y1,z0, x1,y1,z0, r,g,b,a);
        addLine(buffer, m, x1,y0,z0, x1,y1,z0, r,g,b,a);

        addLine(buffer, m, x0,y0,z1, x1,y0,z1, r,g,b,a);
        addLine(buffer, m, x0,y0,z1, x0,y1,z1, r,g,b,a);
        addLine(buffer, m, x0,y1,z1, x1,y1,z1, r,g,b,a);
        addLine(buffer, m, x1,y0,z1, x1,y1,z1, r,g,b,a);

        addLine(buffer, m, x0,y0,z0, x0,y0,z1, r,g,b,a);
        addLine(buffer, m, x1,y0,z0, x1,y0,z1, r,g,b,a);
        addLine(buffer, m, x0,y1,z0, x0,y1,z1, r,g,b,a);
        addLine(buffer, m, x1,y1,z0, x1,y1,z1, r,g,b,a);
    }

    private static void addLine(VertexConsumer buffer, Matrix4f m,
            float x0, float y0, float z0, float x1, float y1, float z1,
            int r, int g, int b, int a) {
        buffer.addVertex(m, x0, y0, z0).setColor(r,g,b,a).setNormal(0, 1, 0);
        buffer.addVertex(m, x1, y1, z1).setColor(r,g,b,a).setNormal(0, 1, 0);
    }
}
