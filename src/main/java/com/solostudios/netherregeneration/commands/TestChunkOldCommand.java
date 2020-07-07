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
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class TestChunkOldCommand implements CommandExecutor, TabCompleter {
    private final NetherRegeneration plugin;
    
    public TestChunkOldCommand(NetherRegeneration plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            sender.sendMessage(plugin.isChunkOld(player.getLocation().getBlockX() << 4, player.getLocation().getBlockZ() << 4) ?
                               "You are in a regenerated chunk." :
                               "You are in an old chunk.");
        }
        
        
        if (args.length == 4) {
            try {
                Objects.requireNonNull(Bukkit.getWorld(args[0])).getChunkAt(Integer.getInteger(args[1]) << 4,
                                                                            Integer.getInteger(args[3]) << 4);
                sender.sendMessage(plugin.isChunkOld(Integer.getInteger(args[1]) << 4, Integer.getInteger(args[2]) << 4) ?
                                   "That chunk is a new chunk." :
                                   "That chunk is an old chunk.");
            } catch (NullPointerException e) {
                sender.sendMessage("Could not find world.");
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number.");
            }
        } else if (args.length == 3) {
            try {
                Objects.requireNonNull(Bukkit.getWorld(args[0])).getChunkAt(Integer.getInteger(args[1]) << 4,
                                                                            Integer.getInteger(args[2]) << 4);
                sender.sendMessage(plugin.isChunkOld(Integer.getInteger(args[1]) << 4, Integer.getInteger(args[2]) << 4) ?
                                   "That chunk is a new chunk." :
                                   "That chunk is an old chunk.");
            } catch (NullPointerException e) {
                sender.sendMessage("Could not find world.");
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number.");
            }
        } else if (args.length == 0) {
            if (sender instanceof Entity) {
                Entity entity = (Entity) sender;
                sender.sendMessage(plugin.isChunkOld(entity.getLocation().getBlockX() << 4, entity.getLocation().getBlockZ() << 4) ?
                                   "You are in a regenerated chunk." :
                                   "You are in an old chunk.");
            } else if (sender instanceof CommandBlock) {
                CommandBlock commandBlock = (CommandBlock) sender;
                sender.sendMessage(
                        plugin.isChunkOld(commandBlock.getLocation().getBlockX() << 4, commandBlock.getLocation().getBlockZ() << 4) ?
                        "You are in a regenerated chunk." :
                        "You are in an old chunk.");
            }
        } else {
            return false;
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("isoldchunk")) {
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
