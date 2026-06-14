package com.demodev.fabricsk.scheduler;

public class ScheduledTask {
   private final String id;
   private final Runnable action;
   private final TaskType type;
   private long delayTicks;
   private long intervalTicks;
   private long nextRunTick;
   private boolean cancelled;

   public ScheduledTask(String id, Runnable action, TaskType type) {
      this.id = id;
      this.action = action;
      this.type = type;
   }

   public String getId() {
      return this.id;
   }

   public TaskType getType() {
      return this.type;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void cancel() {
      this.cancelled = true;
   }

   public void setDelayTicks(long delayTicks) {
      this.delayTicks = delayTicks;
      this.nextRunTick = delayTicks;
   }

   public void setIntervalTicks(long intervalTicks) {
      this.intervalTicks = intervalTicks;
   }

   public long getNextRunTick() {
      return this.nextRunTick;
   }

   public void tick(long currentTick) {
      if (!this.cancelled) {
         if (currentTick >= this.nextRunTick) {
            try {
               this.action.run();
            } catch (Exception e) {
               System.err.println("[Fabric.SK Scheduler] Task error: " + this.id);
               e.printStackTrace();
            }

            if (this.type == TaskType.REPEATING) {
               this.nextRunTick = currentTick + this.intervalTicks;
            } else {
               this.cancel();
            }
         }

      }
   }
}
