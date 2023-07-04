package me.artel.mdchat.managers;

import lombok.SneakyThrows;
import me.artel.feather.files.YAMLFile;
import me.artel.mdchat.MDChatPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class FileManager {
    private static final JavaPlugin plugin = MDChatPlugin.getPlugin();

    private static final YAMLFile
            announcerFile = new YAMLFile(plugin, "announcer.yml"),
            configFile = new YAMLFile(plugin, "config.yml"),
            formatFile = new YAMLFile(plugin, "format.yml"),
            localeFile = new YAMLFile(plugin, "locale.yml"), // TODO: Switch to an i18n locale system
            motdFile = new YAMLFile(plugin, "motd.yml"),
            rulesFile = new YAMLFile(plugin, "rules.yml");

    public static void init() {
        save();
        reload();
    }

    public static void save() {
        // TODO
        // announcerFile.save();
        configFile.save();
        formatFile.save();
        localeFile.save();
        motdFile.save();
        rulesFile.save();
    }

    @SneakyThrows
    public static void reload() {
        save();
        // TODO
        // announcerFile.reload();
        configFile.reload();
        formatFile.reload();
        localeFile.reload();
        motdFile.reload();
        rulesFile.reload();
    }

    public static YamlConfiguration getAnnouncer() {
        return announcerFile.getYaml();
    }

    public static YamlConfiguration getConfig() {
        return configFile.getYaml();
    }

    public static YamlConfiguration getFormat() {
        return formatFile.getYaml();
    }

    public static YamlConfiguration getLocale() {
        return localeFile.getYaml();
    }

    public static String getLocale(String path) {
        String result = getLocale().getString(path);

        if (result == null) {
            result = "Missing key in 'locale.yml': '" + path + "'";
        }

        return result;
    }

    public static YamlConfiguration getMOTD() {
        return motdFile.getYaml();
    }

    public static YamlConfiguration getRules() {
        return rulesFile.getYaml();
    }
}