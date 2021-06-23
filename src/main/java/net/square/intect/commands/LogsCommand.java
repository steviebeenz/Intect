package net.square.intect.commands;

import com.google.common.collect.Lists;
import net.square.intect.Intect;
import net.square.intect.utils.paster.Hastebin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class LogsCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {

        if (!commandSender.hasPermission("intect.command.logs"))
        {
            return true;
        }

        if (args.length < 2)
        {
            commandSender.sendMessage(Intect.getIntect().getPrefix() + "§c/logs <name> <size>");
            return true;
        }

        List<String> informationOfPlayer = Intect.getIntect().getMySQLManager().getInformationOfPlayer(
            args[0], Integer.parseInt(args[1]));

        if (informationOfPlayer.isEmpty())
        {
            commandSender.sendMessage(Intect.getIntect().getPrefix() + "§cList is empty");
            return true;
        }

        StringBuilder builder = new StringBuilder();

        for (String s1 : informationOfPlayer)
        {
            builder.append(s1).append("\n");
        }

        commandSender.sendMessage(Intect.getIntect().getPrefix() + "§7Try to upload the document...");

        for (String link : Intect.getIntect()
            .getConfigHandler()
            .getYamlConfiguration()
            .getStringList("logging.logs-links"))
        {

            String var = Hastebin.post(link, builder.toString());

            if (var.equals("NULL"))
            {
                commandSender.sendMessage(String.format(
                    "%s§cUpload failed on service %s", Intect.getIntect().getPrefix(), link));
                continue;
            }

            commandSender.sendMessage(
                String.format("%s§9Result §f%s", Intect.getIntect().getPrefix(), var));
            break;
        }
        return true;
    }
}
