package me.artel.mdchat.impl;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.artel.feather.messaging.Messenger;
import me.artel.mdchat.MDChatPlugin;
import me.artel.mdchat.managers.FileManager;
import me.artel.mdchat.utils.MDUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Initializer for a new Rule
 *
 * @author Artel
 * @version 2.0.0
 */
@Accessors(fluent = true)
public class Rule {
    @Getter
    public static final HashMap<Rule, String> rules = new HashMap<>();
    @Getter
    private static final Pattern advertPattern = Pattern.compile("(|(?:(?:(?:https?|ftp):)?//))(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z0-9\\u00a1-\\uffff][a-z0-9\\u00a1-\\uffff_-]{0,62})?[a-z0-9\\u00a1-\\uffff]\\.)+(?:[a-z\\u00a1-\\uffff]{2,}\\.?))(?::\\d{2,5})?(?:[/?#]\\S*)?");
    @Getter
    private static final Pattern diacriticPattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    @Getter
    public ConfigurationSection rule;
    @Getter
    public String identifier;
    @Getter
    public boolean enabled, regex, cancel, replace;
    @Getter
    public String replacement, trigger, response;
    @Getter
    public List<String> commands;
    @Getter
    public boolean checkAnvils, checkBooks, checkChat, checkCommands, checkSigns;
    @Getter
    public Pattern triggerPattern;

    public Rule(String identifier) {

        // We don't need to do anything at all if rules are globally disabled
        if (!FileManager.getRules().getBoolean("enabled", false)) {
            return;
        }

        this.identifier = identifier
                .replace(" ", "_");

        this.rule = FileManager.getRules().getConfigurationSection("rules." + identifier);

        // This should never happen
        if (this.rule == null) {
            return;
        }

        this.enabled = rule.getBoolean("enabled", false);

        // We don't need to do anything else since this rule is not enabled
        if (!enabled) {
            return;
        }

        // Initialize the rule
        this.regex = rule.getBoolean("regex", false);
        this.cancel = rule.getBoolean("cancel", false);
        this.replace = rule.getBoolean("replace", false);

        this.replacement = rule.getString("replacement", "");
        this.trigger = rule.getString("trigger", "");
        this.response = rule.getString("response", "");

        this.commands = rule.getStringList("commands");

        this.checkAnvils = rule.getBoolean("check-anvils", true);
        this.checkBooks = rule.getBoolean("check-books", true);
        this.checkChat = rule.getBoolean("check-chat", true);
        this.checkCommands = rule.getBoolean("check-commands", true);
        this.checkSigns = rule.getBoolean("check-signs", true);

        if (trigger.equalsIgnoreCase("{advert-regex}")) {
            trigger = advertPattern.pattern();
        }

        // Initialize rule components
        if (regex) {
            triggerPattern = Pattern.compile("(?i)" + trigger, Pattern.CASE_INSENSITIVE);
        }

        rules.put(this, response);
    }

    public static void repopulate() {
        rules.clear();

        var rulesSection = FileManager.getRules().getConfigurationSection("rules");

        if (rulesSection == null || rulesSection.getKeys(false).isEmpty()) {
            return;
        }

        rulesSection.getKeys(false).forEach(Rule::new);
    }

    public boolean matcher(Player player, String input) {
        if (player.hasPermission("mdchat.bypass.rule." + identifier)) {
            return false;
        }

        if (MDUtil.shouldStripDiacriticalMarks()) {
            input = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll(diacriticPattern.pattern(), "");
        }

        if (regex) {
            return triggerPattern.matcher(input).find();
        } else {
            return input.toLowerCase(Locale.ROOT).contains(trigger.toLowerCase(Locale.ROOT));
        }
    }

    public String catcher(Player player, String input, Cancellable event) {
        if (event.isCancelled()) {
            return input;
        }

        if (matcher(player, input)) {
            if (replace) {
                input = input.replaceAll(regex ? triggerPattern.pattern() : trigger, replacement);
            } else {
                event.setCancelled(true);
            }
            executeCommands(player);
            sendResponse(player);
        }
        return input;
    }

    public void executeCommands(Player player) {
        if (commands == null || commands.isEmpty()) {
            return;
        }

        for (String command : commands) {
            if (command.startsWith("/")) {
                command = command.substring(1);
            }

            final String finalCommand = MDUtil.applyPlayerPlaceholders(command, player);
            Bukkit.getScheduler().callSyncMethod(MDChatPlugin.getPlugin(),
                    () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand));
        }
    }

    public void sendResponse(Player player) {
        Messenger.sendMD(player, MDUtil.applyAllPlaceholders(response, player));
    }
}