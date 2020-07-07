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

import com.solostudios.netherregeneration.NetherRegeneration;
import com.solostudios.netherregeneration.RegenerationTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class ForceRegenCommand implements CommandExecutor, TabCompleter {
    private final NetherRegeneration plugin;
    
    public ForceRegenCommand(NetherRegeneration plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 4) {
            try {
                Objects.requireNonNull(Bukkit.getWorld(args[0])).getChunkAt(Integer.getInteger(args[1]) << 4,
                                                                            Integer.getInteger(args[3]) << 4);
                sender.sendMessage("Forcing the regeneration of the chunk at x:" + (Integer.getInteger(args[1]) << 4) + " z:" +
                                   (Integer.getInteger(args[3]) << 4) + ".");
            } catch (NullPointerException e) {
                sender.sendMessage("Could not find world.");
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number.");
            }
        } else if (args.length == 3) {
            try {
                Objects.requireNonNull(Bukkit.getWorld(args[0])).getChunkAt(Integer.getInteger(args[1]) << 4,
                                                                            Integer.getInteger(args[2]) << 4);
                sender.sendMessage("Forcing the regeneration of the chunk at x:" + (Integer.getInteger(args[1]) << 4) + " z:" +
                                   (Integer.getInteger(args[2]) << 4) + ".");
            } catch (NullPointerException e) {
                sender.sendMessage("Could not find world.");
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number.");
            }
        } else if (args.length == 0) {
            if (sender instanceof Entity) {
                Entity entity = (Entity) sender;
                new RegenerationTask(plugin, entity.getLocation().getChunk()).run();
                sender.sendMessage("Forced a chunk regeneration at your current location.");
            } else if (sender instanceof CommandBlock) {
                new RegenerationTask(plugin, ((CommandBlock) sender).getLocation().getChunk()).run();
                sender.sendMessage("Forced a chunk regeneration at your current location.");
            }
        } else {
            return false;
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("forcechunkregen")) {
            switch (args.length) {
                case 0:
                    return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
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
