package com.example.ouat.commands;

import com.example.ouat.OnceUponATime;
import com.example.ouat.data.PlayerSupernaturalData;
import com.example.ouat.data.UniqueRoleRegistry;
import com.example.ouat.magic.Spell;
import com.example.ouat.magic.SpellRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = OnceUponATime.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class OuATCommands {

    private static final SuggestionProvider<CommandSourceStack> ALL_SPELL_SUGGESTIONS = (context, builder) -> {
        String remaining = builder.getRemainingLowerCase();
        for (ResourceLocation id : SpellRegistry.getAllSpells().keySet()) {
            String spellName = id.getPath();
            if (spellName.startsWith(remaining) || remaining.isEmpty()) {
                builder.suggest(spellName);
            }
        }
        return builder.buildFuture();
    };

    private static final SuggestionProvider<CommandSourceStack> KNOWN_SPELL_SUGGESTIONS = (context, builder) -> {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return builder.buildFuture();
        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        List<ResourceLocation> known = data.getLearnedSpells();
        String remaining = builder.getRemainingLowerCase();
        for (ResourceLocation id : known) {
            String spellName = id.getPath();
            if (spellName.startsWith(remaining) || remaining.isEmpty()) {
                builder.suggest(spellName);
            }
        }
        return builder.buildFuture();
    };

    private static final SuggestionProvider<CommandSourceStack> ROLE_ACTION_SUGGESTIONS = (context, builder) -> {
        String[] actions = {"check", "clear"};
        String remaining = builder.getRemainingLowerCase();
        for (String action : actions) {
            if (action.startsWith(remaining) || remaining.isEmpty()) {
                builder.suggest(action);
            }
        }
        return builder.buildFuture();
    };

    private static final SuggestionProvider<CommandSourceStack> MAGIC_ACTION_SUGGESTIONS = (context, builder) -> {
        String[] actions = {"check", "set"};
        String remaining = builder.getRemainingLowerCase();
        for (String action : actions) {
            if (action.startsWith(remaining) || remaining.isEmpty()) {
                builder.suggest(action);
            }
        }
        return builder.buildFuture();
    };

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("ouat")
                .then(Commands.literal("check")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> handleDataCommand(context.getSource(),
                                        EntityArgument.getPlayer(context, "player")))))
                .then(Commands.literal("role")
                        .then(Commands.argument("action", StringArgumentType.word())
                                .suggests(ROLE_ACTION_SUGGESTIONS)
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> handleRoleCommand(context.getSource(),
                                                StringArgumentType.getString(context, "action"),
                                                EntityArgument.getPlayer(context, "player"))))
                                .executes(context -> handleRoleCommandSelf(context.getSource(),
                                        StringArgumentType.getString(context, "action")))))
                .then(Commands.literal("magic")
                        .then(Commands.argument("action", StringArgumentType.word())
                                .suggests(MAGIC_ACTION_SUGGESTIONS)
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> handleMagicCommand(context.getSource(),
                                                StringArgumentType.getString(context, "action"),
                                                EntityArgument.getPlayer(context, "player"))))
                                .executes(context -> handleMagicCommandSelf(context.getSource(),
                                        StringArgumentType.getString(context, "action")))))
                .then(Commands.literal("cast")
                        .then(Commands.argument("spell", StringArgumentType.word())
                                .suggests(KNOWN_SPELL_SUGGESTIONS)
                                .executes(context -> handleCastCommand(context.getSource(),
                                        StringArgumentType.getString(context, "spell"), null))
                                .then(Commands.argument("argument", StringArgumentType.greedyString())
                                        .executes(context -> handleCastCommand(context.getSource(),
                                                StringArgumentType.getString(context, "spell"),
                                                StringArgumentType.getString(context, "argument"))))))
                .then(Commands.literal("learn")
                        .then(Commands.argument("spell", StringArgumentType.word())
                                .suggests(ALL_SPELL_SUGGESTIONS)
                                .executes(context -> handleLearnCommand(context.getSource(),
                                        StringArgumentType.getString(context, "spell")))))
                .then(Commands.literal("spells")
                        .executes(context -> handleListSpellsCommand(context.getSource())))
                .then(Commands.literal("list")
                        .executes(context -> handleMySpellsCommand(context.getSource())))
                .then(Commands.literal("data")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> handleDataCommand(context.getSource(),
                                        EntityArgument.getPlayer(context, "player")))))
                .then(Commands.literal("debug")
                        .then(Commands.literal("registry")
                                .executes(context -> handleDebugRegistry(context.getSource()))))
        );
    }

    private static int handleRoleCommand(CommandSourceStack source, String action, ServerPlayer target) {
        PlayerSupernaturalData data = target.getData(PlayerSupernaturalData.TYPE);

        switch (action) {
            case "check" -> {
                if (data.getCurrentRole() != null) {
                    source.sendSuccess(() -> Component.literal(target.getName().getString() + "'s role: " + data.getCurrentRole()), false);
                } else {
                    source.sendSuccess(() -> Component.literal(target.getName().getString() + " has no role"), false);
                }
            }
            case "clear" -> {
                data.setCurrentRole(null);
                source.sendSuccess(() -> Component.literal("Cleared " + target.getName().getString() + "'s role"), false);
            }
            default -> source.sendSuccess(() -> Component.literal("§cUnknown action. Use: check, clear"), false);
        }
        return 1;
    }

    private static int handleRoleCommandSelf(CommandSourceStack source, String action) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;
        return handleRoleCommand(source, action, player);
    }

    private static int handleMagicCommand(CommandSourceStack source, String action, ServerPlayer target) {
        PlayerSupernaturalData data = target.getData(PlayerSupernaturalData.TYPE);

        switch (action) {
            case "check" -> {
                source.sendSuccess(() -> Component.literal(target.getName().getString() + " magic proficiency: " + data.getMagicProficiency()), false);
                source.sendSuccess(() -> Component.literal("Alignment: " + data.getMagicalAlignment()), false);
                source.sendSuccess(() -> Component.literal("Spells learned: " + data.getLearnedSpells().size()), false);
            }
            case "set" -> {
                data.setMagicProficiency(50);
                source.sendSuccess(() -> Component.literal("Set " + target.getName().getString() + " magic proficiency to 50"), false);
            }
            default -> source.sendSuccess(() -> Component.literal("Unknown action. Use: check, set"), false);
        }
        return 1;
    }

    private static int handleMagicCommandSelf(CommandSourceStack source, String action) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;
        return handleMagicCommand(source, action, player);
    }

    private static int handleCastCommand(CommandSourceStack source, String spellName, String argument) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendSuccess(() -> Component.literal("§cThis command can only be used by a player."), false);
            return 0;
        }

        ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, spellName);
        Spell spell = SpellRegistry.getSpell(spellId);
        if (spell == null) {
            source.sendSuccess(() -> Component.literal("§cUnknown spell: " + spellName), false);
            source.sendSuccess(() -> Component.literal("§7Use /ouat spells to see all spells."), false);
            return 0;
        }

        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (!data.hasSpell(spellId)) {
            source.sendSuccess(() -> Component.literal("§cYou don't know this spell. Use /ouat learn " + spellName + " first."), false);
            return 0;
        }

        boolean success = argument != null ? spell.tryCast(player) : spell.tryCast(player);
        return success ? 1 : 0;
    }

    private static int handleLearnCommand(CommandSourceStack source, String spellName) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendSuccess(() -> Component.literal("§cThis command can only be used by a player."), false);
            return 0;
        }

        ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(OnceUponATime.MOD_ID, spellName);
        Spell spell = SpellRegistry.getSpell(spellId);
        if (spell == null) {
            source.sendSuccess(() -> Component.literal("§cUnknown spell: " + spellName), false);
            return 0;
        }

        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        if (data.hasSpell(spellId)) {
            source.sendSuccess(() -> Component.literal("§cYou already know this spell."), false);
            return 0;
        }

        if (data.getMagicProficiency() < spell.getProficiencyRequired()) {
            source.sendSuccess(() -> Component.literal("§cYour magic proficiency is too low. Required: " + spell.getProficiencyRequired() + ", Current: " + data.getMagicProficiency()), false);
            return 0;
        }

        data.addSpell(spellId);
        data.addMagicProficiency(5);
        source.sendSuccess(() -> Component.literal("§aYou learned " + spell.getSpellName() + "!"), false);
        return 1;
    }

    private static int handleListSpellsCommand(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("§6=== All Spells ==="), false);
        for (var entry : SpellRegistry.getAllSpells().entrySet()) {
            Spell spell = entry.getValue();
            source.sendSuccess(() -> Component.literal("§e" + spell.getSpellName() + " §7(Prof: " + spell.getProficiencyRequired() + ")"), false);
        }
        source.sendSuccess(() -> Component.literal("§7Use /ouat learn <spell> to learn"), false);
        source.sendSuccess(() -> Component.literal("§7Use /ouat list to see your learned spells"), false);
        return 1;
    }

    private static int handleMySpellsCommand(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendSuccess(() -> Component.literal("§cThis command can only be used by a player."), false);
            return 0;
        }

        PlayerSupernaturalData data = player.getData(PlayerSupernaturalData.TYPE);
        List<ResourceLocation> learned = data.getLearnedSpells();

        if (learned.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§7You haven't learned any spells yet."), false);
            source.sendSuccess(() -> Component.literal("§7Use /ouat spells to see all available spells."), false);
            source.sendSuccess(() -> Component.literal("§7Use /ouat learn <spell> to learn one."), false);
            return 1;
        }

        source.sendSuccess(() -> Component.literal("§6=== Your Spells (" + learned.size() + ") ==="), false);
        for (ResourceLocation id : learned) {
            Spell spell = SpellRegistry.getSpell(id);
            String name = spell != null ? spell.getSpellName() : id.getPath();
            source.sendSuccess(() -> Component.literal("§a" + name + " §7- /ouat cast " + id.getPath()), false);
        }
        source.sendSuccess(() -> Component.literal("§7Use /ouat cast <spell> to cast"), false);
        return 1;
    }

    private static int handleDataCommand(CommandSourceStack source, ServerPlayer target) {
        PlayerSupernaturalData data = target.getData(PlayerSupernaturalData.TYPE);
        source.sendSuccess(() -> Component.literal("=== " + target.getName().getString() + " Supernatural Data ==="), false);
        source.sendSuccess(() -> Component.literal("Role: " + (data.getCurrentRole() != null ? data.getCurrentRole() : "NONE")), false);
        source.sendSuccess(() -> Component.literal("Alignment: " + data.getMagicalAlignment()), false);
        source.sendSuccess(() -> Component.literal("Proficiency: " + data.getMagicProficiency()), false);
        source.sendSuccess(() -> Component.literal("Spells: " + data.getLearnedSpells().size()), false);
        source.sendSuccess(() -> Component.literal("Curses: " + data.getCurses().size()), false);
        source.sendSuccess(() -> Component.literal("Blessings: " + data.getBlessings().size()), false);
        source.sendSuccess(() -> Component.literal("Artifacts: " + data.getHeldArtifacts().size()), false);
        source.sendSuccess(() -> Component.literal("Story Progression: " + data.getStoryProgression()), false);
        source.sendSuccess(() -> Component.literal("Held Unique Roles: " + data.hasHeldUniqueRole()), false);
        source.sendSuccess(() -> Component.literal("Dark One Kills: " + data.getDarkOneKills()), false);
        return 1;
    }

    private static int handleDebugRegistry(CommandSourceStack source) {
        UniqueRoleRegistry registry = UniqueRoleRegistry.get(source.getServer());
        source.sendSuccess(() -> Component.literal("=== Unique Role Registry ==="), false);
        source.sendSuccess(() -> Component.literal("Dark One: " + registry.getHolderName(source.getServer(), UniqueRoleRegistry.RoleType.DARK_ONE)), false);
        source.sendSuccess(() -> Component.literal("Savior: " + registry.getHolderName(source.getServer(), UniqueRoleRegistry.RoleType.SAVIOR)), false);
        source.sendSuccess(() -> Component.literal("Truest Believer: " + registry.getHolderName(source.getServer(), UniqueRoleRegistry.RoleType.TRUEST_BELIEVER)), false);
        source.sendSuccess(() -> Component.literal("Author: " + registry.getHolderName(source.getServer(), UniqueRoleRegistry.RoleType.AUTHOR)), false);
        return 1;
    }
}
