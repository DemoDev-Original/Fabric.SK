package dev.demodev.fabricsk.engine;

import dev.demodev.fabricsk.engine.ScriptModel.*;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Indentation-aware parser for the supported Fabric.SK syntax subset. */
public final class Parser {
    private record Line(int number, int indent, String text) {}
    private record Parsed(List<Statement> statements, int next) {}

    public Script parse(Path path, String source) throws ScriptSyntaxException {
        List<Line> lines = new ArrayList<>();
        String[] raw = source.replace("\r", "").split("\n", -1);
        for (int i=0;i<raw.length;i++) {
            String s=raw[i]; int n=0;
            while(n<s.length() && (s.charAt(n)==' ' || s.charAt(n)=='\t')) n++;
            String body=s.substring(n).stripTrailing();
            if(body.isBlank() || body.startsWith("#")) continue;
            lines.add(new Line(i+1,n,body));
        }
        List<ScriptCommand> commands=new ArrayList<>();
        List<Statement> onLoad=new ArrayList<>(), onDamage=new ArrayList<>(), onRightClick=new ArrayList<>();
        int i=0;
        while(i<lines.size()) {
            Line l=lines.get(i);
            if(l.indent()!=0) throw error(l,"Unexpected indentation at top level.");
            if(l.text().equalsIgnoreCase("on load:")) {
                Parsed p=parseBlock(lines,i+1,4); onLoad.addAll(p.statements()); i=p.next(); continue;
            }
            if(l.text().equalsIgnoreCase("on damage:")) {
                Parsed p=parseBlock(lines,i+1,4); onDamage.addAll(p.statements()); i=p.next(); continue;
            }
            if(l.text().equalsIgnoreCase("on rightclick:") || l.text().equalsIgnoreCase("on right click:")) {
                Parsed p=parseBlock(lines,i+1,4); onRightClick.addAll(p.statements()); i=p.next(); continue;
            }
            Matcher m=Pattern.compile("(?i)^command\\s+/(\\w+)(?:\\s+\\[<([a-zA-Z_]+)>\\])?\\s*:$").matcher(l.text());
            if(!m.matches()) throw error(l,"Expected 'command /name:' or a supported event such as 'on load:'.");
            String name=m.group(1), arg=m.group(2)==null?"":m.group(2).toLowerCase(Locale.ROOT);
            if(!arg.isEmpty() && !arg.equals("player")) throw error(l,"Only optional [<player>] command arguments are supported currently.");
            String permission="", permissionMessage="";
            int j=i+1; boolean trigger=false; List<Statement> body=List.of();
            while(j<lines.size() && lines.get(j).indent()>0) {
                Line opt=lines.get(j);
                if(opt.indent()!=4) throw error(opt,"Command options and trigger must be indented by four spaces.");
                if(opt.text().equalsIgnoreCase("trigger:")) {
                    Parsed p=parseBlock(lines,j+1,8); body=p.statements(); j=p.next(); trigger=true; break;
                } else if(opt.text().toLowerCase(Locale.ROOT).startsWith("permission message:")) {
                    permissionMessage=unquote(opt.text().substring(opt.text().indexOf(':')+1).trim()); j++;
                } else if(opt.text().toLowerCase(Locale.ROOT).startsWith("permission:")) {
                    permission=unquote(opt.text().substring(opt.text().indexOf(':')+1).trim()); j++;
                } else throw error(opt,"Expected 'permission:', 'permission message:', or 'trigger:' in command declaration.");
            }
            if(!trigger) throw error(l,"Command declaration is missing an indented trigger block.");
            commands.add(new ScriptCommand(name,arg,permission,permissionMessage,body,l.number()));
            i=j;
        }
        return new Script(path,List.copyOf(commands),List.copyOf(onLoad),List.copyOf(onDamage),List.copyOf(onRightClick));
    }

