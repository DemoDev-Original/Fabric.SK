package dev.demodev.fabricsk.engine;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.demodev.fabricsk.engine.ScriptModel.*;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionResult;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.core.component.DataComponents;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** Script discovery, parsing, runtime execution, and dynamic command registration. */
public final class ScriptManager {
    public record OperationResult(boolean success, int count, String message) {}
    public record ScriptInfo(String name, int commands, String type) {}

    private final Path configDirectory = FabricLoader.getInstance().getConfigDir().toAbsolutePath().normalize();
    private final Path scriptsDirectory = configDirectory.resolve("fabricsk/scripts").normalize();
    private final Path errorLog = FabricLoader.getInstance().getGameDir().resolve("fabricsk/logs/script-errors.log").toAbsolutePath().normalize();
    private final Path configFile = configDirectory.resolve("fabricsk.json");
    private final SyntaxRegistry syntaxRegistry = new SyntaxRegistry();
    private final Map<Path, Script> scripts = new LinkedHashMap<>();
    private final Map<String, ScriptCommand> commands = new LinkedHashMap<>();
    private final Map<String, String> errors = new LinkedHashMap<>();
    private final Map<String, String> syntaxCorrectionModes = new LinkedHashMap<>();
    private final Map<String, ItemStack> variables = new HashMap<>();
    private final Set<String> registeredCommandRoots = new HashSet<>();
    private boolean autoLoadScripts = true, enableHeavySk = true, enableErrorLogging = true;
    private MinecraftServer activeServer;

