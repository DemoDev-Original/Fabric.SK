package dev.demodev.fabricsk;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.demodev.fabricsk.engine.FabricSKEngine;
import dev.demodev.fabricsk.engine.ScriptManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class FabricSK implements ModInitializer {
    public static final String MOD_ID = "fabricsk", VERSION = "0.2.5";
    private static final String[] ALIASES = {"skript", "sk", "fsk", "fabricskript", "fabricsk"};

    @Override public void onInitialize() {
        FabricSKEngine.getInstance().initialize();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            for (String alias : ALIASES) registerCommands(dispatcher, alias);
            FabricSKEngine.getInstance().getScriptManager().registerScriptCommands(dispatcher);
        });
        System.out.println("[Fabric.SK] Fabric.SK " + VERSION + " initialized.");
    }

    private static void registerCommands(com.mojang.brigadier.CommandDispatcher<CommandSourceStack> d, String alias) {
        var root = Commands.literal(alias).executes(c -> { version(c.getSource()); return 1; })
            .then(Commands.literal("version").executes(c -> { version(c.getSource()); return 1; }))
            .then(Commands.literal("help").executes(c -> { help(c.getSource()); return 1; }))
            .then(Commands.literal("reloadall").executes(c -> { var e=FabricSKEngine.getInstance(); var r=e.reload(); refreshScriptCommands(c.getSource()); report(c.getSource(),r); return 1; }))
            .then(Commands.literal("reload").executes(c -> { var e=FabricSKEngine.getInstance(); var r=e.reload(); refreshScriptCommands(c.getSource()); report(c.getSource(),r); return 1; })
                .then(Commands.argument("script", StringArgumentType.greedyString()).executes(c -> { var e=FabricSKEngine.getInstance(); var r=e.load(StringArgumentType.getString(c,"script")); if(r.success()) refreshScriptCommands(c.getSource()); report(c.getSource(),r); return 1; })))
            .then(Commands.literal("load").then(Commands.argument("script", StringArgumentType.greedyString()).executes(c -> { var e=FabricSKEngine.getInstance(); var r=e.load(StringArgumentType.getString(c,"script")); if(r.success()) refreshScriptCommands(c.getSource()); report(c.getSource(),r); return 1; })))
            .then(Commands.literal("unload").then(Commands.argument("script", StringArgumentType.greedyString()).executes(c -> { var e=FabricSKEngine.getInstance(); var r=e.getScriptManager().unload(StringArgumentType.getString(c,"script")); refreshScriptCommands(c.getSource()); report(c.getSource(),r); return 1; })))
            .then(Commands.literal("list").executes(c -> { list(c.getSource()); return 1; }))
            .then(Commands.literal("info").then(Commands.argument("script", StringArgumentType.greedyString()).executes(c -> { String info=FabricSKEngine.getInstance().getScriptManager().info(StringArgumentType.getString(c,"script")); c.getSource().sendSuccess(()->Component.literal("[Fabric.SK] "+info),false); return 1; })))
            .then(Commands.literal("parse").executes(c -> { report(c.getSource(),FabricSKEngine.getInstance().parse()); return 1; }))
            .then(Commands.literal("validate").executes(c -> { report(c.getSource(),FabricSKEngine.getInstance().validate()); return 1; })
                .then(Commands.argument("script",StringArgumentType.greedyString()).executes(c -> { report(c.getSource(),FabricSKEngine.getInstance().getScriptManager().validate(StringArgumentType.getString(c,"script"))); return 1; })))
            .then(Commands.literal("errors").executes(c -> { errors(c.getSource()); return 1; }))
            .then(Commands.literal("toggle").then(Commands.literal("syntaxcorrect")
                .then(Commands.literal("auto").then(Commands.argument("script",StringArgumentType.greedyString()).executes(c -> { report(c.getSource(),FabricSKEngine.getInstance().getScriptManager().setSyntaxCorrection("auto",StringArgumentType.getString(c,"script"))); return 1; })))
                .then(Commands.literal("partial").then(Commands.argument("script",StringArgumentType.greedyString()).executes(c -> { report(c.getSource(),FabricSKEngine.getInstance().getScriptManager().setSyntaxCorrection("partial",StringArgumentType.getString(c,"script"))); return 1; })))))
            .then(Commands.literal("modrinth").executes(c -> { c.getSource().sendSuccess(()->Component.literal("Fabric.SK: Modrinth project page has not been configured yet."),false); return 1; }))
            .then(Commands.literal("ping").executes(c -> { joke(c.getSource(),"Pong!"); return 1; }))
            .then(Commands.literal("pong").executes(c -> { joke(c.getSource(),"Ping!"); return 1; }));
        d.register(root);
    }

    private static void refreshScriptCommands(CommandSourceStack source) {
        var server = source.getServer();
        var manager = FabricSKEngine.getInstance().getScriptManager();
        manager.registerScriptCommands(server.getCommands().getDispatcher());
        // The server command tree changed at runtime; synchronize it to connected clients.
        for (var player : server.getPlayerList().getPlayers()) server.getCommands().sendCommands(player);
    }
    private static void version(CommandSourceStack s) { var e=FabricSKEngine.getInstance(); var m=e.getScriptManager(); s.sendSuccess(()->Component.literal("Fabric.SK "+VERSION+"-ALPHA | Engine "+e.getVersion()+" | scripts: "+m.getLoadedScripts().size()+" | commands: "+m.getCommandCount()),false); }
    private static void report(CommandSourceStack s, ScriptManager.OperationResult r) { s.sendSuccess(()->Component.literal("[Fabric.SK] "+r.message()),false); }
    private static void list(CommandSourceStack s) {
        var scripts=FabricSKEngine.getInstance().getScriptManager().getScriptInfo();
        s.sendSuccess(()->Component.literal("[Fabric.SK] Loaded scripts/packages: "+scripts.size()),false);
        for(var info:scripts) s.sendSuccess(()->Component.literal(" - "+info.name()+" ("+info.type()+", "+info.commands()+" command(s))"),false);
    }
    private static void errors(CommandSourceStack s) {
        Map<String,String> errors=FabricSKEngine.getInstance().getScriptManager().getErrors();
        if(errors.isEmpty()) { s.sendSuccess(()->Component.literal("[Fabric.SK] No recent parser/validation errors."),false); return; }
        s.sendSuccess(()->Component.literal("[Fabric.SK] Recent errors: "+errors.size()),false);
        errors.forEach((file,error)->s.sendSuccess(()->Component.literal(file+": "+error),false));
    }
    private static void help(CommandSourceStack s) {
        s.sendSuccess(()->Component.literal("[Fabric.SK] Commands: load <script>, unload <script>, reload [script], reloadall, list, info <script>, validate [script], parse, errors, version, help, toggle syntaxcorrect <auto|partial> <script>"),false);
    }
    private static void joke(CommandSourceStack s,String response) { s.sendSuccess(()->Component.literal("Fabric.SK says..."),false); CompletableFuture.delayedExecutor(2,TimeUnit.SECONDS).execute(()->s.getServer().execute(()->s.sendSuccess(()->Component.literal(response),false))); }
}
