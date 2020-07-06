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

import com.solostudios.netherregeneration.commands.TestChunkOldCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class NetherRegeneration extends JavaPlugin {
    private Logger            logger;
    private FileConfiguration config;
    
    /**
     * Checks if a chunk is in the list of regenerated chunks.
     *
     * @param x
     *         The chunk's x coordinate. This is a block coordinate >> 4.
     * @param z
     *         The chunk's z coordinate. This is a block coordinate >> 4.
     */
    public boolean isChunkOld(int x, int z) {
        try {
            return !Objects.requireNonNull(config.getList("oldChunks", new ArrayList<String>())).contains(x + ":" + z);
        } catch (NullPointerException e) {
            logger.log(Level.WARNING, "Getting the config \"oldChunks\" produced null pointer exception.", e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Adds a chunk to the list of regenerated chunks.
     *
     * @param x
     *         The chunk's x coordinate. This is a block coordinate >> 4.
     * @param z
     *         The chunk's z coordinate. This is a block coordinate >> 4.
     */
    public void setRegeneratedChunk(int x, int z) {
        try {
            List<String> coordinates = config.getStringList("oldChunks");
            coordinates.add(x + ":" + z);
            
            config.set("oldChunks", coordinates);
            saveConfig();
            
        } catch (NullPointerException e) {
            logger.log(Level.WARNING, "Getting the config \"oldChunks\" produced null pointer exception.", e);
        }
    }
    
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        config = this.getConfig();
        
        
        logger = this.getLogger();
        
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        
        
        getCommand("isoldchunk").setExecutor(new TestChunkOldCommand(this));
        
    }
}
