package me.artel.mdchat.checks;

import lombok.Getter;
import me.artel.mdchat.managers.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.Locale;

/**
 * Excessive uppercase check
 *
 * @author Artel
 * @version 1.0.0
 */
public class UppercaseCheck {

    public static boolean chat(Player player, String message) {
        // They bypass this check, do nothing
        if (player.hasPermission("mdchat.bypass.uppercase.chat")) {
            return false;
        }

        return excessiveUppercase(message, Action.CHAT);
    }

    public static boolean commands(Player player, String message) {
        // They bypass this check, do nothing
        if (player.hasPermission("mdchat.bypass.uppercase.commands")) {
            return false;
        }

        return excessiveUppercase(message, Action.COMMAND);
    }

    public static boolean excessiveUppercase(String input, Action action) {
        final int minimumLength = FileManager.getConfig().getInt("uppercase." + action.getActionName() + "-minimum", action.equals(Action.CHAT) ? 6 : 12);
        final int minimumPercentage = FileManager.getConfig().getInt("uppercase." + action.getActionName() + "-percentage", 0);

        // This check isn't enabled, do nothing
        if (minimumPercentage <= 0) {
            return false;
        }

        // Check if we should remove online player usernames from this check
        if (FileManager.getConfig().getBoolean("uppercase.ignore-usernames", false)) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                // Remove their username from the string (case-sensitive)
                input = input.replace(onlinePlayer.getName(), "");
            }
        }

        for (String excluded : FileManager.getConfig().getStringList("uppercase.list")) {
            // Remove all exclusions from the string (case-insensitive)
            input = input.replaceAll("(?i)" + excluded, "");
        }

        // Check if the string is a command
        if (input.startsWith("/")) {
            // Remove the command itself from the string
            input = input.substring(input.split(" ")[0].length());
        }

        // Check if the string is now empty or not reaching the length threshold after our modifications
        if (input.isBlank() || input.chars().filter(Character::isLetter).count() < minimumLength) {
            return false;
        }

        // Calculate the percentage of uppercase in the string
        // Then multiply by 100 to convert it to a 0-100 scale rather than a double
        double percentage = (input.chars()
                .filter(Character::isLetter)
                .map(entry -> Character.isUpperCase(entry) ? 1 : 0)
                .summaryStatistics()
                .getAverage()) * 100;

        // Check if the percentage meets the threshold
        return percentage >= minimumPercentage;
    }

    public static String violate(Cancellable event, String input) {
        if (FileManager.getConfig().getBoolean("uppercase.cancel", false)) {
            event.setCancelled(true);
        }

        return input.toLowerCase(Locale.ROOT);
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