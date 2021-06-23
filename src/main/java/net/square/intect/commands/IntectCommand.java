package net.square.intect.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.square.intect.Intect;
import net.square.intect.checks.objectable.Check;
import net.square.intect.menu.MainMenu;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IntectCommand implements CommandExecutor
{

    private final Intect intect;

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
            sendDefaultInfo(sender);
            return true;
        }

        if (args.length == 0)
        {
            sendDefaultCommandOverview(sender);
            return true;
        }

        // -> /intect <var0> <var1> <var2>
        final String var0 = args[0];

        if (var0.equalsIgnoreCase("gui"))
        {

            if (!(sender instanceof Player))
            {
                sender.sendMessage(prefix + "§cYou must be a player to open a gui");
                return true;
            }

            MainMenu.init((((Player) sender).getPlayer()));
            return true;

        }
        else if (var0.equalsIgnoreCase("version"))
        {

            sendDefaultInfo(sender);
            return true;

        }
        else if (var0.equalsIgnoreCase("update"))
        {
            sender.sendMessage(prefix + "§7Fetching newest version...");
            intect.getServer().getScheduler().runTaskAsynchronously(intect, () ->
            {
                intect.getUpdateManager().performCheck();

                int latest = intect.getUpdateManager().getLatestBuild();
                int running = Integer.parseInt(intect.getDescription().getVersion());

                fetchMoreInformation(running, sender, prefix, false);
                fetchMoreInformation(latest, sender, prefix, true);

                String s1 = formatBuildGeneric(running, latest);
                sender.sendMessage(
                    prefix + "Running Intect Build-" + running + " (" + s1.toLowerCase() + ")");

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
                sender.sendMessage(prefix + "/intect info playername: Get information about a player");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null || !target.isOnline())
            {
                sender.sendMessage(prefix + "§cCant find player. Spelling error?");
                return true;
            }

            PlayerStorage playerStorage = PlayerStorage.storageHashMap.get(target);

            sender.sendMessage("");

            sender.sendMessage(String.format("%s§7Information about §c%s", prefix, target.getName()));
            sender.sendMessage(String.format("%s§7UUID %s", prefix, target.getUniqueId().toString().replace("-", "")));
            sender.sendMessage(
                String.format("%s§7Address %s", prefix, target.getAddress().getAddress().getHostAddress()));
            sender.sendMessage(
                String.format(
                    "%s§7Sensitivity %.0f%%", prefix, playerStorage.getRotationProcessor().getFinalSensitivity()));
            sender.sendMessage("");
            sender.sendMessage(prefix + "§7Violations");

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
                sender.sendMessage(prefix + "/intect diagnostics performance: Output performance data");
                sender.sendMessage(prefix + "/intect diagnostics statistics: Output check statistics");
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
                sender.sendMessage(prefix + "/intect diagnostics performance: Output performance data");
                sender.sendMessage(prefix + "/intect diagnostics statistics: Output check statistics");
                return true;
            }

        }
        else if (var0.equalsIgnoreCase("debug"))
        {

            if (args.length == 1)
            {
                sender.sendMessage(prefix + "Available subcommands:");
                sender.sendMessage(prefix + "/intect debug modulename-type: Output debug for module");
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
        sender.sendMessage(prefix + "/intect gui: Open main menu");
        sender.sendMessage(prefix + "/intect update: Checks for update");
        sender.sendMessage(prefix + "/intect version: Show default info");
        sender.sendMessage(prefix + "/intect verbose: Enable or disable verbose output");
        sender.sendMessage(prefix + "/intect diagnostics: Show intect diagnostics");
        sender.sendMessage(prefix + "/intect debug modulename-type: Output debug for module");
        sender.sendMessage(prefix + "/intect info playername: Get information about a player");
    }

    private void fetchMoreInformation(int running, CommandSender sender, String prefix, boolean latest)
    {
        try
        {
            JsonObject run = intect.getUpdateManager()
                .readJsonFromUrl("https://jenkins.squarecode.de/job/Intect/job/master/" + running + "/api/json");

            long timestamp = run.get("timestamp").getAsLong();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);

            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int mHours = calendar.get(Calendar.HOUR);
            int mMinute = calendar.get(Calendar.MINUTE);
            int mSecs = calendar.get(Calendar.SECOND);

            if (latest)
            {
                sender.sendMessage(
                    String.format("%sBuild %d (LATEST) released at (%d.%d.%d - %d:%d:%d)", prefix, running, mDay,
                                  mMonth,
                                  mYear, mHours, mMinute, mSecs));

                sender.sendMessage(String.format("%sChanges:", prefix));

                JsonObject latestCommits = intect.getUpdateManager()
                    .readJsonFromUrl(
                        String.format("https://jenkins.squarecode.de/job/Intect/job/master/%d/api/json", running));

                JsonArray changeSets = latestCommits.get("changeSets").getAsJsonArray();
                for (JsonElement changeSet : changeSets)
                {
                    JsonObject asJsonObject = changeSet.getAsJsonObject();

                    int i = 1;

                    for (JsonElement items : asJsonObject.get("items").getAsJsonArray())
                    {
                        String msg = items.getAsJsonObject().get("msg").getAsString();

                        sender.sendMessage("§7#" + i + " - " + msg);
                        i++;
                    }
                }
            }
            else
            {
                sender.sendMessage(
                    String.format("%sBuild %d (RUNNING) released at (%d.%d.%d - %d:%d:%d)", prefix, running, mDay,
                                  mMonth,
                                  mYear, mHours, mMinute, mSecs));
            }

        } catch (IOException e)
        {
            sender.sendMessage(String.format("%sYour version: Build-%d (ERROR|IN|REQ)", prefix, running));
            sender.sendMessage(String.format("%sError: %s", prefix, e.getMessage()));
        }
    }

    private void sendDefaultInfo(CommandSender sender)
    {

        final String prefix = Intect.getIntect().getPrefix();

        sendIntectVer(sender, prefix);
        sender.sendMessage(prefix + "Made in Germany by the Intect development team");
        sender.sendMessage(prefix + "Visit our website for a full list of contributors");
    }

    private String formatBuildGeneric(int running, int latest)
    {
        String message;
        if (latest == -1)
        {
            message = "Error fetching version";
        }
        else if (running > latest)
        {
            message = "Unknown version(custom build?)";
        }
        else if (running == latest)
        {
            message = "Latest version";
        }
        else
        {
            int var = latest - running;
            message = "Outdated (" + var + " versions behind)";
        }
        return message;
    }

    private void sendIntectVer(CommandSender sender, String prefix)
    {

        int running = Integer.parseInt(intect.getDescription().getVersion());
        int latest = intect.getUpdateManager().getLatestBuild();

        String s = formatBuildGeneric(running, latest);

        sender.sendMessage(
            prefix + "Running Intect Build#" + running + " (" + s.toLowerCase() + ")");
    }
}
