package me.artel.mdchat.impl;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.artel.mdchat.managers.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

@Accessors(fluent = true)
public class Announcement {
    @Getter
    public static HashMap<Announcement, String> announcements = new HashMap<>();
    private static int taskID = -69;
    @Getter
    public ConfigurationSection announcement;
    @Getter
    public String identifier;
    @Getter
    public String world;
    @Getter
    public boolean enabled;
    @Getter
    public String alignment;
    @Getter
    public Object permission;
    @Getter
    public Object sound;
    @Getter
    public String content;

    public Announcement(String identifier) {
        this.identifier = identifier;

        this.announcement = FileManager.getAnnouncer().getConfigurationSection("announcements." + identifier);

        // This should never happen
        if (this.announcement == null) {
            return;
        }

        // Initialize the announcement
        this.enabled = announcement.getBoolean("enabled", true);
        this.alignment = announcement.getString("alignment", "default");
        this.permission = announcement.get("permission", false);
        this.sound = announcement.get("sound", false);
        this.world = announcement.getString("world", "");

        this.content = announcement.getString("content", "");

        announcements.put(this, content);
    }

    public static void repopulate() {
        announcements.clear();
        stopTask();

        var announcementsSection = FileManager.getAnnouncer().getConfigurationSection("announcements");

        if (announcementsSection == null || announcementsSection.getKeys(false).isEmpty()) {
            return;
        }

        announcementsSection.getKeys(false).forEach(Announcement::new);
        startTask();
    }

    public static void startTask() {
        // Just in case
        if (taskID == -69) {
            // TODO: Figure out how to best iterate over the announcements, in order, on a timer
        }
    }

    public static void stopTask() {
        // Make sure the task ID is set
        if (taskID != -69) {
            Bukkit.getScheduler().cancelTask(taskID);
            // Revert to original ID to signify it is no longer running for future start calls
            taskID = -69;
        }
    }
}