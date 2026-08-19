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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.*;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class WardRenderOverlay {

    private static ResourceLocation wardTexture;
    private static boolean textureCreated = false;

    private static final int R = 255, G = 217, B = 77, A = 64;

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
        Level level = mc.level;

        for (WardBoundary ward : wards) {
            renderWard(poseStack, bufferSource, ward, level);
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

    private static void renderWard(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, WardBoundary ward, Level level) {
        Minecraft mc = Minecraft.getInstance();
        double camX = mc.gameRenderer.getMainCamera().getPosition().x;
        double camY = mc.gameRenderer.getMainCamera().getPosition().y;
        double camZ = mc.gameRenderer.getMainCamera().getPosition().z;

        float cx = (float) camX, cy = (float) camY, cz = (float) camZ;

        Set<BlockPos> interiorAir = ward.interiorAir();

        Set<BlockPos> wallBlocks = new HashSet<>();
        for (BlockPos pos : interiorAir) {
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.relative(dir);
                if (!interiorAir.contains(neighbor)) {
                    BlockState state = level.getBlockState(neighbor);
                    if (!state.isAir()) {
                        wallBlocks.add(neighbor);
                    }
                }
            }
        }

        if (wardTexture == null) return;
        VertexConsumer quadBuffer = bufferSource.getBuffer(RenderType.entityTranslucent(wardTexture));
        PoseStack.Pose pose = poseStack.last();

        for (BlockPos wall : wallBlocks) {
            for (Direction dir : Direction.values()) {
                BlockPos faceNeighbor = wall.relative(dir);
                if (!interiorAir.contains(faceNeighbor) && level.getBlockState(faceNeighbor).isAir()) {
                    renderFace(quadBuffer, pose, wall, dir, cx, cy, cz);
                }
            }
        }

        Matrix4f m = pose.pose();
        VertexConsumer lineBuffer = bufferSource.getBuffer(RenderType.lines());
        float x0 = ward.min().getX() - cx;
        float y0 = ward.min().getY() - cy;
        float z0 = ward.min().getZ() - cz;
        float x1 = ward.max().getX() + 1 - cx;
        float y1 = ward.max().getY() + 1 - cy;
        float z1 = ward.max().getZ() + 1 - cz;
        drawLineBox(lineBuffer, m, x0, y0, z0, x1, y1, z1);
    }

    private static void renderFace(VertexConsumer buffer, PoseStack.Pose pose, BlockPos block, Direction dir, float cx, float cy, float cz) {
        float x = block.getX() - cx;
        float y = block.getY() - cy;
        float z = block.getZ() - cz;

        float nx = (float) dir.getStepX();
        float ny = (float) dir.getStepY();
        float nz = (float) dir.getStepZ();

        float[][] verts = getFaceVertices(x, y, z, dir);

        buffer.addVertex(pose, verts[0][0], verts[0][1], verts[0][2]).setColor(R,G,B,A).setUv(0,0).setOverlay(655360).setLight(0xF000F0).setNormal(pose, nx, ny, nz);
        buffer.addVertex(pose, verts[1][0], verts[1][1], verts[1][2]).setColor(R,G,B,A).setUv(1,0).setOverlay(655360).setLight(0xF000F0).setNormal(pose, nx, ny, nz);
        buffer.addVertex(pose, verts[2][0], verts[2][1], verts[2][2]).setColor(R,G,B,A).setUv(1,1).setOverlay(655360).setLight(0xF000F0).setNormal(pose, nx, ny, nz);
        buffer.addVertex(pose, verts[3][0], verts[3][1], verts[3][2]).setColor(R,G,B,A).setUv(0,1).setOverlay(655360).setLight(0xF000F0).setNormal(pose, nx, ny, nz);
    }

    private static float[][] getFaceVertices(float x, float y, float z, Direction dir) {
        float x1 = x + 1, y1 = y + 1, z1 = z + 1;
        return switch (dir) {
            case DOWN  -> new float[][]{{x,y,z}, {x1,y,z}, {x1,y,z1}, {x,y,z1}};
            case UP    -> new float[][]{{x,y1,z1}, {x1,y1,z1}, {x1,y1,z}, {x,y1,z}};
            case NORTH -> new float[][]{{x1,y,z}, {x,y,z}, {x,y1,z}, {x1,y1,z}};
            case SOUTH -> new float[][]{{x,y,z1}, {x1,y,z1}, {x1,y1,z1}, {x,y1,z1}};
            case WEST  -> new float[][]{{x,y,z1}, {x,y,z}, {x,y1,z}, {x,y1,z1}};
            case EAST  -> new float[][]{{x1,y,z}, {x1,y,z1}, {x1,y1,z1}, {x1,y1,z}};
        };
    }

    private static void drawLineBox(VertexConsumer buffer, Matrix4f m,
            float x0, float y0, float z0, float x1, float y1, float z1) {
        addLine(buffer, m, x0,y0,z0, x1,y0,z0);
        addLine(buffer, m, x0,y0,z0, x0,y1,z0);
        addLine(buffer, m, x0,y1,z0, x1,y1,z0);
        addLine(buffer, m, x1,y0,z0, x1,y1,z0);
        addLine(buffer, m, x0,y0,z1, x1,y0,z1);
        addLine(buffer, m, x0,y0,z1, x0,y1,z1);
        addLine(buffer, m, x0,y1,z1, x1,y1,z1);
        addLine(buffer, m, x1,y0,z1, x1,y1,z1);
        addLine(buffer, m, x0,y0,z0, x0,y0,z1);
        addLine(buffer, m, x1,y0,z0, x1,y0,z1);
        addLine(buffer, m, x0,y1,z0, x0,y1,z1);
        addLine(buffer, m, x1,y1,z0, x1,y1,z1);
    }

    private static void addLine(VertexConsumer buffer, Matrix4f m,
            float x0, float y0, float z0, float x1, float y1, float z1) {
        buffer.addVertex(m, x0, y0, z0).setColor(R,G,B,255).setNormal(0, 1, 0);
        buffer.addVertex(m, x1, y1, z1).setColor(R,G,B,255).setNormal(0, 1, 0);
    }
}
