package me.artel.mdchat;

import lombok.Getter;
import me.artel.feather.integration.Wrapper;
import me.artel.mdchat.commands.CommandManager;
import me.artel.mdchat.listeners.Listeners;
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
        CommandManager.init(LOAD);
    }

    @Override
    public void onEnable() {
        plugin = this;

        MDUtil.init();
        CommandManager.init(ENABLE);
        Wrapper.registerListener(this, new Listeners());

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