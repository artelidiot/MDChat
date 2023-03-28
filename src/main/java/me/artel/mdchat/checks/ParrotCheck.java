package me.artel.mdchat.checks;

import info.debatty.java.stringsimilarity.JaroWinkler;
import lombok.Getter;
import me.artel.mdchat.MDChatPlugin;
import me.artel.mdchat.managers.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Parrot check
 *
 * @author Artel
 * @version 1.0.0
 */
public class ParrotCheck {
    private static final JaroWinkler jaroWinkler = new JaroWinkler();
    @Getter
    private static String latestChatMessage = null;

    public static boolean chat(Player player, String message) {
        // They bypass this check, do nothing
        if (player.hasPermission("mdchat.bypass.parrot")) {
            return false;
        }

        if (latestChatMessage == null || latestChatMessage.isBlank()) {
            return false;
        } else {
            return parrot(latestChatMessage, message);
        }
    }

    public static boolean parrot(String compareFrom, String compareTo) {
        final int minimumLength = FileManager.getConfig().getInt("parrot.minimum", 6);
        final int minimumPercentage = FileManager.getConfig().getInt("parrot.percentage", 0);

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
        if (FileManager.getConfig().getBoolean("parrot.ignore-usernames", false)) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                // Remove their username from both strings (case-sensitive)
                compareFrom = compareFrom.replace(onlinePlayer.getName(), "");
                compareTo = compareTo.replace(onlinePlayer.getName(), "");
            }
        }

        for (String excluded : FileManager.getConfig().getStringList("parrot.list")) {
            // Remove all exclusions from both strings (case-insensitive)
            compareFrom = compareFrom.replaceAll("(?i)" + excluded, "");
            compareTo = compareTo.replaceAll("(?i)" + excluded, "");
        }

        // Check if the comparative string is now empty or not reaching the length threshold after our modifications
        if (compareTo.isBlank() || compareTo.length() < minimumLength) {
            return false;
        }

        // Check if this should NOT be case-sensitive
        if (!FileManager.getConfig().getBoolean("parrot.case-sensitive", false)) {
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

    public static void setLatestChatMessage(String message) {
        int decay = FileManager.getConfig().getInt("parrot.decay", 5000);

        // Update the message
        latestChatMessage = message;

        // Check if a decay is desired
        if (decay > 0) {
            // Schedule a task to handle expiration
            Bukkit.getScheduler().scheduleSyncDelayedTask(MDChatPlugin.getPlugin(),
                    () -> latestChatMessage = null,
                    // The decay value is divided by 50, so it works in milliseconds
                    (decay / 50));
        }
    }
}