package com.demodev.fabricsk.effect;

import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class TeleportEffect implements Effect {
   public void execute(Object... args) {
      if (args.length >= 5) {
         Object arg0 = args[0];
         if (arg0 instanceof ServerPlayerEntity player) {
            Object arg1 = args[1];
            if (arg1 instanceof ServerWorld world) {
               Object arg2 = args[2];
               Object arg3 = args[3];
               Object arg4 = args[4];
               if (arg2 instanceof Number x && arg3 instanceof Number y && arg4 instanceof Number z) {
                  Set<PositionFlag> flags = EnumSet.noneOf(PositionFlag.class);
                  player.teleport(world, x.doubleValue(), y.doubleValue(), z.doubleValue(), flags, player.getYaw(), player.getPitch(), false);
               }
            }
         }
      }
   }
}
