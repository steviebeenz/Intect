package net.square.intect.commands;

import net.square.intect.Intect;
import net.square.intect.checks.objectable.Check;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.processor.manager.UpdateManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        if (var0.equalsIgnoreCase("version")) {

            sendDefaultInfo(sender);
            return true;

        } else if (var0.equalsIgnoreCase("verbose")) {
            // Verbose sub command

            if (!(sender instanceof Player)) {
                sender.sendMessage(prefix + "§cYou must be a player to execute this command!");
                return true;
            }

            final Player player = (Player) sender;

            if (this.intect.getStorageManager().getVerboseMode().contains(player)) {

                this.intect.getStorageManager().getVerboseMode().remove(player);
                player.sendMessage(prefix + "You are §cno longer §7receiving verbose output");

            } else {

                this.intect.getStorageManager().getVerboseMode().add(player);
                player.sendMessage(prefix + "You are §anow §7receiving verbose output");

            }

            return true;

        } else if (var0.equalsIgnoreCase("info")) {

            if (args.length == 1) {
                sender.sendMessage(prefix + "Available subcommands:");
                sender.sendMessage(prefix + "/intect info playername: Get information about a player");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null || !target.isOnline()) {
                sender.sendMessage(prefix + "§cCant find player. Spelling error?");
                return true;
            }

            PlayerStorage playerStorage = PlayerStorage.storageHashMap.get(target);

            sender.sendMessage(String.format("%s§7Information about §c%s", prefix, target.getName()));
            sender.sendMessage(String.format("%s§7UUID: %s", prefix, target.getUniqueId()));
            sender.sendMessage(
                String.format("%s§7Address: %s", prefix, target.getAddress().getAddress().getHostAddress()));
            sender.sendMessage(
                String.format(
                    "%s§7Sensitivity: %.3f%%", prefix, playerStorage.getRotationProcessor().getFinalSensitivity()));
            sender.sendMessage("");
            sender.sendMessage(prefix + "§7Violations:");

            List<Check> collect = new ArrayList<>();
            for (Check check111 : playerStorage.getChecks()) {
                if (check111.getTestCount() > 0) {
                    collect.add(check111);
                }
            }

            if (collect.size() == 0) {
                sender.sendMessage(prefix + "§cNo violations found");
                return true;
            }

            for (Check check : collect) {
                sender.sendMessage(
                    prefix + " §8- §7" + check.getCheckInfo().name() + " (" + check.getCheckInfo().type() + ") - "
                        + check.getTestCount());
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

        } else if (var0.equalsIgnoreCase("debug")) {

            if (args.length == 1) {
                sender.sendMessage(prefix + "Available subcommands:");
                sender.sendMessage(prefix + "/intect debug modulename-type: Output debug for module");
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(prefix + "§cYou must be a player to execute this command!");
                return true;
            }

            final Player player = (Player) sender;
            final String module = args[1].toLowerCase();

            PlayerStorage playerStorage = PlayerStorage.storageHashMap.get(player);
            List<Check> checks = playerStorage.getChecks();

            for (Check check : checks) {
                String s1 = check.getCheckInfo().name().toLowerCase() + "-" + check.getCheckInfo().type().toLowerCase();
                if (s1.equalsIgnoreCase(module)) {

                    List<Player> debugMode = check.getDebugMode();

                    if (debugMode.contains(player)) {

                        player.sendMessage(prefix + "You are §cno longer §7receiving debug output for module §c" + s1);
                        debugMode.remove(player);

                    } else {

                        player.sendMessage(prefix + "You are §anow §7receiving debug output for module §c" + s1);
                        debugMode.add(player);
                    }
                }
            }
            return true;

        } else {
            sendDefaultCommandOverview(sender);
            return true;
        }
    }

    private void sendDefaultCommandOverview(CommandSender sender) {

        final String prefix = Intect.getIntect().getPrefix();

        sender.sendMessage(prefix + "Available subcommands:");
        sender.sendMessage(prefix + "/intect version: Show default info");
        sender.sendMessage(prefix + "/intect verbose: Enable or disable verbose output");
        sender.sendMessage(prefix + "/intect diagnostics: Show intect diagnostics");
        sender.sendMessage(prefix + "/intect debug modulename-type: Output debug for module");
        sender.sendMessage(prefix + "/intect info playername: Get information about a player");
    }

    private void sendDefaultInfo(CommandSender sender) {

        final String prefix = Intect.getIntect().getPrefix();

        sendIntectVer(sender, prefix);
        sender.sendMessage(prefix + "Made in Germany by the Intect development team");
        sender.sendMessage(prefix + "Visit our website for a full list of contributors");
    }

    private void sendIntectVer(CommandSender sender, String prefix) {

        int running = Integer.parseInt(intect.getDescription().getVersion());
        int latest = UpdateManager.getLatestBuild();

        String message;
        if (latest == -1) {
            message = "Error fetching version";
        } else if (running > latest) {
            message = "Unknown version(custom build?)";
        } else if (running == latest) {
            message = "Latest version";
        } else {
            int var = latest - running;
            message = "Outdated (" + var + " versions behind)";
        }

        sender.sendMessage(
            prefix + "Running Intect v" + running + " (" + message.toLowerCase() + ")");
    }
}
