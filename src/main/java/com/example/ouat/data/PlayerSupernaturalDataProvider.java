package com.example.ouat.data;

import com.example.ouat.OnceUponATime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class PlayerSupernaturalDataProvider {

    public static PlayerSupernaturalData getForPlayer(IAttachmentHolder holder) {
        if (holder instanceof ServerPlayer player) {
            return player.getData(PlayerSupernaturalData.TYPE);
        }
        return null;
    }

    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal().level().isClientSide()) return;

        ServerPlayer oldPlayer = (ServerPlayer) event.getOriginal();
        ServerPlayer newPlayer = (ServerPlayer) event.getEntity();

        PlayerSupernaturalData oldData = oldPlayer.getData(PlayerSupernaturalData.TYPE);
        PlayerSupernaturalData newData = newPlayer.getData(PlayerSupernaturalData.TYPE);

        newData.setCurrentRole(oldData.getCurrentRole());
        newData.setMagicalAlignment(oldData.getMagicalAlignment());
        newData.setMagicProficiency(oldData.getMagicProficiency());
        newData.setTeacherUUID(oldData.getTeacherUUID());
        newData.setStoryProgression(oldData.getStoryProgression());
        newData.setHasHeldUniqueRole(oldData.hasHeldUniqueRole());

        for (ResourceLocation spell : oldData.getLearnedSpells()) {
            newData.addSpell(spell);
        }

        for (var curse : oldData.getCurses()) {
            newData.addCurse(curse);
        }

        for (var blessing : oldData.getBlessings()) {
            newData.addBlessing(blessing);
        }

        for (ResourceLocation artifact : oldData.getHeldArtifacts()) {
            newData.addHeldArtifact(artifact);
        }

        for (var entry : oldData.getRitualProgression().entrySet()) {
            newData.setRitualProgression(entry.getKey(), entry.getValue());
        }

        for (ResourceLocation role : oldData.getHeldRoles()) {
            newData.addHeldRole(role);
        }

        OnceUponATime.LOGGER.info("Transferred supernatural data for player {}", newPlayer.getName().getString());
    }
}
