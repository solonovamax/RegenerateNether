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

package com.solostudios.netherregeneration.commands;

import com.solostudios.netherregeneration.ChunkUtil;
import com.solostudios.netherregeneration.NetherRegeneration;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;

import java.util.Collections;
import java.util.List;


public class OutlineChunkCommand implements CommandExecutor, TabCompleter {
    private final NetherRegeneration plugin;
    
    public OutlineChunkCommand(NetherRegeneration plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Entity)) {
            sender.sendMessage(
                    "I'm way too lazy to try and figure out why things are breaking, so for now only entities can run the command.");
            return false;
        }
        Entity entity = (Entity) sender;
        if (args.length == 3) {
            try {
                ChunkUtil.outlineChunk(new Location(entity.getWorld(), Integer.getInteger(args[0]) << 4, Integer.getInteger(args[1]) << 4,
                                                    Integer.getInteger(args[2]) << 4));
                sender.sendMessage("Outlining the chunk at x:" + (((Entity) sender).getLocation().getBlockX())
                                   + " z:" + ((Entity) sender).getLocation().getBlockZ() +
                                   ". Note: if you are inside the chunk, then you will not see the outline.");
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number.");
            }
        } else if (args.length == 2) {
            try {
                outlineChunk(sender, entity.getWorld().getChunkAt(Integer.getInteger(args[0]) << 4, Integer.getInteger(args[1]) << 4));
            
                ChunkUtil.outlineChunk(entity.getWorld().getChunkAt(Integer.getInteger(args[0]) << 4, Integer.getInteger(args[1]) << 4));
                sender.sendMessage("Outlining the chunk at x:" + (Integer.getInteger(args[0]) << 4) + " z:" +
                                   (Integer.getInteger(args[1]) << 4) +
                                   ". Note: if you are inside the chunk, then you will not see the outline.");
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number.");
            }
        } else if (args.length == 0) {
            ChunkUtil.outlineChunk(((Entity) sender).getLocation());
            sender.sendMessage("Outlining the chunk at x:" + (((Entity) sender).getLocation().getBlockX())
                               + " z:" + ((Entity) sender).getLocation().getBlockZ() +
                               ". Note: if you are inside the chunk, then you will not see the outline.");
        } else {
            return false;
        }
        
        return true;
    }
    
    private void outlineChunk(CommandSender sender, Chunk chunk) {
        ChunkUtil.outlineChunk(chunk);
        sender.sendMessage("Outlining the chunk at x:" + (chunk.getX() << 4) + " z:" +
                           (chunk.getZ() << 4) + ". Note: if you are inside the chunk, then you will not see the outline.");
    }
    
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("outlinechunk")) {
            switch (args.length) {
                case 1:
                    if (sender instanceof CommandBlock)
                        return Collections.singletonList(String.valueOf(((CommandBlock) sender).getLocation().getBlockX()));
                    if (sender instanceof Entity)
                        return Collections.singletonList(String.valueOf(((Entity) sender).getLocation().getBlockX()));
                    return null;
                case 2:
                case 3:
                    if (sender instanceof CommandBlock)
                        return Collections.singletonList(String.valueOf(((CommandBlock) sender).getLocation().getBlockZ()));
                    if (sender instanceof Entity)
                        return Collections.singletonList(String.valueOf(((Entity) sender).getLocation().getBlockZ()));
                    return null;
            }
        }
        return null;
    }
}
