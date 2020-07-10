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

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MagmaCube;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;


public class ChunkUtil {
    public static void outlineChunk(Chunk chunk) {
        World currentWorld = chunk.getWorld();
        int   x            = (chunk.getX() << 4) + 8; // shift 4 bits to get the block x value,
        int   z            = (chunk.getZ() << 4) + 8; // then add 8 to get the center of the chunk.
    
    
        Bukkit.getLogger().info("Outlining chunk at x:" + x + " z:" + z);
    
        genChunkOutline(chunk, currentWorld, 128);
        Bukkit.getScheduler().runTaskLater(NetherRegeneration.getPlugin(NetherRegeneration.class), () -> removeChunkOutline(chunk),
                                           10 * 30);
    }
    
    public static void outlineChunk(Location location) {
        World currentWorld = location.getWorld();
        Chunk chunk        = location.getChunk();
        int   x            = (chunk.getX() << 4) + 8; // shift 4 bits to get the block x value,
        int   z            = (chunk.getZ() << 4) + 8; // then add 8 to get the center of the chunk.
        
        
        Bukkit.getLogger().info("Outlining chunk at x:" + x + " z:" + z);
        
        genChunkOutline(chunk, currentWorld, location.getBlockY());
        Bukkit.getScheduler().runTaskLater(NetherRegeneration.getPlugin(NetherRegeneration.class), () -> removeChunkOutline(chunk),
                                           10 * 30);
    }
    
    public static void genChunkOutline(Chunk chunk, World currentWorld, int y) {
        int        x                      = (chunk.getX() << 4) + 8; // shift 4 bits to get the block x value,
        int        z                      = (chunk.getZ() << 4) + 8; // then add 8 to get the center of the chunk.
        Scoreboard chunkOutlineScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team       chunkOutlineTeam       = chunkOutlineScoreboard.getTeam("chunk" + (x >> 4) + "-" + (z >> 4));
        
        if (chunkOutlineTeam == null)
            chunkOutlineTeam = chunkOutlineScoreboard.registerNewTeam("chunk" + (x >> 4) + "-" + (z >> 4));
        
        chunkOutlineTeam.setColor(ChatColor.DARK_GREEN);
        chunkOutlineTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        
        for (int i = y % 16; i < y; i += 16) {
            Entity    entity    = currentWorld.spawnEntity(new Location(currentWorld, x, i, z), EntityType.MAGMA_CUBE);
            MagmaCube magmaCube = (MagmaCube) entity;
            
            magmaCube.setSize(32);
            magmaCube.addPotionEffects(Arrays.asList(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 0, true, false),
                                                     new PotionEffect(PotionEffectType.GLOWING, 100000, 0, true, false)));
            
            magmaCube.setInvulnerable(true);
            magmaCube.setPersistent(true);
            magmaCube.setSilent(true);
            magmaCube.setCollidable(true);
            magmaCube.setAI(false);
            magmaCube.setCustomName("chunk " + (x >> 4) + "-" + (z >> 4));
            
            System.out.println(magmaCube.isCollidable());
            
            chunkOutlineTeam.addEntry(magmaCube.getUniqueId().toString());
            magmaCube.setGlowing(true);
        }
    }
    
    public static void removeChunkOutline(Chunk chunk) {
        chunk.getWorld().getEntities().stream().filter((entity) -> {
            if (entity instanceof MagmaCube)
                return entity.getName().equalsIgnoreCase("chunk " + chunk.getX() + "-" + chunk.getZ());
            return false;
        }).forEach((entity) -> {
            entity.teleport(new Location(chunk.getWorld(), 0, -2000, 0));
        });
    }
    
    private static long stringToSeed(String s) {
        if (s == null) {
            return 0;
        }
        long hash = 0;
        for (char c : s.toCharArray()) {
            hash = 31L * hash + c;
        }
        return hash;
    }
}