    private Parsed parseBlock(List<Line> lines,int start,int expectedIndent) throws ScriptSyntaxException {
        if(start>=lines.size() || lines.get(start).indent()<expectedIndent)
            return new Parsed(List.of(),start);
        int indent=lines.get(start).indent();
        if(indent<expectedIndent) return new Parsed(List.of(),start);
        List<Statement> out=new ArrayList<>(); int i=start;
        while(i<lines.size()) {
            Line l=lines.get(i);
            if(l.indent()<indent) break;
            if(l.indent()>indent) throw error(l,"Unexpected indentation; add a block header such as 'if ...:' or fix spacing.");
            String s=l.text(); String low=s.toLowerCase(Locale.ROOT);
            if(low.startsWith("loop entities in radius ") && s.endsWith(" of location of player:")) {
                Matcher lm=Pattern.compile("(?i)^loop entities in radius ([0-9]+(?:\\.[0-9]+)?) of location of player:$").matcher(s);
                if(!lm.matches()) throw error(l,"Expected 'loop entities in radius <number> of location of player:'.");
                Parsed nested=parseBlock(lines,i+1,indent+4);
                out.add(new LoopEntitiesStatement(Double.parseDouble(lm.group(1)),nested.statements(),l.number())); i=nested.next(); continue;
            }
            if(low.startsWith("if ") && s.endsWith(":")) {
                String condition=s.substring(3,s.length()-1).trim();
                Parsed nested=parseBlock(lines,i+1,indent+4);
                List<Statement> otherwise=List.of(); int next=nested.next();
                if(next<lines.size() && lines.get(next).indent()==indent && lines.get(next).text().equalsIgnoreCase("else:")) {
                    Parsed other=parseBlock(lines,next+1,indent+4); otherwise=other.statements(); next=other.next();
                }
                out.add(new IfStatement(condition,"",nested.statements(),otherwise,l.number())); i=next; continue;
            }
            Statement statement=parseStatement(l);
            out.add(statement); i++;
        }
        return new Parsed(List.copyOf(out),i);
    }

