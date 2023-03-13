package me.artel.mdchat.commands.mdchat.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import me.artel.feather.messaging.Messenger;
import me.artel.mdchat.managers.FileManager;
import me.artel.mdchat.utils.MDUtil;
import org.bukkit.entity.Player;

public class MOTDSubCommand {

    public static CommandAPICommand motdSubCommand = new CommandAPICommand("motd")
            .withPermission("mdchat.command.motd")
            .withShortDescription("View the MOTD from MDChat.")
            // TODO: Add support for console execution, just because
            .executesPlayer((player, args) -> {
                Messenger.sendMD(player, parseMOTD(player));
            });

    private static String parseMOTD(Player player) {
        return MDUtil.applyAllPlaceholders(FileManager.getMOTD().getString("motd"), player);
    }
}