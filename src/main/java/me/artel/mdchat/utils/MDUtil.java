package me.artel.mdchat.utils;

import me.artel.feather.messaging.Messenger;
import me.artel.mdchat.MDChatPlugin;
import me.artel.mdchat.impl.Announcement;
import me.artel.mdchat.impl.Rule;
import me.artel.mdchat.managers.FileManager;
import me.artel.mdchat.managers.HookManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Utility class specifically for MDChat
 *
 * @author Artel
 */
public class MDUtil {

    public static void init() {
        FileManager.init();
        HookManager.init();
        Announcement.repopulate();
        Rule.repopulate();

        if (shouldEnableMetrics()) {
            new Metrics(MDChatPlugin.getPlugin(), 17942);
        }
    }

    public static void reload() {
        FileManager.reload();
        Announcement.repopulate();
        Rule.repopulate();
    }

    /* File Accessors */

    // config.yml
    public static boolean shouldEnableMetrics() {
        return FileManager.getConfig().getBoolean("metrics", true);
    }

    public static boolean shouldRequireMovement() {
        return FileManager.getConfig().getBoolean("movement-required.chat", false)
                || FileManager.getConfig().getBoolean("movement-required.commands", false);
    }

    // format.yml
    public static boolean shouldFormatChat() {
        return FileManager.getFormat().getBoolean("enabled", false);
    }

    // motd.yml
    public static boolean shouldSendMOTD() {
        return FileManager.getMOTD().getBoolean("enabled", false);
    }

    public static void sendMOTD(Player player) {
        String motd = applyAllPlaceholders(FileManager.getMOTD().getString("motd"), player);
        int delay = FileManager.getMOTD().getInt("delay", 1000);

        Bukkit.getScheduler().scheduleSyncDelayedTask(MDChatPlugin.getPlugin(),
                () -> Messenger.sendMD(player, motd),
                // We divide the delay by 50, so it works in milliseconds
                delay > 0 ? (delay / 50) : 0);
    }

    // rules.yml
    public static boolean shouldDropSigns() {
        return FileManager.getRules().getBoolean("drop-signs", false);
    }

    public static boolean shouldStripDiacriticalMarks() {
        return FileManager.getRules().getBoolean("strip-diacritical-marks", false);
    }

    /* Placeholder handling */

    public static String applyAllPlaceholders(String input, Player player) {
        String processed = input;

        processed = applyPluginPlaceholders(processed);
        processed = applyPlayerPlaceholders(processed, player);
        processed = applyPAPIPlaceholders(processed, player);

        return processed;
    }

    public static String applyPluginPlaceholders(String input) {
        String processed = input;

        if (input.startsWith("{@") && input.endsWith("}")) {
            processed = input.substring(2, input.length() - 1);
            processed = FileManager.getLocale(processed);
        }

        processed = processed
                .replace("{prefix}", FileManager.getLocale("prefix"))
                .replace("{version}", FileManager.getLocale("version"));

        return processed;
    }

    public static String applyPlayerPlaceholders(String input, Player player) {
        String processed = input;

        processed = processed
                .replace("{player-name}", player.getName())
                .replace("{player-display-name}", player.getDisplayName())
                .replace("{player_name}", player.getName())
                .replace("{player_display_name}", player.getDisplayName());

        return processed;
    }

    public static String applyPAPIPlaceholders(String input, Player player) {
        String processed = input;

        if (HookManager.placeholderAPI()) {
            processed = PlaceholderAPI.setPlaceholders(player, processed);
        }

        return processed;
    }
}