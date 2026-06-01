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
        return id;
    }

    public TaskType getType() {
        return type;
    }

    public boolean isCancelled() {
        return cancelled;
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
        return nextRunTick;
    }

    public void tick(long currentTick) {
        if (cancelled) {
            return;
        }

        if (currentTick >= nextRunTick) {
            try {
                action.run();
            } catch (Exception e) {
                System.err.println("[Fabric.SK Scheduler] Task error: " + id);
                e.printStackTrace();
            }

            if (type == TaskType.REPEATING) {
                nextRunTick = currentTick + intervalTicks;
            } else {
                cancel();
            }
        }
    }
}
