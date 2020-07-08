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
import org.bukkit.Location;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;

import java.util.Collections;
import java.util.List;


public class TestChunkOldCommand implements CommandExecutor, TabCompleter {
    private final NetherRegeneration plugin;
    
    public TestChunkOldCommand(NetherRegeneration plugin) {
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
                isChunkOld(sender, Integer.getInteger(args[0]), Integer.getInteger(args[2]));
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number.");
            }
        } else if (args.length == 2) {
            try {
                isChunkOld(sender, Integer.getInteger(args[0]), Integer.getInteger(args[1]));
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number.");
            }
        } else if (args.length == 0) {
            isChunkOld(sender, ((Entity) sender).getLocation());
        } else {
            return false;
        }
        return true;
    }
    
    private void isChunkOld(CommandSender sender, int x, int z) {
        sender.sendMessage(plugin.isChunkOld(x << 4, z << 4) ?
                           "The selected chunks is a new chunk." :
                           "The selected chunk is an old chunk.");
    }
    
    private void isChunkOld(CommandSender sender, Location location) {
        isChunkOld(sender, location.getBlockX(), location.getBlockZ());
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("isoldchunk")) {
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
