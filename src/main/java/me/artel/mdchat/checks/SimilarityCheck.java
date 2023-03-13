package me.artel.mdchat.checks;

import info.debatty.java.stringsimilarity.JaroWinkler;
import lombok.Getter;
import me.artel.mdchat.managers.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;

/**
 * Similarity check
 *
 * @author Artel
 * @version 1.0.0
 */
public class SimilarityCheck {
    private static final JaroWinkler jaroWinkler = new JaroWinkler();
    @Getter
    private static final HashMap<Player, String> chatHistory = new HashMap<>();
    @Getter
    private static final HashMap<Player, String> commandHistory = new HashMap<>();

    public static boolean chat(Player player, String message) {
        // They bypass this check, do nothing
        if (player.hasPermission("mdchat.bypass.similarity.chat")) {
            return false;
        }

        // Check if they have a previous action to compare to
        if (chatHistory.containsKey(player)) {
            // Compare their latest action to the previous one, prevent it if they're too similar
            return similarity(chatHistory.get(player), message, Action.CHAT);
        } else {
            // They did not have a previous action
            chatHistory.put(player, message);
        }

        // Let the action pass
        return false;
    }

    public static boolean commands(Player player, String command) {
        // They bypass this check, do nothing
        if (player.hasPermission("mdchat.bypass.similarity.commands")) {
            return false;
        }

        // Check if they have a previous action to compare to
        if (commandHistory.containsKey(player)) {
            // Compare their latest action to the previous one, prevent it if they're too similar
            return similarity(commandHistory.get(player), command, Action.COMMAND);
        } else {
            // They did not have a previous action
            commandHistory.put(player, command);
        }

        // Let the action pass
        return false;
    }

    public static boolean similarity(String compareFrom, String compareTo, Action action) {
        final int minimumLength = FileManager.getConfig().getInt("similarity." + action.getActionName() + "-minimum", action.equals(Action.CHAT) ? 6 : 12);
        final int minimumPercentage = FileManager.getConfig().getInt("similarity." + action.getActionName() + "-percentage", 0);

        // This check isn't enabled, do nothing
        if (minimumPercentage <= 0) {
            return false;
        }

        // We can't do a comparison check if one or the other doesn't exist
        if (compareFrom == null || compareTo == null) {
            return false;
        } else {
            // Remove color codes since we don't care about them
            compareFrom = ChatColor.stripColor(compareFrom);
            compareTo = ChatColor.stripColor(compareTo);
        }

        // Check if we should remove online player usernames from this check
        if (FileManager.getConfig().getBoolean("similarity.ignore-usernames", false)) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                // Remove their username from both strings (case-sensitive)
                compareFrom = compareFrom.replace(onlinePlayer.getName(), "");
                compareTo = compareTo.replace(onlinePlayer.getName(), "");
            }
        }

        for (String excluded : FileManager.getConfig().getStringList("similarity.list")) {
            // Remove all exclusions from both strings (case-insensitive)
            compareFrom = compareFrom.replaceAll("(?i)" + excluded, "");
            compareTo = compareTo.replaceAll("(?i)" + excluded, "");
        }

        // Check if the comparative string is now empty or not reaching the length threshold after our modifications
        if (compareTo.isBlank() || compareTo.length() < minimumLength) {
            return false;
        }

        // Check if this should NOT be case-sensitive
        if (!FileManager.getConfig().getBoolean("similarity.case-sensitive", false)) {
            // Normalize both of the strings to be lowercase
            compareFrom = compareFrom.toLowerCase(Locale.ROOT);
            compareTo = compareTo.toLowerCase(Locale.ROOT);
        }

        // Calculate the percentage between the two strings using the JaroWinkler method
        // Then multiply by 100 to convert it to a 0-100 scale
        double percentageJaroWinkler = jaroWinkler.similarity(compareFrom, compareTo) * 100;

        // Check if the percentage meets the threshold
        return percentageJaroWinkler >= minimumPercentage;
    }

    public static void cleanUp(Player player) {
        chatHistory.remove(player);
        commandHistory.remove(player);
    }

    private enum Action {
        CHAT("chat"), COMMAND("command");

        @Getter
        private final String actionName;

        Action(String actionName) {
            this.actionName = actionName;
        }
    }
}