package me.artel.mdchat.commands.mdchat.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import me.artel.feather.messaging.Messenger;
import me.artel.mdchat.managers.FileManager;
import me.artel.mdchat.utils.MDUtil;

public class ReloadSubCommand {

    public static CommandAPICommand reloadSubCommand = new CommandAPICommand("reload")
            .withPermission("mdchat.command.reload")
            .withShortDescription("Reload MDChat.")
            .executes((sender, args) -> {
                MDUtil.reload();
                Messenger.sendMD(sender, MDUtil.applyPluginPlaceholders(FileManager.getLocale("command-reloaded")));
            });

}