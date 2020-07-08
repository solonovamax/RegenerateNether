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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class ForceRegenCommand implements CommandExecutor, TabCompleter {
    private final NetherRegeneration plugin;
    
    public ForceRegenCommand(NetherRegeneration plugin) {
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
        if (args.length == 4) {
            try {
                regenerate(sender, entity.getWorld().getChunkAt(Integer.getInteger(args[1]) << 4, Integer.getInteger(args[3]) << 4));
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number.");
            }
        } else if (args.length == 3) {
            try {
                regenerate(sender, entity.getWorld().getChunkAt(Integer.getInteger(args[1]) << 4, Integer.getInteger(args[2]) << 4));
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number.");
            }
        } else if (args.length == 0) {
            regenerate(sender, ((Entity) sender).getLocation().getChunk());
        } else {
            return false;
        }
    
        return true;
    }
    
    private void regenerate(CommandSender sender, Chunk chunk) {
        new RegenerationTask(plugin, chunk).run();
        sender.sendMessage("Forcing the regeneration of the chunk at x:" + (chunk.getX() << 4) + " z:" +
                           (chunk.getZ() << 4) + ".");
        plugin.getServer().getOnlinePlayers().forEach((player -> {
            TextComponent outlineComponent = new TextComponent("[Click to Outline]");
            outlineComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                          "/outlinechunk " + chunk.getWorld().getName() + " " + (chunk.getX() << 4) + " " +
                                                          (chunk.getZ() << 4)));
            outlineComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click me to run!").create()));
        
            player.spigot().sendMessage(new ComponentBuilder("Would you like to outline the chunk?").color(ChatColor.WHITE)
                                                                                                    .append(" ")
                                                                                                    .color(ChatColor.GREEN)
                                                                                                    .append(outlineComponent)
                                                                                                    .create());
        }));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("forcechunkregen")) {
            switch (args.length) {
                case 1:
                    return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
                case 2:
                    if (sender instanceof CommandBlock)
                        return Collections.singletonList(String.valueOf(((CommandBlock) sender).getLocation().getBlockX()));
                    if (sender instanceof Entity)
                        return Collections.singletonList(String.valueOf(((Entity) sender).getLocation().getBlockX()));
                    return null;
                case 3:
                case 4:
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
