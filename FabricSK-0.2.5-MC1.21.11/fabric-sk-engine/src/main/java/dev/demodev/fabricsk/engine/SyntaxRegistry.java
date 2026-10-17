package dev.demodev.fabricsk.engine;

import dev.demodev.fabricsk.engine.ScriptModel.SendStatement;
import net.minecraft.network.chat.Component;
import java.util.LinkedHashMap;
import java.util.Map;

/** Registry for executable syntax effects; extensions can register additional handlers. */
public final class SyntaxRegistry {
    @FunctionalInterface public interface EffectHandler<T> { void execute(T statement, ExecutionContext context); }
    private final Map<Class<?>, EffectHandler<?>> handlers=new LinkedHashMap<>();
    public SyntaxRegistry(){register(SendStatement.class,(s,c)->{
        String text=Expression.interpolate(s.message(),c);
        if(s.toPlayer() && c.player()!=null) c.player().sendSystemMessage(Component.literal(text));
        else c.source().sendSuccess(()->Component.literal(text),false);
    });}
    public <T> void register(Class<T> type, EffectHandler<T> handler){handlers.put(type,handler);}
    @SuppressWarnings("unchecked") public void execute(Object statement,ExecutionContext context){
        EffectHandler<Object> handler=(EffectHandler<Object>)handlers.get(statement.getClass());
        if(handler==null) throw new IllegalArgumentException("No syntax handler registered for "+statement.getClass().getSimpleName());
        handler.execute(statement,context);
    }
    public int size(){return handlers.size();}
}
