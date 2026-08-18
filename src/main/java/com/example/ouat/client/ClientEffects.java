package com.example.ouat.client;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.roles.DarkOneRole;
import com.example.ouat.roles.SaviorRole;
import com.example.ouat.roles.TruestBelieverRole;
import com.example.ouat.roles.AuthorRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientEffects {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        PlayerSupernaturalData data = mc.player.getData(PlayerSupernaturalData.TYPE);

        if (DarkOneRole.isDarkOne(data)) {
            renderDarkOneEffects(event, mc.player);
        } else if (SaviorRole.isSavior(data)) {
            renderSaviorEffects(event, mc.player);
        } else if (TruestBelieverRole.isTruestBeliever(data)) {
            renderTruestBelieverEffects(event, mc.player);
        } else if (AuthorRole.isAuthor(data)) {
            renderAuthorEffects(event, mc.player);
        }
    }

    private static void renderDarkOneEffects(RenderLevelStageEvent event, Player player) {
    }

    private static void renderSaviorEffects(RenderLevelStageEvent event, Player player) {
    }

    private static void renderTruestBelieverEffects(RenderLevelStageEvent event, Player player) {
    }

    private static void renderAuthorEffects(RenderLevelStageEvent event, Player player) {
    }
}
