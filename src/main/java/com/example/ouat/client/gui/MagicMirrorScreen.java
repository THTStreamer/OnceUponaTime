package com.example.ouat.client.gui;

import com.example.ouat.OnceUponATime;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class MagicMirrorScreen extends Screen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "textures/gui/magic_mirror.png");
    private final List<Player> nearbyPlayers;
    private Player selectedPlayer;
    private int tickCount = 0;

    public MagicMirrorScreen(List<Player> nearbyPlayers) {
        super(Component.literal("Magic Mirror"));
        this.nearbyPlayers = nearbyPlayers;
    }

    @Override
    protected void init() {
        super.init();
        this.selectedPlayer = null;
    }

    @Override
    public void tick() {
        super.tick();
        tickCount++;

        // Update nearby players list
        if (minecraft != null && minecraft.level != null) {
            nearbyPlayers.clear();
            for (Player player : minecraft.level.players()) {
                if (player != minecraft.player) {
                    nearbyPlayers.add(player);
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Dark background
        guiGraphics.fill(0, 0, this.width, this.height, 0x80000000);

        // Title
        guiGraphics.drawCenteredString(this.font, Component.literal("§5§lMagic Mirror §7- Select a soul to view"),
                this.width / 2, 20, 0xFFFFFF);

        // Draw mirror frame
        int mirrorX = this.width / 2 - 80;
        int mirrorY = 40;
        int mirrorWidth = 160;
        int mirrorHeight = 120;

        // Frame border (ornate)
        guiGraphics.fill(mirrorX - 4, mirrorY - 4, mirrorX + mirrorWidth + 4, mirrorY, 0xFF8B4513); // Top
        guiGraphics.fill(mirrorX - 4, mirrorY + mirrorHeight, mirrorX + mirrorWidth + 4, mirrorY + mirrorHeight + 4, 0xFF8B4513); // Bottom
        guiGraphics.fill(mirrorX - 4, mirrorY, mirrorX, mirrorY + mirrorHeight, 0xFF8B4513); // Left
        guiGraphics.fill(mirrorX + mirrorWidth, mirrorY, mirrorX + mirrorWidth + 4, mirrorY + mirrorHeight, 0xFF8B4513); // Right

        // Mirror surface (shifting pattern based on tick)
        for (int x = 0; x < mirrorWidth; x++) {
            for (int y = 0; y < mirrorHeight; y++) {
                int shimmer = (int) (Math.sin((x + tickCount * 0.5) * 0.1) * 20 + Math.cos((y + tickCount * 0.3) * 0.1) * 20);
                int r = Math.max(0, Math.min(255, 40 + shimmer));
                int g = Math.max(0, Math.min(255, 40 + shimmer / 2));
                int b = Math.max(0, Math.min(255, 60 + shimmer));
                guiGraphics.fill(mirrorX + x, mirrorY + y, mirrorX + x + 1, mirrorY + y + 1, 0xFF000000 | (r << 16) | (g << 8) | b);
            }
        }

        // Draw selected player in mirror
        if (selectedPlayer != null) {
            // Render player head in mirror
            int headX = mirrorX + mirrorWidth / 2;
            int headY = mirrorY + mirrorHeight / 2;

            // Draw a simple representation of the player
            guiGraphics.fill(headX - 8, headY - 8, headX + 8, headY + 8, 0xFF00FF00);
            guiGraphics.fill(headX - 4, headY - 12, headX + 4, headY - 4, 0xFF00FF00);

            // Player name
            guiGraphics.drawCenteredString(this.font, selectedPlayer.getName().getString(),
                    headX, headY + 15, 0xFFFFFF);

            // Instructions
            guiGraphics.drawCenteredString(this.font, "§7Right-click mirror to release",
                    this.width / 2, mirrorY + mirrorHeight + 20, 0xAAAAAA);
        } else {
            // No selection message
            guiGraphics.drawCenteredString(this.font, "§7Click a name below to view through the mirror",
                    this.width / 2, mirrorY + mirrorHeight / 2, 0xAAAAAA);
        }

        // Draw player list
        int listY = mirrorY + mirrorHeight + 35;
        int listX = this.width / 2 - 75;

        guiGraphics.drawString(this.font, "§6Online Players:", listX, listY, 0xFFFFFF);
        listY += 15;

        for (int i = 0; i < nearbyPlayers.size(); i++) {
            Player player = nearbyPlayers.get(i);
            int buttonY = listY + i * 20;

            if (buttonY > this.height - 30) break;

            // Button background
            boolean hovered = mouseX >= listX && mouseX <= listX + 150 &&
                    mouseY >= buttonY && mouseY <= buttonY + 18;
            int bgColor = hovered ? 0xFF555555 : (selectedPlayer == player ? 0xFF3333AA : 0xFF222222);
            guiGraphics.fill(listX, buttonY, listX + 150, buttonY + 18, bgColor);

            // Player name
            String name = player.getName().getString();
            int textColor = selectedPlayer == player ? 0xFFFFFF : 0xAAAAAA;
            guiGraphics.drawString(this.font, name, listX + 5, buttonY + 5, textColor);
        }

        if (nearbyPlayers.isEmpty()) {
            guiGraphics.drawString(this.font, "§7No other players online", listX, listY, 0xAAAAAA);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int listY = 40 + 120 + 35 + 15;
            int listX = this.width / 2 - 75;

            for (int i = 0; i < nearbyPlayers.size(); i++) {
                int buttonY = listY + i * 20;
                if (mouseX >= listX && mouseX <= listX + 150 &&
                        mouseY >= buttonY && mouseY <= buttonY + 18) {
                    selectedPlayer = nearbyPlayers.get(i);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
