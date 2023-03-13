package me.artel.mdchat.commands;

import com.google.common.collect.ImmutableList;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIConfig;
import me.artel.mdchat.MDChatPlugin;
import me.artel.mdchat.commands.mdchat.MDChatCommand;

public class CommandManager {

    private static final ImmutableList<CommandAPICommand> commands = ImmutableList.of(
            MDChatCommand.mdChatCommand
    );

    public static void init(Stage stage) {
        switch (stage) {
            case LOAD -> CommandAPI.onLoad(new CommandAPIConfig());
            case ENABLE -> {
                CommandAPI.onEnable(MDChatPlugin.getPlugin());
                commands.forEach(CommandAPICommand::register);
            }
            case DISABLE -> commands.forEach(command -> CommandAPI.unregister(command.getName()));
        }
    }

    public enum Stage {
        LOAD, ENABLE, DISABLE
    }
}