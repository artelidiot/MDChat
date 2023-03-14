package me.artel.mdchat.impl;

import me.artel.feather.lib.minedown.MineDown;
import me.artel.feather.messaging.Messenger;
import me.artel.mdchat.MDChatPlugin;
import me.artel.mdchat.managers.FileManager;
import me.artel.mdchat.managers.HookManager;
import me.artel.mdchat.utils.MDUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {
    private static final Pattern colors = Pattern.compile("&([\\da-f])", Pattern.CASE_INSENSITIVE);
    private static final Pattern formats = Pattern.compile("&([k-or])", Pattern.CASE_INSENSITIVE);

    public static void chat(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) {
            return;
        }

        String format = formatBuilder(e.getPlayer(), e.getMessage());

        if (FileManager.getFormat().getBoolean("use-advanced-formatter", true)) {
            e.setCancelled(true);

            Messenger.sendMD(Bukkit.getConsoleSender(), format);
            for (Player player : e.getRecipients()) {
                Messenger.sendMD(player, format);
            }
        } else {
            e.setFormat(format);
        }
    }

    public static String formatBuilder(Player player, String message) {
        StringBuilder sb = new StringBuilder();
        Object format;

        if (FileManager.getFormat().get("player-formats." + player.getUniqueId()) != null) {
            format = FileManager.getFormat().get("player-formats." + player.getUniqueId());
        } else if (HookManager.vault() && FileManager.getFormat().get("group-formats." + HookManager.vaultChat().getPrimaryGroup(player)) != null) {
            format = FileManager.getFormat().get("group-formats." + HookManager.vaultChat().getPrimaryGroup(player));
        } else {
            format = FileManager.getFormat().get("format");
        }

        if (format instanceof List<?>) {
            for (var entry : (List<?>)format) {
                if (entry instanceof String) {
                    sb.append(entry);
                }
            }
        } else if (format instanceof String) {
            sb.append(format);
        } else {
            sb.append("ERROR OCCURRED PARSING FORMAT");
            MDChatPlugin.getPlugin().getLogger().warning("Invalid format detected: " + format);
        }

        return MDUtil.applyAllPlaceholders(sb.toString(), player)
                .replace("{message}", messageBuilder(player, message));
    }

    public static String messageBuilder(Player player, String message) {
        String processed = message;

        processed = MineDown.escape(processed); // TODO: Implement permission-based MineDown syntax usage

        if (player.hasPermission("mdchat.color.hex")) {
            processed = Messenger.parseHexColors(processed);
        }

        Matcher colorMatcher = colors.matcher(processed);
        while (colorMatcher.find()) {
            processed = colorMatcher.replaceAll(player.hasPermission("mdchat.color." + colorMatcher.group(1)) ? "\u00A7$1" : "&$1");
        }

        Matcher formatMatcher = formats.matcher(processed);
        while (formatMatcher.find()) {
            processed = formatMatcher.replaceAll(player.hasPermission("mdchat.format." + formatMatcher.group(1)) ? "\u00A7$1" : "&$1");
        }

        return processed;
    }
}