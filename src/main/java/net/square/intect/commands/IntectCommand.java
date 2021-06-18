package net.square.intect.commands;

import net.square.intect.Intect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IntectCommand implements CommandExecutor {

    private final Intect intect;

    public IntectCommand(Intect intect) {
        this.intect = intect;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        final String prefix = Intect.getIntect().getPrefix();

        if (!sender.hasPermission("intect.command")) {
            sendDefaultInfo(sender);
            return true;
        }

        if (args.length == 0) {
            sendDefaultCommandOverview(sender);
            return true;
        }

        // -> /intect <var0> <var1> <var2>
        final String var0 = args[0];

        if (var0.equalsIgnoreCase("verbose")) {
            // Verbose sub command

            if (!(sender instanceof Player)) {
                sender.sendMessage(prefix + "§cYou must be a player to execute this command!");
                return true;
            }

            final Player player = (Player) sender;

            if (this.intect.getVerboseMode().contains(player)) {

                this.intect.getVerboseMode().remove(player);
                player.sendMessage(prefix + "You are §cno longer §7receiving verbose output");

            } else {

                this.intect.getVerboseMode().add(player);
                player.sendMessage(prefix + "You are §anow §7receiving verbose output");

            }

            return true;

        } else if (var0.equalsIgnoreCase("diagnostics")) {
            // Diagnostics sub command

            if (args.length == 1) {
                sender.sendMessage(prefix + "Available subcommands:");
                sender.sendMessage(prefix + "/intect diagnostics performance: Output performance data");
                sender.sendMessage(prefix + "/intect diagnostics statistics: Output check statistics");
                return true;
            }

            final String var1 = args[1];

            if (var1.equalsIgnoreCase("performance")) {

                sender.sendMessage(prefix + "§cCurrently unavailable!");
                return true;

            } else if (var1.equalsIgnoreCase("statistics")) {

                sender.sendMessage(prefix + "§cCurrently unavailable!");
                return true;

            } else {
                sender.sendMessage(prefix + "Available subcommands:");
                sender.sendMessage(prefix + "/intect diagnostics performance: Output performance data");
                sender.sendMessage(prefix + "/intect diagnostics statistics: Output check statistics");
                return true;
            }

        } else {
            sendDefaultCommandOverview(sender);
            return true;
        }
    }

    private void sendDefaultCommandOverview(CommandSender sender) {

        final String prefix = Intect.getIntect().getPrefix();

        sender.sendMessage(prefix + "Available subcommands:");
        sender.sendMessage(prefix + "/intect verbose: Enable or disable verbose output");
        sender.sendMessage(prefix + "/intect diagnostics: Show intect diagnostics");
    }

    private void sendDefaultInfo(CommandSender sender) {

        final String prefix = Intect.getIntect().getPrefix();

        sender.sendMessage(
            String.format(
                "%sRunning Intect %s (one day old)", prefix, Intect.getIntect().getDescription().getVersion()));
        sender.sendMessage(prefix + "Made in Germany by the Intect development team");
        sender.sendMessage(prefix + "Visit our website for a full list of contributors");
        sender.sendMessage(prefix + "Certified for SquareCode (verified)");
    }
}
