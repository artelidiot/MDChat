package me.artel.mdchat.managers;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Utility class for hooks
 */
@Accessors(fluent = true)
public class HookManager {
    @Getter
    private static boolean vault, placeholderAPI;
    @Getter
    private static Chat vaultChat = null;

    public static void init() {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault") && chatSetup()) {
            vault = true;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholderAPI = true;
        }
    }

    public static boolean chatSetup() {
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (rsp == null) {
            return false;
        }
        vaultChat = rsp.getProvider();
        return true;
    }
}
