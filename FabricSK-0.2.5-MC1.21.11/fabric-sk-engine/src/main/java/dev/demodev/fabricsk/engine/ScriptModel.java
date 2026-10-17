package dev.demodev.fabricsk.engine;
import java.nio.file.Path;
import java.util.List;
public final class ScriptModel {
    private ScriptModel() { }
    public record Script(Path path, List<ScriptCommand> commands) { }
    public record ScriptCommand(String name, List<Statement> statements, int line) { }
    public sealed interface Statement permits SendStatement { int line(); }
    public record SendStatement(String message, boolean toPlayer, int line) implements Statement { }
}
