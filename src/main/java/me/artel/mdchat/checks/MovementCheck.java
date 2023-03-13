package me.artel.mdchat.checks;

import lombok.Getter;
import me.artel.mdchat.managers.FileManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Movement check
 *
 * @author Artel
 * @version 1.0.0
 */
public class MovementCheck {
    @Getter
    private static final HashMap<Player, Location> joinLocations = new HashMap<>();

    public static boolean chat(Player player) {
        return movement(player, Action.CHAT);
    }

    public static boolean commands(Player player) {
        return movement(player, Action.COMMAND);
    }

    public static boolean movement(Player player, Action action) {
        // This check isn't enabled, do nothing
        if (!FileManager.getConfig().getBoolean("movement-required." + action.getActionName(), false)) {
            return false;
        }

        // They bypass this check, do nothing
        if (player.hasPermission("mdchat.bypass.movement." + action.getActionName())) {
            return false;
        }

        // They have moved, do nothing
        if (!joinLocations.containsKey(player)) {
            return false;
        }

        // Check if they have moved
        if (magnitudeOfDifference(player, 2)) {
            // Remove them from the list, so we don't waste resources calculating this every time
            joinLocations.remove(player);
            // Let the action pass
            return false;
        }

        // They have not moved, prevent the action
        return true;
    }

    public static boolean magnitudeOfDifference(Player player, int differenceThreshold) {
        // Get the location they were at when they logged in
        final Location from = joinLocations.get(player);
        // Get their current location
        final Location to = player.getLocation();

        // This should never happen
        if (from == null) {
            return false;
        }

        // Subtract their join location coordinates from their current location coordinates
        final int differenceX = from.getBlockX() - to.getBlockX();
        final int differenceY = from.getBlockY() - to.getBlockY();
        final int differenceZ = from.getBlockZ() - to.getBlockZ();

        // Add up the differences, so we get the total distance they have moved
        final int difference = differenceX + differenceY + differenceZ;

        // Check if they have moved beyond the given threshold, on either axis
        return difference >= differenceThreshold || difference <= -differenceThreshold;
    }

    public static void cleanUp(Player player) {
        joinLocations.remove(player);
    }

    private enum Action {
        CHAT("chat"), COMMAND("commands");

        @Getter
        private final String actionName;

        Action(String actionName) {
            this.actionName = actionName;
        }
    }
}