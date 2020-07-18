/*
 * Copyright 2020 solonovamax@12oclockpoint.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.solostudios.netherregeneration.regenqueue;

import com.solostudios.netherregeneration.NetherRegeneration;
import com.solostudios.netherregeneration.RegenerationTask;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.concurrent.ScheduledFuture;


public class RegenQueueItem {
    private final NetherRegeneration plugin;
    private final Chunk              chunk;
    private       ScheduledFuture<?> futureTask;
    private       CommandSender      sender = null;
    
    
    public RegenQueueItem(NetherRegeneration plugin, Chunk chunk, ScheduledFuture<?> futureTask) {
        this.plugin = plugin;
        this.chunk = chunk;
        this.futureTask = futureTask;
    }
    
    public RegenQueueItem(NetherRegeneration plugin, Chunk chunk, ScheduledFuture<?> futureTask, CommandSender sender) {
        this(plugin, chunk, futureTask);
        this.sender = sender;
    }
    
    public void cancelTask() {
        futureTask.cancel(false);
    }
    
    public void replaceTask(ScheduledFuture<?> futureTask) {
        futureTask.cancel(false);
        this.futureTask = futureTask;
    }
    
    public void sendMessageToSender(BaseComponent[] components) {
        if (sender != null) {
            sender.spigot().sendMessage(components);
        }
    }
    
    public int getOutlineYLevel() {
        return sender == null ? 128 : (sender instanceof Entity) ? ((Entity) sender).getLocation().getBlockY() : 128;
    }
    
    public void regenerateChunk() {
        Bukkit.getScheduler().runTask(plugin, new RegenerationTask(plugin, chunk));
    }
}
