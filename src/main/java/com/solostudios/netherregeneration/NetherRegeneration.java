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

import com.solostudios.netherregeneration.commands.ForceRegenCommand;
import com.solostudios.netherregeneration.commands.TestChunkOldCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;


public class NetherRegeneration extends JavaPlugin {
    private Logger     logger;
    private JSONObject chunkList;
    
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
            return !chunkList.getJSONArray("oldChunks").toList().contains(x + ":" + z);
        } catch (JSONException | NullPointerException e) {
            //logger.log(Level.WARNING, "Getting the config \"oldChunks\" produced null pointer exception.", e);
            //throw new RuntimeException(e);
            chunkList.put("oldChunks", new JSONArray());
            return true;
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
        JSONArray coordinates;
        try {
            coordinates = chunkList.getJSONArray("oldChunks");
        } catch (JSONException e) {
            //logger.log(Level.WARNING, "Getting the config \"oldChunks\" produced null pointer exception.", e);
            coordinates = new JSONArray();
        }
        coordinates.put(x + ":" + z);
    
        chunkList.put("oldChunks", coordinates);
    }
    
    public void saveJSON() {
        try (FileWriter chunkListFile = new FileWriter("plugins/RegenerateNether/chunkList.json")) {
            chunkListFile.write(chunkList.toString());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not save config file for list of chunks!", e);
        }
    }
    
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        logger = this.getLogger();
    
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
    
        TestChunkOldCommand testChunkOldCommand = new TestChunkOldCommand(this);
        ForceRegenCommand   forceRegenCommand   = new ForceRegenCommand(this);
    
        getCommand("isoldchunk").setExecutor(testChunkOldCommand);
        getCommand("forcechunkregen").setExecutor(forceRegenCommand);
    
        getCommand("isoldchunk").setTabCompleter(testChunkOldCommand);
        getCommand("forcechunkregen").setTabCompleter(forceRegenCommand);
    
    
        File chunkListFile = new File("plugins/RegenerateNether/chunkList.json");
        if (chunkListFile.exists()) {
            try {
                chunkList = new JSONObject(new String(Files.readAllBytes(chunkListFile.toPath())));
            } catch (FileNotFoundException e) {
                logger.log(Level.WARNING, "Could not find the chunk list file, even though the file exists...", e);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Exception occurred while attempting to read file.", e);
            }
        } else {
            try {
                //chunkList.mkdir();
                chunkListFile.createNewFile();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not create config file for list of chunks!", e);
            }
            chunkList = new JSONObject();
        }
        
    }
}
