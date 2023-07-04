package me.artel.mdchat.commands.mdchat;

import com.google.common.collect.ImmutableList;
import dev.jorel.commandapi.CommandAPICommand;
import lombok.Getter;
import me.artel.mdchat.commands.mdchat.subcommands.ClearSubCommand;
import me.artel.mdchat.commands.mdchat.subcommands.MOTDSubCommand;
import me.artel.mdchat.commands.mdchat.subcommands.ReloadSubCommand;

public class MDChatCommand {

    private static final ImmutableList<CommandAPICommand> subCommands = ImmutableList.of(
            ClearSubCommand.getInstance(),
            MOTDSubCommand.getInstance(),
            ReloadSubCommand.getInstance()
    );

    @Getter
    public static CommandAPICommand instance = new CommandAPICommand("minedownchat")
            .withAliases("mdchat")
            .withPermission("mdchat.command")
            .withSubcommands(subCommands.toArray(new CommandAPICommand[0]));
}