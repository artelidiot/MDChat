package me.artel.mdchat.commands.mdchat.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import lombok.Getter;
import me.artel.feather.messaging.Messenger;
import me.artel.mdchat.managers.FileManager;
import me.artel.mdchat.utils.MDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearSubCommand {

    // This text is not configurable, therefore it does not need to be recreated each run.
    private static final String clear = ChatColor
            .translateAlternateColorCodes('&', "&0\n&8\n&7\n&f\n")
            .repeat(69);

    @Getter
    public static CommandAPICommand instance = new CommandAPICommand("clear")
            .withPermission("mdchat.command.clear")
            .withShortDescription("Clears the chat.")
            .executes((sender, args) -> {
                clear(sender, false);
            })
            .withSubcommand(new CommandAPICommand("silent")
                    .withAliases("s", "-silent", "-s")
                    .executes((sender, args) -> {
                        clear(sender, true);
                    })
            );

    private static void clear(CommandSender sender, boolean silent) {
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

        Messenger.sendMD(sender, MDUtil.applyPluginPlaceholders(FileManager.getLocale("command-cleared")));
    }
}