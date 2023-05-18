package me.artel.mdchat.listeners;

import me.artel.mdchat.checks.DelayCheck;
import me.artel.mdchat.checks.MovementCheck;
import me.artel.mdchat.checks.SimilarityCheck;
import me.artel.mdchat.utils.MDUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DataListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (MDUtil.shouldRequireMovement()) {
            MovementCheck.getJoinLocations().put(e.getPlayer(), e.getPlayer().getLocation());
        }

        if (MDUtil.shouldSendMOTD()) {
            MDUtil.sendMOTD(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        DelayCheck.cleanUp(e.getPlayer());
        MovementCheck.cleanUp(e.getPlayer());
        SimilarityCheck.cleanUp(e.getPlayer());
    }
}