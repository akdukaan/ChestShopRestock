package com.birdflop.chestshoprestock;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandCsrestock implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.hasPermission("chestshoprestock.command.reload")) {
            Lang.sendMessage(commandSender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }
        if (args.length == 0) return false;
        if (args[0].equalsIgnoreCase("reload")) {
            Lang.reload();
            Lang.sendMessage(commandSender, Lang.RELOAD_SUCCESS);
            return true;
        }
        return false;
    }
}
