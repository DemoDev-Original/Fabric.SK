package dev.demodev.fabricsk.engine;

import java.nio.file.Path;
import java.util.List;

public final class ScriptModel {
    private ScriptModel() {}
    public record Script(Path path, List<ScriptCommand> commands, List<Statement> onLoad, List<Statement> onDamage, List<Statement> onRightClick, List<Statement> everySecond) {}
    public record ScriptCommand(String name, String argumentType, String permission, String permissionMessage,
                                List<Statement> statements, int line) {}
    public sealed interface Statement permits SendStatement, SetItemStatement, SetNameStatement,
            SetLoreStatement, GiveStatement, GiveMaterialStatement, IfStatement, ApplyEffectStatement, SetFreezeTicksStatement,
            StrikeLightningStatement, LoopEntitiesStatement, PlaySoundStatement, IgniteStatement, PushStatement, SetDamageMultiplierStatement, LoopPlayersStatement {
        int line();
    }
    public record SendStatement(String message, String recipient, int line) implements Statement {}
    public record SetItemStatement(String variable, String material, String enchantment, int level, int line) implements Statement {}
    public record SetNameStatement(String variable, String name, int line) implements Statement {}
    public record SetLoreStatement(String variable, List<String> lore, int line) implements Statement {}
    public record GiveStatement(String variable, int line) implements Statement {}
    public record GiveMaterialStatement(String material, String name, int line) implements Statement {}
    public record IfStatement(String condition, String expected, List<Statement> body, List<Statement> otherwise, int line) implements Statement {}
    public record ApplyEffectStatement(String effect, int level, int seconds, String target, int line) implements Statement {}
    public record SetFreezeTicksStatement(int ticks, String target, int line) implements Statement {}
    public record StrikeLightningStatement(String target, int line) implements Statement {}
    public record LoopEntitiesStatement(double radius, List<Statement> body, int line) implements Statement {}
    public record PlaySoundStatement(String sound, String target, int line) implements Statement {}
    public record IgniteStatement(String target, int seconds, int line) implements Statement {}
    public record PushStatement(String target, double x, double y, double z, int line) implements Statement {}
    public record SetDamageMultiplierStatement(double multiplier, int line) implements Statement {}
    public record LoopPlayersStatement(List<Statement> body, int line) implements Statement {}
}
