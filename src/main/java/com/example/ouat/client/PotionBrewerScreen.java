package com.example.ouat.client;

import com.example.ouat.OnceUponATime;
import com.example.ouat.menu.PotionBrewerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PotionBrewerScreen extends AbstractContainerScreen<PotionBrewerMenu> {
    private final PotionBrewerMenu menu;

    public PotionBrewerScreen(PotionBrewerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.menu = menu;
        this.imageWidth = 176;
        this.imageHeight = 197;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // Main background — dark cauldron-like panel
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFF1A0A2E);
        guiGraphics.fill(x + 1, y + 1, x + this.imageWidth - 1, y + this.imageHeight - 1, 0xFF2D1B4E);

        // Title area
        guiGraphics.fill(x, y, x + this.imageWidth, y + 14, 0xFF0D0520);

        // 3x3 input grid background
        guiGraphics.fill(x + 20, y + 10, x + 110, y + 100, 0xFF120828);
        guiGraphics.fill(x + 21, y + 11, x + 109, y + 99, 0xFF1E1040);

        // Grid slot outlines (3x3)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int sx = x + 25 + col * 28;
                int sy = y + 17 + row * 28;
                guiGraphics.fill(sx, sy, sx + 18, sy + 18, 0xFF0A0518);
                guiGraphics.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF2A1850);
            }
        }

        // Arrow area
        guiGraphics.fill(x + 112, y + 42, x + 132, y + 52, 0xFF4A2A80);
        guiGraphics.drawString(this.font, ">>", x + 116, y + 43, 0xFFCC88, false);

        // Output slot
        guiGraphics.fill(x + 142, y + 52, x + 159, y + 69, 0xFF0A0518);
        guiGraphics.fill(x + 143, y + 53, x + 158, y + 68, 0xFF2A1850);

        if (menu.canBrew()) {
            float glow = (float) (Math.sin(System.currentTimeMillis() / 200.0) * 0.3 + 0.7);
            int alpha = (int) (glow * 80);
            guiGraphics.fill(x + 142, y + 52, x + 159, y + 69, (alpha << 24) | 0x8040FF);
        }

        // Brew button
        int btnColor = menu.canBrew() ? 0xFF6B3FA0 : 0xFF333333;
        int textColor = menu.canBrew() ? 0xFF00FF00 : 0xFF666666;
        guiGraphics.fill(x + 135, y + 75, x + 167, y + 91, btnColor);
        guiGraphics.fill(x + 136, y + 76, x + 166, y + 90, menu.canBrew() ? 0xFF8B5FC0 : 0xFF444444);
        guiGraphics.drawString(this.font, "Brew", x + 141, y + 80, textColor, false);

        // Cauldron bubbles decoration
        long time = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            double bx = x + 65 + Math.sin(time / 500.0 + i * 1.3) * 15;
            double by = y + 50 + Math.cos(time / 400.0 + i * 0.9) * 8;
            int size = 2 + (int) (Math.sin(time / 300.0 + i) * 1);
            int bubbleAlpha = (int) (Math.sin(time / 600.0 + i * 2) * 40 + 60);
            guiGraphics.fill((int) bx, (int) by, (int) bx + size, (int) by + size,
                    (bubbleAlpha << 24) | 0x9966CC);
        }

        // Player inventory background
        guiGraphics.fill(x + 7, y + 108, x + 169, y + 172, 0xFF120828);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.imageWidth / 2 - this.font.width(this.title) / 2, 3, 0xFFCC88, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFFCC88, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int bx = this.leftPos + 135;
            int by = this.topPos + 75;
            if (mouseX >= bx && mouseX < bx + 32 && mouseY >= by && mouseY < by + 16 && menu.canBrew()) {
                if (this.minecraft != null && this.minecraft.player != null) {
                    this.minecraft.gameMode.handleInventoryMouseClick(
                            this.menu.containerId, PotionBrewerMenu.OUTPUT_SLOT, 0,
                            net.minecraft.world.inventory.ClickType.PICKUP, this.minecraft.player);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
