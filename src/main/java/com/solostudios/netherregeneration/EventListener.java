/*
 * Copyright 2020 solonovamax@12oclockpoint.com
 *
 *
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

package com.solostudios.netherregeneration;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;


public class EventListener implements Listener {
    private final NetherRegeneration plugin;
    
    public EventListener(NetherRegeneration plugin) {
        this.plugin = plugin;
        Runtime.getRuntime().addShutdownHook(new Thread(this::onJVMShutdown));
    }
    
    public void onJVMShutdown() {
        plugin.saveJSON();
    }
    
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent chunkLoadEvent) {
        Chunk chunk = chunkLoadEvent.getChunk();
    
        if (chunkLoadEvent.isNewChunk()) {
            plugin.setRegeneratedChunk(chunk.getX(), chunk.getZ());
        } else {
            if (chunk.getWorld().getEnvironment() == World.Environment.NETHER) {
                //if (chunk.getWorld().getName().equalsIgnoreCase("worldeditregentempworld")) {
                //    plugin.getLogger().info("worldedit world");
                //    return;
                //}
                if (plugin.isChunkOld(chunk.getX(), chunk.getZ())) {
                    //plugin.setRegeneratedChunk(chunk.getX(), chunk.getZ());
                    //Bukkit.getScheduler().runTaskAsynchronously(plugin, new RegenerationTask(plugin, chunk));
                    new RegenerationTask(plugin, chunk).run(); //I was told running async causes problems and it turns out it does
                }
            }
        }
    }
    
}
