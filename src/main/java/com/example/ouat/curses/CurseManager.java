package com.example.ouat.curses;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.CurseInstance;
import com.example.ouat.data.PlayerSupernaturalData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class CurseManager {
    public static final ResourceLocation SLEEPING_CURSE = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "sleeping_curse");
    public static final ResourceLocation MEMORY_CURSE = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "memory_curse");
    public static final ResourceLocation TRANSFORMATION_CURSE = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "transformation_curse");
    public static final ResourceLocation BINDING_CURSE = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "binding_curse");
    public static final ResourceLocation WEAKNESS_CURSE = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, "weakness_curse");

    public static boolean applyCurse(ServerPlayer target, ResourceLocation curseId, String curseName, long durationTicks, ServerPlayer source) {
        PlayerSupernaturalData targetData = target.getData(PlayerSupernaturalData.TYPE);

        if (targetData.hasCurse(curseId)) {
            target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cYou are already cursed with this!"));
            return false;
        }

        CurseInstance curse = new CurseInstance(curseId, curseName, durationTicks * 50, source != null ? source.getUUID() : null);
        targetData.addCurse(curse);

        target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou have been cursed: " + curseName + "!"));

        if (source != null) {
            source.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aYou have cursed " + target.getName().getString() + " with " + curseName + "!"));
        }

        applyCurseEffects(target, curseId);

        OnceUponATime.LOGGER.info("Applied curse {} to {}", curseName, target.getName().getString());
        return true;
    }

    public static boolean removeCurse(ServerPlayer target, ResourceLocation curseId) {
        PlayerSupernaturalData targetData = target.getData(PlayerSupernaturalData.TYPE);

        if (!targetData.hasCurse(curseId)) {
            return false;
        }

        targetData.removeCurse(curseId);
        removeCurseEffects(target, curseId);

        target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aThe curse has been broken!"));

        OnceUponATime.LOGGER.info("Removed curse {} from {}", curseId, target.getName().getString());
        return true;
    }

    private static void applyCurseEffects(ServerPlayer player, ResourceLocation curseId) {
        if (curseId.equals(SLEEPING_CURSE)) {
            player.setPose(net.minecraft.world.entity.Pose.SLEEPING);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou fall into an enchanted sleep..."));
        } else if (curseId.equals(MEMORY_CURSE)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYour memories begin to fade..."));
        } else if (curseId.equals(TRANSFORMATION_CURSE)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYour form begins to change..."));
        } else if (curseId.equals(BINDING_CURSE)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou feel bound by dark magic..."));
        } else if (curseId.equals(WEAKNESS_CURSE)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lYou feel weakened..."));
        }
    }

    private static void removeCurseEffects(ServerPlayer player, ResourceLocation curseId) {
        if (curseId.equals(SLEEPING_CURSE)) {
            player.setPose(net.minecraft.world.entity.Pose.STANDING);
        }
    }

    public static void tickCurses(ServerPlayer player) {
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        var curses = data.getCurses();

        for (CurseInstance curse : curses) {
            if (curse.isExpired()) {
                removeCurse(player, curse.getCurseId());
            } else {
                tickCurseEffect(player, curse);
            }
        }
    }

    private static void tickCurseEffect(ServerPlayer player, CurseInstance curse) {
        if (curse.getCurseId().equals(WEAKNESS_CURSE)) {
            if (player.level().getGameTime() % 100 == 0) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§lThe weakness curse drains your strength..."));
            }
        } else if (curse.getCurseId().equals(SLEEPING_CURSE)) {
            if (player.getPose() != net.minecraft.world.entity.Pose.SLEEPING) {
                player.setPose(net.minecraft.world.entity.Pose.SLEEPING);
            }
        }
    }
}