    public synchronized void initialize() {
        try {
            Files.createDirectories(scriptsDirectory);
            Files.createDirectories(errorLog.getParent());
            writeDefaultConfigIfMissing(); readConfig(); createExampleIfMissing();
        } catch (IOException e) { System.err.println("[Fabric.SK Engine] Could not initialize files: " + e.getMessage()); }
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            synchronized (ScriptManager.this) { activeServer = server; }
            runAllLoadEvents(server);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> { synchronized (ScriptManager.this) { if(activeServer==server) activeServer=null; } });
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (!(entity.level() instanceof net.minecraft.server.level.ServerLevel)) return;
            ServerPlayer attacker = source.getEntity() instanceof ServerPlayer p ? p : null;
            MinecraftServer server = ((net.minecraft.server.level.ServerLevel) entity.level()).getServer();
            if (server == null) return;
            List<Script> snapshot;
            synchronized (ScriptManager.this) { snapshot = List.copyOf(scripts.values()); }
            for (Script script : snapshot) {
                if (!script.onDamage().isEmpty()) {
                    try { executeStatements(script.onDamage(), new ExecutionContext(server.createCommandSourceStack(), attacker, attacker, entity, null, null, null, damageTaken)); }
                    catch (RuntimeException ex) { recordRuntimeError(script.path(), ex); }
                }
            }
        });
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
            MinecraftServer server = ((net.minecraft.server.level.ServerLevel) world).getServer();
            List<Script> snapshot; synchronized (ScriptManager.this) { snapshot = List.copyOf(scripts.values()); }
            for (Script script : snapshot) if (!script.onRightClick().isEmpty()) {
                try { executeStatements(script.onRightClick(), new ExecutionContext(server.createCommandSourceStack(), serverPlayer, serverPlayer, null, null, null)); }
                catch (RuntimeException ex) { recordRuntimeError(script.path(), ex); }
            }
            return InteractionResult.PASS;
        });
        OperationResult result = autoLoadScripts ? reload() : new OperationResult(true,0,"Automatic script loading is disabled in config.");
        System.out.println("[Fabric.SK Engine] Script manager ready: " + result.message());
    }

    private void writeDefaultConfigIfMissing() throws IOException {
        if (!Files.exists(configFile)) Files.writeString(configFile,
                "{\n  \"version\": \"0.2.5-ALPHA\",\n  \"autoLoadScripts\": true,\n  \"enableHeavySk\": true,\n  \"enableErrorLogging\": true\n}\n",
                StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
    }
    private void readConfig() throws IOException {
        String json=Files.readString(configFile,StandardCharsets.UTF_8);
        autoLoadScripts=readBoolean(json,"autoLoadScripts",true);
        enableHeavySk=readBoolean(json,"enableHeavySk",true);
        enableErrorLogging=readBoolean(json,"enableErrorLogging",true);
    }
    private boolean readBoolean(String json,String key,boolean fallback) {
        var m=Pattern.compile("\""+Pattern.quote(key)+"\"\\s*:\\s*(true|false)",Pattern.CASE_INSENSITIVE).matcher(json);
        return m.find()?Boolean.parseBoolean(m.group(1)):fallback;
    }
    private void createExampleIfMissing() throws IOException {
        Path example=scriptsDirectory.resolve("example/example.sk"); Files.createDirectories(example.getParent());
        if(!Files.exists(example)) Files.writeString(example,"command /hello:\n    trigger:\n        send \"Hello, %player%!\" to player\n",StandardCharsets.UTF_8);
    }

    public synchronized OperationResult reload() {
        scripts.clear(); commands.clear(); errors.clear();
        try {
            Files.createDirectories(scriptsDirectory);
            try(var paths=Files.list(scriptsDirectory)) {
                for(Path p:paths.filter(Files::isRegularFile)
                        .filter(p->p.getFileName().toString().endsWith(".sk")||(enableHeavySk&&p.getFileName().toString().endsWith(".heavysk")))
                        .sorted().toList()) parsePath(p);
            }
        } catch(IOException e) { return new OperationResult(false,0,"Could not scan scripts: "+e.getMessage()); }
        if(activeServer!=null) runAllLoadEvents(activeServer);
        return new OperationResult(errors.isEmpty(),scripts.size(),"Loaded "+scripts.size()+" script(s)/package(s), registered "+commands.size()+" command(s), "+errors.size()+" error(s).");
    }

    public synchronized OperationResult load(String name) {
        if(name==null||name.isBlank()) return new OperationResult(false,0,"Specify a script path, e.g. example/example.sk.");
        if(name.equalsIgnoreCase("-example-/example.sk")) name="example/example.sk";
        String filename=name.replace('\\','/');
        if(!filename.endsWith(".sk")&&!filename.endsWith(".heavysk")) filename+=".sk";
        Path path=scriptsDirectory.resolve(filename).normalize();
        if(!path.startsWith(scriptsDirectory)||path.equals(scriptsDirectory)) return new OperationResult(false,0,"Invalid script path. Paths must stay inside config/fabricsk/scripts.");
        if(!Files.isRegularFile(path)) return new OperationResult(false,0,"Script not found: "+filename);
        if(filename.endsWith(".heavysk")&&!enableHeavySk) return new OperationResult(false,0,"Heavy Skript packages are disabled in config/fabricsk.json.");
        Script previous=scripts.get(path); removeLoadedPath(path); errors.remove(path.toString());
        boolean ok=parsePath(path);
        if(!ok && previous!=null) { scripts.put(path,previous); for(ScriptCommand c:previous.commands()) commands.put(c.name().toLowerCase(Locale.ROOT),c); }
        if(ok && activeServer!=null) runLoadEvent(scripts.get(path),activeServer);
        return new OperationResult(ok,ok?1:0,ok?"Loaded "+scriptsDirectory.relativize(path).toString().replace('\\','/')+".":errors.getOrDefault(path.toString(),"Load failed."));
    }

    public synchronized OperationResult unload(String name) {
        if(name==null||name.isBlank()) return new OperationResult(false,0,"Specify a loaded script name.");
        String filename=name.replace('\\','/'); Path path=scriptsDirectory.resolve(filename).normalize();
        if(!path.startsWith(scriptsDirectory)) return new OperationResult(false,0,"Invalid script path.");
        if(!scripts.containsKey(path)&&!filename.endsWith(".sk")&&!filename.endsWith(".heavysk")) {
            Path sk=scriptsDirectory.resolve(filename+".sk").normalize(), heavy=scriptsDirectory.resolve(filename+".heavysk").normalize();
            if(scripts.containsKey(sk)) path=sk; else if(scripts.containsKey(heavy)) path=heavy;
        }
        if(!scripts.containsKey(path)) return new OperationResult(false,0,"That script is not currently loaded: "+filename);
        removeLoadedPath(path); errors.remove(path.toString());
        return new OperationResult(true,1,"Unloaded "+scriptsDirectory.relativize(path).toString().replace('\\','/')+".");
    }
    private void removeLoadedPath(Path path) {
        Script old=scripts.remove(path);
        if(old!=null) for(ScriptCommand c:old.commands()) {
            String key=c.name().toLowerCase(Locale.ROOT);
            boolean used=scripts.values().stream().flatMap(s->s.commands().stream()).anyMatch(x->x.name().equalsIgnoreCase(c.name()));
            if(!used) commands.remove(key);
        }
    }

    private boolean parsePath(Path path) {
        try {
            List<ScriptCommand> parsedCommands=new ArrayList<>(); List<Statement> onLoad=new ArrayList<>(), onDamage=new ArrayList<>(), onRightClick=new ArrayList<>();
            if(path.getFileName().toString().endsWith(".heavysk")) {
                try(ZipFile zip=new ZipFile(path.toFile())) {
                    List<? extends ZipEntry> entries=Collections.list(zip.entries()).stream().filter(e->!e.isDirectory()&&e.getName().toLowerCase(Locale.ROOT).endsWith(".sk")).sorted(Comparator.comparing(ZipEntry::getName)).toList();
                    if(entries.isEmpty()) throw new IOException("Package contains no .sk files.");
                    for(ZipEntry entry:entries) try(var in=zip.getInputStream(entry)) {
                        Script s=new Parser().parse(path.resolve(entry.getName()),new String(in.readAllBytes(),StandardCharsets.UTF_8));
                        parsedCommands.addAll(s.commands()); onLoad.addAll(s.onLoad()); onDamage.addAll(s.onDamage()); onRightClick.addAll(s.onRightClick());
                    }
                }
            } else {
                Script s=new Parser().parse(path,Files.readString(path,StandardCharsets.UTF_8));
                parsedCommands.addAll(s.commands()); onLoad.addAll(s.onLoad()); onDamage.addAll(s.onDamage()); onRightClick.addAll(s.onRightClick());
            }
            Set<String> local=new HashSet<>();
            for(ScriptCommand c:parsedCommands) {
                String key=c.name().toLowerCase(Locale.ROOT);
                if(!local.add(key)||(commands.containsKey(key)&&scripts.values().stream().noneMatch(s->s.path().equals(path)&&s.commands().stream().anyMatch(old->old.name().equalsIgnoreCase(c.name())))))
                    throw new ScriptSyntaxException(c.line(),1,"Duplicate scripted command /"+c.name()+".");
            }
            Script script=new Script(path,List.copyOf(parsedCommands),List.copyOf(onLoad),List.copyOf(onDamage),List.copyOf(onRightClick));
            scripts.put(path,script); for(ScriptCommand c:parsedCommands) commands.put(c.name().toLowerCase(Locale.ROOT),c);
            return true;
        } catch(IOException|ScriptSyntaxException e) {
            String msg=e instanceof ScriptSyntaxException se?"Line "+se.getLine()+", column "+se.getColumn()+": "+se.getMessage():e.getMessage();
            errors.put(path.toString(),msg); logError(path,msg); System.err.println("[Fabric.SK] "+path.getFileName()+": "+msg); return false;
        }
    }

    private void runAllLoadEvents(MinecraftServer server) {
        List<Script> snapshot; synchronized(this){snapshot=List.copyOf(scripts.values());}
        for(Script s:snapshot) runLoadEvent(s,server);
    }
    private void runLoadEvent(Script script,MinecraftServer server) {
        if(script==null||script.onLoad().isEmpty()) return;
        try { executeStatements(script.onLoad(),new ExecutionContext(server.createCommandSourceStack(),null,null,null,null,null)); }
        catch(RuntimeException ex) { recordRuntimeError(script.path(),ex); }
    }

    private void executeStatements(List<Statement> statements,ExecutionContext ctx) {
        for(Statement st:statements) {
            if(st instanceof SendStatement s) {
                String message=Expression.interpolate(s.message(),ctx);
                if(s.recipient().equals("player")&&ctx.player()!=null) ctx.player().sendSystemMessage(Component.literal(message));
                else if(s.recipient().equals("victim")&&ctx.victim() instanceof ServerPlayer victimPlayer) victimPlayer.sendSystemMessage(Component.literal(message));
                else ctx.source().sendSuccess(()->Component.literal(message),false);
            } else if(st instanceof SetItemStatement s) {
                var itemHolder=BuiltInRegistries.ITEM.get(Identifier.withDefaultNamespace(s.material().toLowerCase(Locale.ROOT)));
                if(itemHolder.isEmpty() || itemHolder.get().value()==Items.AIR)
                    throw new IllegalArgumentException("Unknown item material: "+s.material());
                ItemStack stack=new ItemStack(itemHolder.get());
                if(!s.enchantment().isBlank()&&s.level()>0) {
                    var key=Identifier.withDefaultNamespace(s.enchantment().toLowerCase(Locale.ROOT));
                    var holder=ctx.source().getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(key);
                    if(holder.isEmpty()) throw new IllegalArgumentException("Unknown enchantment: "+s.enchantment());
                    stack.enchant(holder.get(),s.level());
                }
                synchronized(this){variables.put(s.variable().toLowerCase(Locale.ROOT),stack);}
            } else if(st instanceof SetNameStatement s) {
                ItemStack stack=variable(s.variable()); stack.set(DataComponents.CUSTOM_NAME,Component.literal(color(s.name())));
            } else if(st instanceof SetLoreStatement s) {
                ItemStack stack=variable(s.variable()); List<Component> lore=new ArrayList<>(); for(String line:s.lore()) lore.add(Component.literal(color(line)));
                stack.set(DataComponents.LORE,new ItemLore(lore));
            } else if(st instanceof GiveStatement s) {
                if(ctx.player()==null) throw new IllegalArgumentException("give player requires a player command context.");
                ItemStack stack=variable(s.variable()).copy(); if(!ctx.player().getInventory().add(stack)) ctx.player().drop(stack,false);
            } else if(st instanceof GiveMaterialStatement s) {
                if(ctx.player()==null) throw new IllegalArgumentException("give player requires a player command context.");
                var found=BuiltInRegistries.ITEM.get(Identifier.withDefaultNamespace(s.material().toLowerCase(Locale.ROOT)));
                if(found.isEmpty() || found.get().value()==Items.AIR) throw new IllegalArgumentException("Unknown item material: "+s.material());
                ItemStack stack=new ItemStack(found.get());
                if(!s.name().isBlank()) stack.set(DataComponents.CUSTOM_NAME,Component.literal(color(s.name())));
                if(!ctx.player().getInventory().add(stack)) ctx.player().drop(stack,false);
            } else if(st instanceof LoopEntitiesStatement s) {
                if(ctx.player()==null) throw new IllegalArgumentException("Entity radius loops require a player context.");
                var level=(net.minecraft.server.level.ServerLevel)ctx.player().level();
                AABB area=ctx.player().getBoundingBox().inflate(s.radius());
                for(Entity entity: level.getEntitiesOfClass(Entity.class,area,e->e.isAlive())) {
                    executeStatements(s.body(),ctx.withLoopEntity(entity));
                }
            } else if(st instanceof PlaySoundStatement s) {
                if(ctx.player()==null) throw new IllegalArgumentException("play sound requires a player context.");
                var id=Identifier.tryParse(s.sound());
                if(id==null) id=Identifier.withDefaultNamespace(s.sound().toLowerCase(Locale.ROOT));
                var sound=BuiltInRegistries.SOUND_EVENT.get(id);
                if(sound.isEmpty()) throw new IllegalArgumentException("Unknown sound: "+s.sound());
                ctx.player().level().playSound(null, ctx.player().blockPosition(), sound.get().value(), SoundSource.PLAYERS, 1.0F, 1.0F);
            } else if(st instanceof IgniteStatement s) {
                Entity target=entityTarget(s.target(),ctx); target.igniteForSeconds((float)s.seconds());
            } else if(st instanceof PushStatement s) {
                Entity target=entityTarget(s.target(),ctx);
                target.setDeltaMovement(target.getDeltaMovement().x,s.y(),target.getDeltaMovement().z);
            } else if(st instanceof SetDamageMultiplierStatement s) {
                // AFTER_DAMAGE is a post-hit event. Apply only the bonus portion as a follow-up hit.
                if(ctx.victim()==null) throw new IllegalArgumentException("Damage multiplier is only valid in on damage.");
                if(s.multiplier()>1.0) {
                    var victim=ctx.victim();
                    if(victim.level() instanceof net.minecraft.server.level.ServerLevel level) {
                        float bonus=(float)Math.min(2048.0, Math.max(0.0, ctx.damageTaken()*(s.multiplier()-1.0)));
                        if(bonus>0) victim.hurtServer(level,victim.damageSources().magic(),bonus);
                    }
                }
            } else if(st instanceof IfStatement s) {
                if(testCondition(s.condition(),ctx)) executeStatements(s.body(),ctx); else executeStatements(s.otherwise(),ctx);
            } else if(st instanceof ApplyEffectStatement s) {
                LivingEntity target=target(s.target(),ctx);
                var effect=s.effect().toLowerCase(Locale.ROOT);
                if(effect.equals("slowness")||effect.equals("movement_slowdown"))
                    target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS,s.seconds()*20,Math.max(0,s.level()-1),false,false,false));
                else if(effect.equals("blindness")) target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,s.seconds()*20,Math.max(0,s.level()-1),false,false,false));
                else if(effect.equals("weakness")) target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,s.seconds()*20,Math.max(0,s.level()-1),false,false,false));
                else if(effect.equals("instant_damage")||effect.equals("instant_damage_effect"))
                    target.hurtServer((net.minecraft.server.level.ServerLevel)target.level(),target.damageSources().magic(),Math.max(1,Math.min(1024,6 * (1 << Math.min(7,Math.max(0,s.level()-1))))));
                else throw new IllegalArgumentException("Unsupported effect: "+s.effect());
            } else if(st instanceof SetFreezeTicksStatement s) target(s.target(),ctx).setTicksFrozen(s.ticks());
            else if(st instanceof StrikeLightningStatement s) {
                LivingEntity target=s.target().equals("arg-1")?ctx.argumentPlayer():ctx.player();
                if(target==null) throw new IllegalArgumentException("Lightning target is not available.");
                var level=(net.minecraft.server.level.ServerLevel)target.level();
                var bolt=EntityType.LIGHTNING_BOLT.create(level,EntitySpawnReason.COMMAND);
                if(bolt==null) throw new IllegalArgumentException("Could not create lightning entity.");
                bolt.setPos(target.getX(),target.getY(),target.getZ()); level.addFreshEntity(bolt);
            }
        }
    }
    private ItemStack variable(String name) {
        ItemStack stack=variables.get(name.toLowerCase(Locale.ROOT));
        if(stack==null) throw new IllegalArgumentException("Unknown item variable {"+name+"}.");
        return stack;
    }
    private static String color(String s){return s.replace('&','§');}
    private static String stripColors(String s){return s.replaceAll("(?i)§[0-9A-FK-OR]","");}
    private static Entity entityTarget(String target,ExecutionContext ctx) {
        Entity e=switch(target) { case "victim" -> ctx.victim(); case "loop-entity" -> ctx.loopEntity(); default -> ctx.player(); };
        if(e==null) throw new IllegalArgumentException("Target '"+target+"' is not available in this context."); return e;
    }
    private static LivingEntity target(String target,ExecutionContext ctx) {
        if(target.equals("loop-entity") && ctx.loopEntity() instanceof LivingEntity living) return living;
        LivingEntity e=target.equals("victim")?ctx.victim():ctx.player();
        if(e==null) throw new IllegalArgumentException("Target '"+target+"' is not available in this context."); return e;
    }
    private boolean testCondition(String condition,ExecutionContext ctx) {
        String c=condition.toLowerCase(Locale.ROOT).trim();
        if(c.equals("attacker is a player")) return ctx.attacker()!=null;
        if(c.equals("arg-1 is set")) return ctx.argumentName()!=null;
        if(c.equals("arg-1 is online")) return ctx.argumentName()!=null && ctx.argumentPlayer()!=null;
        if(c.equals("loop-entity is not player")) return ctx.loopEntity()!=null && !(ctx.loopEntity() instanceof ServerPlayer);
        var health=java.util.regex.Pattern.compile("(?i)^health of victim is less than or equal to ([0-9]+(?:\\.[0-9]+)?)$").matcher(condition.trim());
        if(health.matches() && ctx.victim()!=null) return ctx.victim().getHealth()<=Double.parseDouble(health.group(1));
        var playerTool=java.util.regex.Pattern.compile("(?i)^name of player'?s tool is [\"'](.+)[\"']$").matcher(condition.trim());
        if(playerTool.matches() && ctx.player()!=null) {
            ItemStack held=ctx.player().getMainHandItem(); Component name=held.get(DataComponents.CUSTOM_NAME);
            String actual=name==null?held.getHoverName().getString():name.getString();
            return stripColors(actual).trim().equals(stripColors(color(playerTool.group(1))).trim());
        }
        var m=java.util.regex.Pattern.compile("(?i)^name of attacker'?s tool is [\"'](.+)[\"']$").matcher(condition.trim());
        if(m.matches()&&ctx.attacker()!=null) {
            ItemStack held=ctx.attacker().getMainHandItem();
            Component name=held.get(DataComponents.CUSTOM_NAME);
            String actual=name==null?held.getHoverName().getString():name.getString();
            return stripColors(actual).trim().equals(stripColors(color(m.group(1))).trim());
        }
        return false;
    }
    private void recordRuntimeError(Path path,RuntimeException e) {
        String msg="Runtime error: "+(e.getMessage()==null?e.getClass().getSimpleName():e.getMessage());
        synchronized(this){errors.put(path.toString(),msg);} logError(path,msg);
        System.err.println("[Fabric.SK] "+path.getFileName()+": "+msg);
    }

    private void logError(Path path,String message) {
        if(!enableErrorLogging)return;
        try { Files.createDirectories(errorLog.getParent()); Files.writeString(errorLog,"["+LocalDateTime.now()+"] [Fabric.SK] FSK001\nFile: "+path+"\n"+message+"\n\n",StandardCharsets.UTF_8,StandardOpenOption.CREATE,StandardOpenOption.APPEND); }
        catch(IOException e){System.err.println("[Fabric.SK] Could not write error log: "+e.getMessage());}
    }

    public synchronized OperationResult parseAll() {
        int good=0; errors.clear();
        for(Path p:scriptFilesForValidation()) try {
            if(p.toString().endsWith(".heavysk")) try(ZipFile zip=new ZipFile(p.toFile())) {
                List<? extends ZipEntry> es=Collections.list(zip.entries()).stream().filter(e->!e.isDirectory()&&e.getName().endsWith(".sk")).toList();
                if(es.isEmpty())throw new IOException("Package contains no .sk files.");
                for(ZipEntry e:es)try(var in=zip.getInputStream(e)){new Parser().parse(p.resolve(e.getName()),new String(in.readAllBytes(),StandardCharsets.UTF_8));}
            } else new Parser().parse(p,Files.readString(p,StandardCharsets.UTF_8));
            good++;
        } catch(Exception e){errors.put(p.toString(),e.getMessage());}
        return new OperationResult(errors.isEmpty(),good,"Parsed "+good+" script(s)/package(s); "+errors.size()+" error(s)."+(errors.isEmpty()?"":" First error: "+errors.values().iterator().next()));
    }
    public synchronized OperationResult validateAll() {
        OperationResult r=parseAll(); return r.success()?new OperationResult(true,r.count(),"Validation passed for "+r.count()+" script(s)/package(s)."):new OperationResult(false,r.count(),"Validation failed. "+r.message());
    }
    public synchronized OperationResult validate(String name) {
        if(name==null||name.isBlank())return validateAll();
        String f=name.replace('\\','/');if(!f.endsWith(".sk")&&!f.endsWith(".heavysk"))f+=".sk";
        Path p=scriptsDirectory.resolve(f).normalize();
        if(!p.startsWith(scriptsDirectory)||!Files.isRegularFile(p))return new OperationResult(false,0,"Script not found: "+f);
        try {
            if(f.endsWith(".heavysk"))try(ZipFile zip=new ZipFile(p.toFile())) {
                List<? extends ZipEntry> es=Collections.list(zip.entries()).stream().filter(e->!e.isDirectory()&&e.getName().endsWith(".sk")).toList();
                if(es.isEmpty())return new OperationResult(false,0,"Package contains no .sk files.");
                for(ZipEntry e:es)try(var in=zip.getInputStream(e)){new Parser().parse(p.resolve(e.getName()),new String(in.readAllBytes(),StandardCharsets.UTF_8));}
            } else new Parser().parse(p,Files.readString(p,StandardCharsets.UTF_8));
            errors.remove(p.toString());return new OperationResult(true,1,"Validation passed for "+f+".");
        } catch(Exception e){errors.put(p.toString(),e.getMessage());logError(p,e.getMessage());return new OperationResult(false,0,"Validation failed for "+f+": "+e.getMessage());}
    }
    private List<Path> scriptFilesForValidation() {
        try(var paths=Files.walk(scriptsDirectory)){return paths.filter(Files::isRegularFile).filter(p->p.toString().endsWith(".sk")||(enableHeavySk&&p.toString().endsWith(".heavysk"))).sorted().toList();}
        catch(IOException e){return List.of();}
    }

    public synchronized void registerScriptCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        for(String key:commands.keySet()) {
            if(!registeredCommandRoots.contains(key) && dispatcher.getRoot().getChild(key)!=null) {
                errors.put("command:/"+key,"Command name conflicts with a Minecraft or another mod command; rename it.");
                continue;
            }
            ScriptCommand scriptCommand=commands.get(key);
            var literal=Commands.literal(key).executes(ctx->runCommand(ctx.getSource(),key,null));
            if(scriptCommand!=null && !scriptCommand.argumentType().isEmpty())
                literal.then(Commands.argument("arg-1",StringArgumentType.word()).executes(ctx->runCommand(ctx.getSource(),key,StringArgumentType.getString(ctx,"arg-1"))));
            if (!registeredCommandRoots.contains(key)) dispatcher.register(literal);
            registeredCommandRoots.add(key);
        }
    }
    private boolean hasCommandPermission(CommandSourceStack source) {
        // In Minecraft 1.21.11, CommandSourceStack no longer exposes hasPermission(int).
        // Treat console as trusted and require operator status for player-issued
        // commands that declare a permission until a Fabric permission bridge is configured.
        ServerPlayer player;
        try { player = source.getPlayer(); } catch (Exception ignored) { player = null; }
        return player == null || source.getServer().getPlayerList().isOp(new NameAndId(player.getGameProfile()));
    }

    private int runCommand(CommandSourceStack source,String key,String argName) {
        ScriptCommand cmd; synchronized(this){cmd=commands.get(key);}
        if(cmd==null){source.sendFailure(Component.literal("That Fabric.SK command is no longer loaded."));return 0;}
        ServerPlayer argPlayer=argName==null?null:source.getServer().getPlayerList().getPlayerByName(argName);
        if(!cmd.permission().isBlank()&&!hasCommandPermission(source)) {
            source.sendFailure(Component.literal(cmd.permissionMessage().isBlank()?"You do not have permission to use this command!":color(cmd.permissionMessage())));return 0;
        }
        ServerPlayer player;
        try{player=source.getPlayer();}catch(Exception e){player=null;}
        ExecutionContext context=new ExecutionContext(source,player,player,null,argPlayer,argName);
        try{executeStatements(cmd.statements(),context);return 1;}
        catch(RuntimeException e){source.sendFailure(Component.literal("Fabric.SK script error: "+e.getMessage()));return 0;}
    }

    public synchronized List<String> getLoadedScripts(){return scripts.keySet().stream().map(p->scriptsDirectory.relativize(p).toString().replace('\\','/')).toList();}
    public synchronized List<ScriptInfo> getScriptInfo(){return scripts.entrySet().stream().map(e->new ScriptInfo(scriptsDirectory.relativize(e.getKey()).toString().replace('\\','/'),e.getValue().commands().size(),e.getKey().toString().endsWith(".heavysk")?"Heavy Skript package":"Skript file")).toList();}
    public synchronized String info(String name) {
        String n=name.replace('\\','/');if(!n.endsWith(".sk")&&!n.endsWith(".heavysk"))n+=".sk";
        for(ScriptInfo i:getScriptInfo())if(i.name().equalsIgnoreCase(n))return i.name()+" | "+i.type()+" | commands: "+i.commands();
        return "Script is not loaded: "+n;
    }
    public synchronized Map<String,String> getErrors(){return Collections.unmodifiableMap(new LinkedHashMap<>(errors));}
    public synchronized OperationResult setSyntaxCorrection(String mode,String script) {
        if(!mode.equalsIgnoreCase("auto")&&!mode.equalsIgnoreCase("partial"))return new OperationResult(false,0,"Mode must be auto or partial.");
        syntaxCorrectionModes.put(script.replace('\\','/'),mode.toLowerCase(Locale.ROOT));
        return new OperationResult(true,1,"Syntax-correction preference set to "+mode.toLowerCase(Locale.ROOT)+" for "+script+". The correction engine is experimental; this setting does not rewrite scripts yet.");
    }
    public Path getScriptsDirectory(){return scriptsDirectory;} public Path getErrorLog(){return errorLog;}
    public synchronized int getCommandCount(){return commands.size();} public synchronized int getSyntaxCount(){return syntaxRegistry.size();}
}
