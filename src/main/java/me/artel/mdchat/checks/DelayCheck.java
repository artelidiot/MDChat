package me.artel.mdchat.checks;

import lombok.Getter;
import me.artel.mdchat.managers.FileManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Delay check
 *
 * @author Artel
 * @version 1.0.0
 */
public class DelayCheck {
    @Getter
    private static final HashMap<Player, Long> chatDelays = new HashMap<>();
    @Getter
    private static final HashMap<Player, Long> commandDelays = new HashMap<>();

    public static boolean chat(Player player) {
        return delay(player, Action.CHAT);
    }

    public static boolean commands(Player player) {
        return delay(player, Action.COMMAND);
    }

    public static boolean delay(Player player, Action action) {
        // This check isn't enabled, do nothing
        if (FileManager.getConfig().getInt("delay." + action.getActionName(), 0) <= 0) {
            return false;
        }

        // They bypass this check, do nothing
        if (player.hasPermission("mdchat.bypass.delay." + action.getActionName())) {
            return false;
        }

        final var delays = action.equals(Action.CHAT) ? chatDelays : commandDelays;

        if (delays.containsKey(player)) {
            // Check if the elapsed time in milliseconds is longer than the delay
            if (TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - delays.get(player)))
                    >= FileManager.getConfig().getInt("delay." + action.getActionName(), 0)) {
                // Give them a new delay
                delays.put(player, System.nanoTime());
                // Let the action pass
                return false;
            }
            // They performed the action whilst on a delay, prevent the action
            return true;
        } else {
            // They aren't in the map, give them a delay
            delays.put(player, System.nanoTime());
            // Let the action pass
            return false;
        }
    }

    public static void cleanUp(Player player) {
        chatDelays.remove(player);
        commandDelays.remove(player);
    }

    private enum Action {
        CHAT("chat"), COMMAND("commands");

        @Getter
        private final String actionName;

        Action(String actionName) {
            this.actionName = actionName;
        }
    }
}