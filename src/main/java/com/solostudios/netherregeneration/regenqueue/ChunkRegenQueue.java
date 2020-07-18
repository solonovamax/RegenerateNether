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

import com.solostudios.netherregeneration.ChunkUtil;
import com.solostudios.netherregeneration.NetherRegeneration;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ChunkRegenQueue {
    private final NetherRegeneration       plugin;
    private final ScheduledExecutorService chunkTaskThreadPool;
    
    private final Map<String, RegenQueueItem> regenQueueItemMap;
    
    public ChunkRegenQueue(NetherRegeneration plugin) {
        this.plugin = plugin;
        this.chunkTaskThreadPool = Executors.newSingleThreadScheduledExecutor();
        regenQueueItemMap = new HashMap<>();
    }
    
    private static TextComponent getClickableCommandTextComponent(String text, String command, String hoverText) {
        TextComponent component = getClickableCommandTextComponent(text, command);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
        return component;
    }
    
    private static TextComponent getClickableCommandTextComponent(String text, String command) {
        TextComponent component = new TextComponent(text);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return component;
    }
    
    public void processQueueItem(String queueItemName) {
        if (!regenQueueItemMap.containsKey(queueItemName))
            return;
        
        RegenQueueItem queueItem = regenQueueItemMap.remove(queueItemName);
        
        queueItem.regenerateChunk();
        
    }
    
    @SuppressWarnings("DuplicatedCode")
    public boolean addTaskWithConfirmation(Chunk chunk, Entity entity) {
        TextComponent cancelRegenComponent = getClickableCommandTextComponent("[Click to Cancel]",
                                                                              "/cancelregen " + (chunk.getX() << 4) + " " +
                                                                              entity.getLocation().getBlockY() + " " + (chunk.getZ() << 4),
                                                                              "Click me to cancel the regen!");
        TextComponent confirmComponent = getClickableCommandTextComponent("[Click to Confirm]",
                                                                          "/confirmregen " + (chunk.getX() << 4) + " " +
                                                                          entity.getLocation().getBlockY() + " " + (chunk.getZ() << 4),
                                                                          "Click me to confirm the regen!");
        TextComponent outlineComponent = getClickableCommandTextComponent("[Click to Outline]",
                                                                          "/outlineanddelay " + (chunk.getX() << 4) + " " +
                                                                          (chunk.getZ() << 4),
                                                                          "Click me to outline the chunk and delay the task by 30 " +
                                                                          "seconds!");
        
        entity.spigot().sendMessage(new ComponentBuilder("Regenerating the chunk in 30 seconds.")
                                            .color(ChatColor.WHITE.asBungee())
                                            .append(" ")
                                            .color(net.md_5.bungee.api.ChatColor.GREEN)
                                            .append(cancelRegenComponent)
                                            .append(" ")
                                            .append(confirmComponent)
                                            .append(" ")
                                            .append(outlineComponent)
                                            .create());
        
        regenQueueItemMap.put("chunk " + chunk.getX() + "-" + chunk.getZ(), new RegenQueueItem(plugin, chunk, chunkTaskThreadPool.schedule(
                () -> processQueueItem("chunk " + chunk.getX() + "-" + chunk.getZ()), 30L, TimeUnit.SECONDS), entity));
        
        return true;
    }
    
    @SuppressWarnings("DuplicatedCode")
    public boolean addTaskWithConfirmation(Chunk chunk) {
        regenQueueItemMap.put("chunk " + chunk.getX() + "-" + chunk.getZ(), new RegenQueueItem(plugin, chunk, chunkTaskThreadPool.schedule(
                () -> processQueueItem("chunk " + chunk.getX() + "-" + chunk.getZ()), 30L, TimeUnit.SECONDS)));
        
        return true;
    }
    
    @SuppressWarnings("DuplicatedCode")
    public boolean addTaskWithQuickConfirmation(Chunk chunk, Entity entity) {
        TextComponent cancelRegenComponent = getClickableCommandTextComponent("[Click to Cancel]",
                                                                              "/cancelregen " + (chunk.getX() << 4) + " " +
                                                                              entity.getLocation().getBlockY() + " " + (chunk.getZ() << 4),
                                                                              "Click me to cancel the regen!");
        TextComponent confirmComponent = getClickableCommandTextComponent("[Click to Confirm]",
                                                                          "/confirmregen " + (chunk.getX() << 4) + " " +
                                                                          entity.getLocation().getBlockY() + " " + (chunk.getZ() << 4),
                                                                          "Click me to confirm the regen!");
        
        entity.spigot().sendMessage(new ComponentBuilder("Regenerating the chunk in 10 seconds.")
                                            .color(ChatColor.WHITE.asBungee())
                                            .append(" ")
                                            .color(net.md_5.bungee.api.ChatColor.GREEN)
                                            .append(cancelRegenComponent)
                                            .append(" ")
                                            .append(confirmComponent)
                                            .create());
        
        regenQueueItemMap.put("chunk " + chunk.getX() + "-" + chunk.getZ(), new RegenQueueItem(plugin, chunk, chunkTaskThreadPool.schedule(
                () -> processQueueItem("chunk " + chunk.getX() + "-" + chunk.getZ()), 10L, TimeUnit.SECONDS), entity));
        
        return true;
    }
    
    public boolean quickComplete(Chunk chunk) {
        if (!regenQueueItemMap.containsKey("chunk " + chunk.getX() + "-" + chunk.getZ()))
            return false;
        
        RegenQueueItem queueItem = regenQueueItemMap.get("chunk " + chunk.getX() + "-" + chunk.getZ());
        
        queueItem.replaceTask(chunkTaskThreadPool.schedule(() -> processQueueItem("chunk " + chunk.getX() + "-" + chunk.getZ()),
                                                           0L, TimeUnit.MILLISECONDS));
        return true;
    }
    
    public boolean removeTask(Chunk chunk) {
        if (!regenQueueItemMap.containsKey("chunk " + chunk.getX() + "-" + chunk.getZ()))
            return false;
        else {
            regenQueueItemMap.get("chunk " + chunk.getX() + "-" + chunk.getZ()).cancelTask();
        }
        return regenQueueItemMap.remove("chunk " + chunk.getX() + "-" + chunk.getZ()) != null;
    }
    
    public boolean showOutlineAndDelayTask(Chunk chunk) {
        plugin.getLogger().info("chunk " + chunk.getX() + "-" + chunk.getZ());
        if (!regenQueueItemMap.containsKey("chunk " + chunk.getX() + "-" + chunk.getZ()))
            return false;
        
        RegenQueueItem queueItem = regenQueueItemMap.get("chunk " + chunk.getX() + "-" + chunk.getZ());
        
        
        TextComponent cancelRegenComponent = getClickableCommandTextComponent("[Click to Cancel]",
                                                                              "/cancelregen " + (chunk.getX() << 4) + " " +
                                                                              queueItem.getOutlineYLevel() + " " + (chunk.getZ() << 4),
                                                                              "Click me to cancel the regen!");
        TextComponent confirmComponent = getClickableCommandTextComponent("[Click to Confirm]",
                                                                          "/confirmregen " + (chunk.getX() << 4) + " " +
                                                                          queueItem.getOutlineYLevel() + " " + (chunk.getZ() << 4),
                                                                          "Click me to confirm the regen!");
        
        queueItem.sendMessageToSender(new ComponentBuilder(
                "Outlined the chunk. The chunk will be regenerated in 30 seconds unless canceled")
                                              .color(ChatColor.WHITE.asBungee())
                                              .append(" ")
                                              .color(net.md_5.bungee.api.ChatColor.GREEN)
                                              .append(cancelRegenComponent)
                                              .append(" ")
                                              .append(confirmComponent)
                                              .create());
        
        ChunkUtil.outlineChunk(chunk);
        
        queueItem.replaceTask(chunkTaskThreadPool.schedule(() -> processQueueItem("chunk " + chunk.getX() + "-" + chunk.getZ()),
                                                           0L, TimeUnit.MILLISECONDS));
        
        return true;
    }
}
