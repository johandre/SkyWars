package vc.pvp.skywars.commands;

import com.google.common.collect.Maps;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vc.pvp.skywars.utilities.Messaging;

import java.util.Map;
import vc.pvp.skywars.config.PluginConfig;

public class MainCommand implements CommandExecutor {

    private Map<String, CommandExecutor> subCommandMap = Maps.newHashMap();

    public MainCommand() {
        subCommandMap.put("reload", new ReloadCommand());
        subCommandMap.put("setlobby", new SetLobbyCommand());
        subCommandMap.put("start", new StartCommand());
        subCommandMap.put("leave", new LeaveCommand());
        subCommandMap.put("score", new ScoreCommand());
        if (!PluginConfig.disableKits()) {
            subCommandMap.put("kit", new KitCommand());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            printHelp(sender, label);
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        if (!subCommandMap.containsKey(subCommandName)) {
            printHelp(sender, label);
            return true;
        }

        ///-------------------------------------------------------------
        if(sender instanceof Player)
        {  Player player = (Player)sender;
        CommandExecutor subCommand = subCommandMap.get(subCommandName);
        if (!hasPermission(player, subCommand)) {
            player.sendMessage(new Messaging.MessageFormatter().format("error.insufficient-permissions"));
            return true;
          }
          return subCommand.onCommand(sender, command, label, args);
        }
        else
        { if(args[0].equals("reload") || args[0].equals("score"))
          { CommandExecutor subCommand = subCommandMap.get(subCommandName);
            return subCommand.onCommand(sender, command, label, args);
          }
        }
        return true;
        ///---------------------------------------------------------------
    }

    private boolean hasPermission(CommandSender bukkitPlayer, CommandExecutor cmd) {
        CommandPermissions permissions = cmd.getClass().getAnnotation(CommandPermissions.class);
        if (permissions == null) {
            return true;
        }

        for (String permission : permissions.value()) {
            if (bukkitPlayer.hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }

    private void printHelp(CommandSender bukkitPlayer, String label) {
        bukkitPlayer.sendMessage(new Messaging.MessageFormatter().withPrefix().format("cmd.available-commands"));

        for (Map.Entry<String, CommandExecutor> commandEntry : subCommandMap.entrySet()) {
            if (hasPermission(bukkitPlayer, commandEntry.getValue())) {
                String description = "No description available.";

                CommandDescription cmdDescription = commandEntry.getValue().getClass().getAnnotation(CommandDescription.class);
                if (cmdDescription != null) {
                    description = cmdDescription.value();
                }

                bukkitPlayer.sendMessage("\2477/" + label + " " + commandEntry.getKey() + " \247f-\247e " + description);
            }
        }
    }
}
