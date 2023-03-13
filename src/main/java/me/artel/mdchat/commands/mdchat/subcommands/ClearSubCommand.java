package me.artel.mdchat.commands.mdchat.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import me.artel.feather.messaging.Messenger;
import me.artel.mdchat.managers.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ClearSubCommand {

    public static CommandAPICommand clearSubCommand = new CommandAPICommand("clear")
            .withPermission("mdchat.command.clear")
            .withShortDescription("Clears the chat.")
            .executes((sender, args) -> {
                clear(false);
            })
            .withSubcommand(new CommandAPICommand("silent")
                    .withAliases("s", "-silent", "-s")
                    .executes((sender, args) -> {
                        clear(true);
                    })
            );

    private static void clear(boolean silent) {
        String clear = ChatColor.translateAlternateColorCodes('&', "&0\n&8\n&7\n&f\n").repeat(69);
        String notify = FileManager.getLocale("chat-cleared");

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("mdchat.bypass.clear")) {
                continue;
            }

            player.sendMessage(clear);
            if (!silent) {
                Messenger.sendMD(player, notify);
            }
        }
    }
}