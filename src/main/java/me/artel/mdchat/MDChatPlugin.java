package me.artel.mdchat;

import lombok.Getter;
import me.artel.mdchat.commands.CommandManager;
import me.artel.mdchat.utils.MDUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import static me.artel.mdchat.commands.CommandManager.Stage.*;

public class MDChatPlugin extends JavaPlugin {
    @Getter
    private static MDChatPlugin plugin;
    @Getter
    private static Metrics metrics;

    @Override
    public void onLoad() {
        plugin = this;

        CommandManager.init(LOAD);
    }

    @Override
    public void onEnable() {
        MDUtil.init();
        CommandManager.init(ENABLE);

        if (MDUtil.shouldEnableMetrics()) {
            metrics = new Metrics(MDChatPlugin.getPlugin(), 17942);
        }
    }

    @Override
    public void onDisable() {
        CommandManager.init(DISABLE);

        if (metrics != null) {
            metrics.shutdown();
        }
    }
}