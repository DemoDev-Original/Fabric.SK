package dev.demodev.fabricsk.engine;

import net.minecraft.server.level.ServerPlayer;

public interface Expression<T> {
    T evaluate(ExecutionContext context);
    static String interpolate(String input,ExecutionContext context) {
        String result=input;
        ServerPlayer player=context.player();
        result=result.replace("%player%",player==null?"CONSOLE":player.getName().getString());
        result=result.replace("%uuid%",player==null?"":player.getUUID().toString());
        result=result.replace("%world%",context.source().getLevel().dimension().identifier().toString());
        if(context.argumentName()!=null)result=result.replace("%arg-1%",context.argumentName());
        return result;
    }
}
