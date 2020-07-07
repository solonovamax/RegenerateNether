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

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Chunk;


public class RegenerationTask implements Runnable {
    private final Chunk              chunk;
    private final NetherRegeneration plugin;
    
    public RegenerationTask(NetherRegeneration plugin, Chunk chunk) {
        this.plugin = plugin;
        this.chunk = chunk;
    }
    
    @Override
    public void run() {
        /*
        This code was taken from here:
        https://www.spigotmc.org/threads/world-regeneratechunk-x-z.414053/
         */
        
        if (plugin.isChunkOld(chunk.getX(), chunk.getZ())) {
            System.out.println(
                    "The world is: " + chunk.getWorld().getName() + " chunk coordinates: x:" + chunk.getX() + " z:" + chunk.getZ());
            plugin.setRegeneratedChunk(chunk.getX(), chunk.getZ());
            int bx = chunk.getX() << 4; //shift 4 bits to get translate coordinates from chunk coordinates to block coordinates
            int bz = chunk.getZ() << 4;
            try {
                BukkitWorld world = new BukkitWorld(chunk.getWorld());
                CuboidRegion regenerationRegion = new CuboidRegion(world, BlockVector3.at(bx, 0, bz), BlockVector3.at(bx + 15, 122,
                                                                                                                      bz + 15));
                EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
                boolean     result  = world.regenerate(regenerationRegion, session);
                session.flushSession();
        
                //log here for testing
                //Bukkit.getLogger().info("Regenerating chunk at coords x:" + bx + " z:" + bz);
                
            } catch (Exception e) {
                //Bukkit.getLogger().log(Level.WARNING, "error", e);
        
            }
        }
    }
}
