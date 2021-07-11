package net.square.intect.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.retrooper.packetevents.PacketEvents;
import net.square.intect.Intect;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class IntectCommand implements CommandExecutor, TabExecutor
{

    private final Intect intect;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public IntectCommand(Intect intect)
    {
        this.intect = intect;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args)
    {

        final String prefix = Intect.getIntect().getPrefix();

        if (!sender.hasPermission("intect.command"))
        {
            this.intect.getServer().getScheduler().runTaskAsynchronously(intect, () -> sendDefaultInfo(sender));
            return true;
        }

        if (args.length == 0)
        {
            sendDefaultCommandOverview(sender);
            return true;
        }

        // -> /intect <var0> <var1> <var2>
        final String var0 = args[0];

        if (var0.equalsIgnoreCase("version"))
        {
            this.intect.getServer().getScheduler().runTaskAsynchronously(intect, () -> sendDefaultInfo(sender));
            return true;
        }
        else if (var0.equalsIgnoreCase("update"))
        {
            intect.getServer().getScheduler().runTaskAsynchronously(intect, () ->
            {
                intect.getUpdateManager().performCheck();

                int latest = intect.getUpdateManager().getLatestBuild();
                int running = Integer.parseInt(intect.getDescription().getVersion());

                sender.sendMessage("");
                sender.sendMessage(prefix + "§8» §7Running version");
                fetchMoreInformation(running, sender, prefix, false);

                sender.sendMessage("");
                sender.sendMessage(prefix + "§8» §7Latest version");
                fetchMoreInformation(latest, sender, prefix, true);
                sender.sendMessage("");
            });
            return true;
        }
        else if (var0.equalsIgnoreCase("verbose"))
        {
            // Verbose sub command

            if (!(sender instanceof Player))
            {
                sender.sendMessage(prefix + "§cYou must be a player to execute this command!");
                return true;
            }

            final Player player = (Player) sender;

            if (this.intect.getStorageManager().getVerboseMode().contains(player))
            {

                this.intect.getStorageManager().getVerboseMode().remove(player);
                player.sendMessage(prefix + "You are §cno longer §7receiving verbose output");

            }
            else
            {
                this.intect.getStorageManager().getVerboseMode().add(player);
                player.sendMessage(prefix + "You are §anow §7receiving verbose output");

            }

            return true;

        }
        else if (var0.equalsIgnoreCase("info"))
        {

            if (args.length == 1)
            {
                sender.sendMessage(prefix + "Available subcommands:");
                sender.sendMessage(prefix + "/intect (info) (playerName): Get information about a player");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null || !target.isOnline())
            {
                sender.sendMessage(prefix + "§cCant find player. Spelling error?");
                return true;
            }

            PlayerStorage playerStorage = PlayerStorage.storageHashMap.get(target);

            sender.sendMessage(String.format("%s§7Information about §c%s", prefix, target.getName()));
            sender.sendMessage(String.format("%s§7UUID: %s", prefix, target.getUniqueId().toString().replace("-", "")));
            sender.sendMessage(
                String.format("%s§7Address: %s", prefix, target.getAddress().getAddress().getHostAddress()));
            sender.sendMessage(
                String.format(
                    "%s§7Sensitivity: %.0f%%", prefix, playerStorage.getRotationProcessor().getFinalSensitivity()));
            sender.sendMessage(
                String.format("%s§7Version: %s", prefix,
                              PacketEvents.get()
                                  .getPlayerUtils()
                                  .getClientVersion(target)
                                  .name()));
            sender.sendMessage(String.format("%s§7Loaded: %d", prefix, playerStorage.getChecks().size()));
            sender.sendMessage("");
            sender.sendMessage(prefix + "§7Violations:");

            List<Check> collect = new ArrayList<>();
            for (Check check111 : playerStorage.getChecks())
            {
                if (check111.getVerbose() > 0)
                {
                    collect.add(check111);
                }
            }

            if (collect.size() == 0)
            {
                sender.sendMessage(prefix + "§cNo violations found");
                return true;
            }

            for (Check check : collect)
            {
                sender.sendMessage(
                    prefix + " §8- §7" + check.getCheckInfo().name() + " " + check.getCheckInfo().type() + " ("
                        + check.getVerbose() + ")");
            }
            return true;

        }
        else if (var0.equalsIgnoreCase("diagnostics"))
        {
            // Diagnostics sub command

            if (args.length == 1)
            {
                sender.sendMessage(prefix + "Available subcommands:");
                sender.sendMessage(prefix + "/intect (diagnostics) (performance): Output performance data");
                sender.sendMessage(prefix + "/intect (diagnostics) (statistics): Output check statistics");
                return true;
            }

            final String var1 = args[1];

            if (var1.equalsIgnoreCase("performance"))
            {

                sender.sendMessage(prefix + "§cCurrently unavailable!");
                return true;

            }
            else if (var1.equalsIgnoreCase("statistics"))
            {

                sender.sendMessage(prefix + "§cCurrently unavailable!");
                return true;

            }
            else
            {
                sender.sendMessage(prefix + "Available subcommands:");
                sender.sendMessage(prefix + "/intect (diagnostics) (performance): Output performance data");
                sender.sendMessage(prefix + "/intect (diagnostics) (statistics): Output check statistics");
                return true;
            }

        }
        else if (var0.equalsIgnoreCase("debug"))
        {

            if (args.length == 1)
            {
                sender.sendMessage(prefix + "Available subcommands:");
                sender.sendMessage(prefix + "/intect (debug) (moduleName-type): Output debug for module");
                return true;
            }

            if (!(sender instanceof Player))
            {
                sender.sendMessage(prefix + "§cYou must be a player to execute this command!");
                return true;
            }

            final Player player = (Player) sender;
            final String module = args[1].toLowerCase();

            PlayerStorage playerStorage = PlayerStorage.storageHashMap.get(player);
            List<Check> checks = playerStorage.getChecks();

            for (Check check : checks)
            {
                String s1 = check.getCheckInfo().name().toLowerCase() + "-" + check.getCheckInfo().type().toLowerCase();
                if (s1.equalsIgnoreCase(module))
                {

                    List<Player> debugMode = check.getDebugMode();

                    if (debugMode.contains(player))
                    {
                        player.sendMessage(prefix + "You are §cno longer §7receiving debug output for module §c" + s1);
                        debugMode.remove(player);
                    }
                    else
                    {
                        player.sendMessage(prefix + "You are §anow §7receiving debug output for module §c" + s1);
                        debugMode.add(player);
                    }
                }
            }
            return true;

        }
        else
        {
            sendDefaultCommandOverview(sender);
            return true;
        }
    }

    private void sendDefaultCommandOverview(CommandSender sender)
    {

        final String prefix = Intect.getIntect().getPrefix();

        sender.sendMessage(prefix + "Available subcommands:");
        sender.sendMessage(prefix + "/intect (update): Checks for update");
        sender.sendMessage(prefix + "/intect (version): Show default info");
        sender.sendMessage(prefix + "/intect (verbose): Enable or disable verbose output");
        sender.sendMessage(prefix + "/intect (diagnostics): Show intect diagnostics");
        sender.sendMessage(prefix + "/intect (debug) (moduleName-type): Output debug for module");
        sender.sendMessage(prefix + "/intect (info) (playerName): Get information about a player");
    }

    private void fetchMoreInformation(int running, CommandSender sender, String prefix, boolean latest)
    {
        try
        {
            JsonObject run = intect.getUpdateManager()
                .readJsonFromUrl("https://jenkins.squarecode.de/job/Intect/job/master/" + running + "/api/json");

            long timestamp = run.get("timestamp").getAsLong();
            LocalDateTime timestampDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());

            String formattedDateTime = timestampDateTime.format(this.dateTimeFormatter);
            if (latest)
            {
                sender.sendMessage(
                    String.format("%sBuild #%d released at (%s)", prefix, running, formattedDateTime));

                sender.sendMessage(String.format("%sChanges:", prefix));

                JsonObject latestCommits = intect.getUpdateManager()
                    .readJsonFromUrl(
                        String.format("https://jenkins.squarecode.de/job/Intect/job/master/%d/api/json", running));

                JsonArray changeSets = latestCommits.get("changeSets").getAsJsonArray();
                for (JsonElement changeSet : changeSets)
                {
                    JsonObject asJsonObject = changeSet.getAsJsonObject();

                    for (JsonElement item : asJsonObject.get("items").getAsJsonArray())
                    {
                        sender.sendMessage(
                            prefix + " §8- §7" + item.getAsJsonObject().get("msg").getAsString());
                    }
                }
            }
            else
            {
                sender.sendMessage(
                    String.format("%sBuild #%d released at (%s)", prefix, running, formattedDateTime));
            }
        } catch (IOException e)
        {
            sender.sendMessage(String.format("%sError fetching ID: Build-%d (ERROR|IN|REQ)", prefix, running));
            sender.sendMessage(String.format("%sError: %s", prefix, e.getMessage()));
        }
    }

    private void sendDefaultInfo(CommandSender sender)
    {

        final String prefix = Intect.getIntect().getPrefix();

        sendIntectVersion(sender, prefix);
        sender.sendMessage(String.format("%sMade in Germany by the Intect development team", prefix));
        sender.sendMessage(String.format("%sVisit our website for a full list of contributors", prefix));
        sender.sendMessage(String.format("%sLicensed for %s", prefix, intect.getState()[4]));
    }

    private void sendIntectVersion(CommandSender sender, String prefix)
    {

        int running = Integer.parseInt(intect.getDescription().getVersion());

        try
        {
            JsonObject latestCommits = intect.getUpdateManager()
                .readJsonFromUrl(
                    String.format("https://jenkins.squarecode.de/job/Intect/job/master/%d/api/json", running));

            sender.sendMessage(
                prefix + "Running Intect v" + running + " (" + DateUtils.calculateTimeAgo(
                    latestCommits.get("timestamp").getAsLong()) + ")");

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String mainCommand,
                                      String[] strings)
    {
        if (strings.length == 1)
        {
            List<String> tempMap = new ArrayList<>();
            tempMap.add("update");
            tempMap.add("version");
            tempMap.add("verbose");
            tempMap.add("diagnostics");
            tempMap.add("debug");
            tempMap.add("info");
            return tempMap;
        }
        else if (strings.length > 1)
        {
            String var = strings[0];

            if (var.equalsIgnoreCase("debug"))
            {
                List<String> tempMap = new ArrayList<>();

                for (Check check : PlayerStorage.storageHashMap.get((Player) commandSender).getChecks())
                {
                    CheckInfo checkInfo = check.getCheckInfo();
                    tempMap.add(checkInfo.name() + "-" + checkInfo.type());
                }
                return tempMap;
            }
            else if (var.equalsIgnoreCase("info"))
            {
                List<String> tempMap = new ArrayList<>();

                for (Player onlinePlayer : this.intect.getServer().getOnlinePlayers())
                {
                    tempMap.add(onlinePlayer.getName());
                }

                return tempMap;
            }
            else if (var.equalsIgnoreCase("diagnostics"))
            {
                List<String> tempMap = new ArrayList<>();

                tempMap.add("performance");
                tempMap.add("statistics");

                return tempMap;
            }
        }
        return null;
    }
}
