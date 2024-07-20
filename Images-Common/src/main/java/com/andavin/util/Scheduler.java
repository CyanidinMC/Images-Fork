/*
 * MIT License
 *
 * Copyright (c) 2020 Mark
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.andavin.util;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

/**
 * A class to make using the {@link BukkitScheduler} less
 * cumbersome and easier to use. Also, allows for easy
 * {@code do-while} loops and similar condition based looping
 * in timed loop tasks.
 *
 * @author Andavin
 * @since February 9, 2018
 */
@SuppressWarnings("UnusedReturnValue")
public final class Scheduler {

    private static Plugin instance;
    private static boolean isFolia = false;

    static {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        }catch (Exception ignored){}
    }

    public static void syncRegionized(Runnable task, Location loc){
        if (isFolia){
            Bukkit.getRegionScheduler().run(instance, loc, s -> task.run());
            return;
        }

        Bukkit.getScheduler().runTask(instance, task);
    }

    public static void syncOnEntity(Runnable task, Entity entity){
        entity.getScheduler().execute(instance, task, null, 1);
    }

    public static void async(Runnable run) {
        if (isFolia){
            Bukkit.getAsyncScheduler().runNow(instance, s -> run.run());
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(instance, run);
    }

    public static Object laterAsync(Runnable run, long delay) {
        if (isFolia){
            return Bukkit.getAsyncScheduler().runDelayed(instance, s -> run.run(), delay * 50, TimeUnit.MILLISECONDS);
        }

        return Bukkit.getScheduler().runTaskLaterAsynchronously(instance, run, delay);
    }

    public static Object repeatAsync(Runnable run, long delay, long period) {
        if (isFolia){
            return Bukkit.getAsyncScheduler().runAtFixedRate(instance, s -> run.run(), delay * 50, period * 50, TimeUnit.MILLISECONDS);
        }

        return Bukkit.getScheduler().runTaskTimerAsynchronously(instance, run, delay, period);
    }

    public static Object repeatAsyncWhile(Runnable run, long delay, long period, BooleanSupplier condition) {
        Task task = new Task(run, condition);
        Object bukkitTask = repeatAsync(task, delay, period);
        task.setTask(bukkitTask);
        return bukkitTask;
    }

    private static class Task implements Runnable {

        Object task;
        boolean cancelled;
        private final Runnable runnable;
        private final BooleanSupplier condition;

        Task(Runnable runnable, BooleanSupplier condition) {
            this.runnable = runnable;
            this.condition = condition;
        }

        final void setTask(Object task) {
            this.task = task;
        }

        @Override
        public void run() {

            if (this.cancelled) {

                if (this.task != null) {
                    if (this.task instanceof BukkitTask bTask){
                        bTask.cancel();
                    }else if (this.task instanceof ScheduledTask sTask){
                        sTask.cancel();
                    }
                }

                return;
            }

            if (this.condition.getAsBoolean()) {
                this.runnable.run();
            } else {

                if (this.task != null) {
                    if (this.task instanceof BukkitTask bTask){
                        bTask.cancel();
                    }else if (this.task instanceof ScheduledTask sTask){
                        sTask.cancel();
                    }
                } else {
                    this.cancelled = true;
                }
            }
        }
    }

}
