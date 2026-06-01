package com.demodev.fabricsk.command;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

public final class FabricSkriptCommand {

    private FabricSkriptCommand() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                registerRoot(dispatcher);

                dispatcher.register(
                    literal("skript").redirect(
                        dispatcher.getRoot().getChild("fabricskript")
                    )
                );

                dispatcher.register(
                    literal("fsk").redirect(
                        dispatcher.getRoot().getChild("fabricskript")
                    )
                );

                dispatcher.register(
                    literal("sk").redirect(
                        dispatcher.getRoot().getChild("fabricskript")
                    )
                );
            }
        );
    }

    private static void registerRoot(
        CommandDispatcher<ServerCommandSource> dispatcher
    ) {
        dispatcher.register(
            literal("fabricskript")
                .then(literal("help").executes(ctx -> help(ctx.getSource())))

                .then(
                    literal("version").executes(ctx -> version(ctx.getSource()))
                )

                .then(literal("list").executes(ctx -> list(ctx.getSource())))

                .then(
                    literal("reloadall").executes(ctx ->
                        reloadAll(ctx.getSource())
                    )
                )

                .then(
                    literal("errors").executes(ctx -> errors(ctx.getSource()))
                )

                .then(
                    literal("reload").then(
                        argument(
                            "script",
                            StringArgumentType.greedyString()
                        ).executes(ctx ->
                            reload(
                                ctx.getSource(),
                                StringArgumentType.getString(ctx, "script")
                            )
                        )
                    )
                )

                .then(
                    literal("load").then(
                        argument(
                            "script",
                            StringArgumentType.greedyString()
                        ).executes(ctx ->
                            load(
                                ctx.getSource(),
                                StringArgumentType.getString(ctx, "script")
                            )
                        )
                    )
                )

                .then(
                    literal("unload").then(
                        argument(
                            "script",
                            StringArgumentType.greedyString()
                        ).executes(ctx ->
                            unload(
                                ctx.getSource(),
                                StringArgumentType.getString(ctx, "script")
                            )
                        )
                    )
                )

                .then(
                    literal("validate").then(
                        argument(
                            "script",
                            StringArgumentType.greedyString()
                        ).executes(ctx ->
                            validate(
                                ctx.getSource(),
                                StringArgumentType.getString(ctx, "script")
                            )
                        )
                    )
                )

                .then(
                    literal("info").then(
                        argument(
                            "script",
                            StringArgumentType.greedyString()
                        ).executes(ctx ->
                            info(
                                ctx.getSource(),
                                StringArgumentType.getString(ctx, "script")
                            )
                        )
                    )
                )

                .then(
                    literal("toggle").then(
                        argument("feature", StringArgumentType.word()).then(
                            argument("mode", StringArgumentType.word()).then(
                                argument(
                                    "script",
                                    StringArgumentType.greedyString()
                                ).executes(ctx ->
                                    toggle(
                                        ctx.getSource(),
                                        StringArgumentType.getString(
                                            ctx,
                                            "feature"
                                        ),
                                        StringArgumentType.getString(
                                            ctx,
                                            "mode"
                                        ),
                                        StringArgumentType.getString(
                                            ctx,
                                            "script"
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
        );
    }

    private static int help(ServerCommandSource source) {
        source.sendFeedback(
            () ->
                net.minecraft.text.Text.literal(
                    """
                    Fabric.SK Commands

                    /fsk help
                    /fsk version
                    /fsk list
                    /fsk reload <script>
                    /fsk reloadall
                    /fsk load <script>
                    /fsk unload <script>
                    /fsk validate <script>
                    /fsk info <script>
                    /fsk errors
                    /fsk toggle syntaxcorrect <auto|partial> <script>
                    """
                ),
            false
        );

        return 1;
    }

    private static int version(ServerCommandSource source) {
        source.sendFeedback(
            () ->
                net.minecraft.text.Text.literal(
                    "--- Fabric.SK Version/Info ---"
                ),
            false
        );

        source.sendFeedback(
            () -> net.minecraft.text.Text.literal("Fabric.SK: 0.2.0"),
            false
        );

        source.sendFeedback(
            () -> net.minecraft.text.Text.literal("Author: DemoDev"),
            false
        );

        return 1;
    }

    private static int list(ServerCommandSource source) {
        source.sendFeedback(
            () ->
                net.minecraft.text.Text.literal(
                    "Script listing not implemented yet."
                ),
            false
        );

        return 1;
    }

    private static int reload(ServerCommandSource source, String script) {
        source.sendFeedback(
            () ->
                net.minecraft.text.Text.literal("Reloading script: " + script),
            false
        );

        return 1;
    }

    private static int load(ServerCommandSource source, String script) {
        source.sendFeedback(
            () -> net.minecraft.text.Text.literal("Loading script: " + script),
            false
        );

        return 1;
    }

    private static int unload(ServerCommandSource source, String script) {
        source.sendFeedback(
            () ->
                net.minecraft.text.Text.literal("Unloading script: " + script),
            false
        );

        return 1;
    }

    private static int validate(ServerCommandSource source, String script) {
        source.sendFeedback(
            () ->
                net.minecraft.text.Text.literal("Validating script: " + script),
            false
        );

        return 1;
    }

    private static int info(ServerCommandSource source, String script) {
        source.sendFeedback(
            () -> net.minecraft.text.Text.literal("Information for: " + script),
            false
        );

        return 1;
    }

    private static int errors(ServerCommandSource source) {
        source.sendFeedback(
            () -> net.minecraft.text.Text.literal("Recent script errors."),
            false
        );

        return 1;
    }

    private static int reloadAll(ServerCommandSource source) {
        source.sendFeedback(
            () -> net.minecraft.text.Text.literal("Reloading all scripts..."),
            false
        );

        return 1;
    }

    private static int toggle(
        ServerCommandSource source,
        String feature,
        String mode,
        String script
    ) {
        source.sendFeedback(
            () ->
                net.minecraft.text.Text.literal(
                    "Feature " + feature + " set to " + mode + " for " + script
                ),
            false
        );

        return 1;
    }
}
