package dev.demodev.fabricsk.engine;
public final class ScriptSyntaxException extends Exception {
    private final int line, column;
    public ScriptSyntaxException(int line,int column,String message){super(message);this.line=line;this.column=column;}
    public int getLine(){return line;} public int getColumn(){return column;}
}