    private Statement parseStatement(Line l) throws ScriptSyntaxException {
        String s=l.text(), low=s.toLowerCase(Locale.ROOT);
        Matcher m;
        if((m=Pattern.compile("(?i)^send\\s+(.+?)(?:\\s+to\\s+(player|victim))?$").matcher(s)).matches())
            return new SendStatement(unquote(m.group(1)),m.group(2)==null?"source":m.group(2).toLowerCase(Locale.ROOT),l.number());
        if((m=Pattern.compile("(?i)^set\\s+\\{([^}]+)}\\s+to\\s+([a-z0-9_ ]+?)(?:\\s+of\\s+([a-z0-9_]+)\\s+(\\d+))?$").matcher(s)).matches()) {
            String material=m.group(2).trim().replace(' ','_').toUpperCase(Locale.ROOT);
            String ench=m.group(3)==null?"":m.group(3).toUpperCase(Locale.ROOT);
            int level=m.group(4)==null?0:Integer.parseInt(m.group(4));
            return new SetItemStatement(m.group(1).trim(),material,ench,level,l.number());
        }
        if((m=Pattern.compile("(?i)^set\\s+name\\s+of\\s+\\{([^}]+)}\\s+to\\s+(.+)$").matcher(s)).matches())
            return new SetNameStatement(m.group(1).trim(),unquote(m.group(2).trim()),l.number());
        if((m=Pattern.compile("(?i)^set\\s+lore\\s+of\\s+\\{([^}]+)}\\s+to\\s+(.+)$").matcher(s)).matches())
            return new SetLoreStatement(m.group(1).trim(),splitComma(m.group(2)).stream().map(Parser::unquote).toList(),l.number());
        if((m=Pattern.compile("(?i)^give\\s+player\\s+\\{([^}]+)}$").matcher(s)).matches())
            return new GiveStatement(m.group(1).trim(),l.number());
        if((m=Pattern.compile("(?i)^give\\s+player\\s+([a-z0-9_ ]+?)(?:\\s+named\\s+\"([^\"]*)\")?$").matcher(s)).matches())
            return new GiveMaterialStatement(m.group(1).trim().replace(' ','_').toUpperCase(Locale.ROOT),m.group(2)==null?"":unquote(m.group(2)),l.number());
        if(low.equals("lightning at player")) return new StrikeLightningStatement("player",l.number());
        if((m=Pattern.compile("(?i)^play sound [\"']([^\"']+)[\"'] at location of player$").matcher(s)).matches()) return new PlaySoundStatement(m.group(1),"player",l.number());
        if((m=Pattern.compile("(?i)^ignite (loop-entity|victim|player) for (\\d+) seconds?$").matcher(s)).matches()) return new IgniteStatement(m.group(1).toLowerCase(Locale.ROOT),Integer.parseInt(m.group(2)),l.number());
        if((m=Pattern.compile("(?i)^push (player|victim) upward at speed ([0-9]+(?:\\.[0-9]+)?)$").matcher(s)).matches()) return new PushStatement(m.group(1).toLowerCase(Locale.ROOT),0,Double.parseDouble(m.group(2)),0,l.number());
        if((m=Pattern.compile("(?i)^set damage to damage \\* ([0-9]+(?:\\.[0-9]+)?)$").matcher(s)).matches()) return new SetDamageMultiplierStatement(Double.parseDouble(m.group(1)),l.number());
        if((m=Pattern.compile("(?i)^apply\\s+instant damage\\s+(\\d+)\\s+to\\s+(?:the\\s+)?victim(?:\\s+without particles and without icon)?$").matcher(s)).matches())
            return new ApplyEffectStatement("INSTANT_DAMAGE",Integer.parseInt(m.group(1)),0,"victim",l.number());
        if((m=Pattern.compile("(?i)^apply\\s+([a-z_ ]+?)\\s+(\\d+)\\s+to\\s+(?:the\\s+)?(victim|player|loop-entity)\\s+for\\s+(\\d+)\\s+seconds?$").matcher(s)).matches())
            return new ApplyEffectStatement(m.group(1).trim().replace(' ','_').toUpperCase(Locale.ROOT),Integer.parseInt(m.group(2)),Integer.parseInt(m.group(4)),m.group(3).toLowerCase(Locale.ROOT),l.number());
        if((m=Pattern.compile("(?i)^set\\s+freeze\\s+ticks\\s+of\\s+(victim|player)\\s+to\\s+(\\d+)$").matcher(s)).matches())
            return new SetFreezeTicksStatement(Integer.parseInt(m.group(2)),m.group(1).toLowerCase(Locale.ROOT),l.number());
        if((m=Pattern.compile("(?i)^strike\\s+lightning\\s+at\\s+location\\s+of\\s+(arg-1|player)$").matcher(s)).matches())
            return new StrikeLightningStatement(m.group(1).toLowerCase(Locale.ROOT),l.number());
        throw error(l,"Unsupported statement: '"+s+"'. Supported: send, item variables, give player <item>, if/else, loops, effects, ignite, sound, push, damage multiplier, freeze ticks, lightning, on rightclick.");
    }

    private static List<String> splitComma(String s) {
        List<String> result=new ArrayList<>(); StringBuilder b=new StringBuilder(); boolean quoted=false; char quote=0;
        for(int i=0;i<s.length();i++){char c=s.charAt(i); if((c=='"'||c==39)&&(!quoted||c==quote)){if(!quoted){quoted=true;quote=c;}else quoted=false;b.append(c);}else if(c==','&&!quoted){result.add(b.toString().trim());b.setLength(0);}else b.append(c);}
        result.add(b.toString().trim()); return result;
    }
    private static String unquote(String s) {
        String v=s.trim(); if(v.length()>=2 && ((v.startsWith("\"")&&v.endsWith("\""))||(v.startsWith("'")&&v.endsWith("'")))) v=v.substring(1,v.length()-1);
        return v.replace("&","§");
    }
    private static ScriptSyntaxException error(Line l,String msg){return new ScriptSyntaxException(l.number(),l.indent()+1,msg);}
}
