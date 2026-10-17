package dev.demodev.fabricsk.engine;

import java.util.ArrayList;
import java.util.List;

/** Small indentation-aware lexer for the first Fabric.SK syntax set. */
public final class Lexer {
    public enum Type { WORD, STRING, COLON, NEWLINE, INDENT, DEDENT, EOF }
    public record Token(Type type, String value, int line, int column) { }

    public List<Token> lex(String source) throws ScriptSyntaxException {
        List<Token> out = new ArrayList<>();
        List<Integer> indents = new ArrayList<>(); indents.add(0);
        String[] lines = source.replace("\r", "").split("\n", -1);
        for (int li=0; li<lines.length; li++) {
            String line = lines[li]; int i=0;
            while (i<line.length() && (line.charAt(i)==' ' || line.charAt(i)=='\t')) i++;
            if (i==line.length() || line.substring(i).startsWith("#")) { out.add(new Token(Type.NEWLINE,"",li+1,line.length()+1)); continue; }
            int indent=i, current=indents.get(indents.size()-1);
            if (indent>current) { indents.add(indent); out.add(new Token(Type.INDENT,"",li+1,1)); }
            else if (indent<current) {
                while (indents.size()>1 && indent<indents.get(indents.size()-1)) { indents.remove(indents.size()-1); out.add(new Token(Type.DEDENT,"",li+1,1)); }
                if (indent!=indents.get(indents.size()-1)) throw new ScriptSyntaxException(li+1,1,"Indentation does not match an earlier block.");
            }
            while (i<line.length()) {
                char c=line.charAt(i);
                if (c==' ' || c=='\t') { i++; continue; }
                int col=i+1;
                if (c=='#') break;
                if (c==':') { out.add(new Token(Type.COLON,":",li+1,col)); i++; continue; }
                if (c=='"' || c=='\'') {
                    char quote=c; i++; StringBuilder b=new StringBuilder(); boolean closed=false;
                    while (i<line.length()) { char q=line.charAt(i++); if(q==quote){closed=true;break;} if(q=='\\' && i<line.length()){char e=line.charAt(i++); b.append(switch(e){case 'n'->'\n';case 't'->'\t';default->e;});} else b.append(q); }
                    if(!closed) throw new ScriptSyntaxException(li+1,col,"Unclosed string literal.");
                    out.add(new Token(Type.STRING,b.toString(),li+1,col)); continue;
                }
                int start=i;
                while(i<line.length() && !Character.isWhitespace(line.charAt(i)) && line.charAt(i)!=':') i++;
                out.add(new Token(Type.WORD,line.substring(start,i),li+1,col));
            }
            out.add(new Token(Type.NEWLINE,"",li+1,line.length()+1));
        }
        while(indents.size()>1){indents.remove(indents.size()-1);out.add(new Token(Type.DEDENT,"",lines.length,1));}
        out.add(new Token(Type.EOF,"",lines.length,1)); return out;
    }
}
