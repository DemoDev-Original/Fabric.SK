package dev.demodev.fabricsk.engine;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import static dev.demodev.fabricsk.engine.Lexer.Type.*;
import dev.demodev.fabricsk.engine.ScriptModel.*;

/** Parser for command /name: -> trigger: -> send "text" to player. */
public final class Parser {
    public Script parse(Path path, String source) throws ScriptSyntaxException {
        List<Lexer.Token> t = new Lexer().lex(source); int i=0; List<ScriptCommand> commands=new ArrayList<>();
        while(t.get(i).type()!=EOF){
            if(t.get(i).type()==NEWLINE){i++;continue;}
            Lexer.Token start=t.get(i);
            if(start.type()!=WORD || !start.value().equalsIgnoreCase("command")) throw err(start,"Expected 'command /name:' declaration.");
            i++; if(t.get(i).type()!=WORD || !t.get(i).value().startsWith("/")) throw err(t.get(i),"Expected command name such as /hello.");
            String name=t.get(i++).value().substring(1); if(name.isBlank()) throw err(start,"Command name cannot be empty.");
            if(t.get(i).type()!=COLON) throw err(t.get(i),"Expected ':' after command declaration."); i++;
            i=skipLine(t,i); if(t.get(i).type()!=INDENT) throw err(t.get(i),"Expected an indented command body."); i++;
            if(t.get(i).type()!=WORD || !t.get(i).value().equalsIgnoreCase("trigger")) throw err(t.get(i),"Expected 'trigger:' inside command.");
            i++; if(t.get(i).type()!=COLON) throw err(t.get(i),"Expected ':' after trigger."); i++;
            i=skipLine(t,i); if(t.get(i).type()!=INDENT) throw err(t.get(i),"Expected an indented trigger body."); i++;
            List<Statement> statements=new ArrayList<>();
            while(t.get(i).type()!=DEDENT && t.get(i).type()!=EOF){
                if(t.get(i).type()==NEWLINE){i++;continue;}
                Lexer.Token stmt=t.get(i++);
                if(stmt.type()!=WORD || !stmt.value().equalsIgnoreCase("send")) throw err(stmt,"Unsupported statement. Currently supported: send.");
                if(t.get(i).type()!=STRING) throw err(t.get(i),"Expected quoted text after send.");
                String message=t.get(i++).value(); boolean toPlayer=false;
                if(t.get(i).type()==WORD && t.get(i).value().equalsIgnoreCase("to")){i++; if(t.get(i).type()!=WORD || !t.get(i).value().equalsIgnoreCase("player")) throw err(t.get(i),"Expected 'player' after 'to'."); i++; toPlayer=true;}
                if(t.get(i).type()!=NEWLINE && t.get(i).type()!=DEDENT && t.get(i).type()!=EOF) throw err(t.get(i),"Unexpected text after send statement.");
                statements.add(new ScriptModel.SendStatement(message,toPlayer,stmt.line())); i=skipLine(t,i);
            }
            if(t.get(i).type()==DEDENT)i++;
            if(t.get(i).type()==DEDENT)i++;
            if(statements.isEmpty()) throw err(start,"Command trigger must contain at least one statement.");
            commands.add(new ScriptCommand(name,statements,start.line()));
        }
        return new Script(path,List.copyOf(commands));
    }
    private static int skipLine(List<Lexer.Token> t,int i){while(t.get(i).type()!=NEWLINE && t.get(i).type()!=EOF)i++; if(t.get(i).type()==NEWLINE)i++; return i;}
    private static ScriptSyntaxException err(Lexer.Token t,String m){return new ScriptSyntaxException(t.line(),t.column(),m);}
}
