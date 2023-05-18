package me.artel.mdchat.listeners;

import me.artel.feather.messaging.Messenger;
import me.artel.mdchat.checks.*;
import me.artel.mdchat.impl.Formatter;
import me.artel.mdchat.impl.Rule;
import me.artel.mdchat.managers.FileManager;
import me.artel.mdchat.utils.MDUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEditBookEvent;

import java.util.Arrays;

public class RuleListeners implements Listener {

    @EventHandler // No coyotes were harmed in the making of this listener
    public void onPrepareAnvil(PrepareAnvilEvent e) {
        String renameText = e.getInventory().getRenameText();

        if (renameText == null || renameText.isBlank()) {
            return;
        }

        for (Rule rule : Rule.rules().keySet()) {
            if (rule.checkAnvils() && rule.matcher((Player) e.getView().getPlayer(), renameText)) {
                if (rule.cancel() || rule.replace()) {
                    e.setResult(null);
                    break;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEditBook(PlayerEditBookEvent e) {
        String bookContents = (e.getNewBookMeta().getTitle() + e.getNewBookMeta().getPages()).trim();

        for (Rule rule : Rule.rules().keySet()) {
            if (rule.checkBooks() && rule.matcher(e.getPlayer(), bookContents)) {
                if (rule.cancel() || rule.replace()) {
                    e.setSigning(false);
                    e.setCancelled(true);
                    e.getPlayer().updateInventory();
                    rule.sendResponse(e.getPlayer());
                    break;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (DelayCheck.chat(e.getPlayer())) {
            Messenger.sendMD(e.getPlayer(), FileManager.getLocale("chat-delay"));
            e.setCancelled(true);
            return;
        }

        if (MovementCheck.chat(e.getPlayer())) {
            Messenger.sendMD(e.getPlayer(), FileManager.getLocale("chat-movement"));
            e.setCancelled(true);
            return;
        }

        if (SimilarityCheck.chat(e.getPlayer(), e.getMessage())) {
            Messenger.sendMD(e.getPlayer(), FileManager.getLocale("chat-similarity"));
            e.setCancelled(true);
            return;
        }

        if (ParrotCheck.chat(e.getPlayer(), e.getMessage())) {
            Messenger.sendMD(e.getPlayer(), FileManager.getLocale("chat-parrot"));
            e.setCancelled(true);
            return;
        }

        if (UppercaseCheck.chat(e.getPlayer(), e.getMessage())) {
            Messenger.sendMD(e.getPlayer(), FileManager.getLocale("chat-uppercase"));
            e.setMessage(UppercaseCheck.violate(e, e.getMessage()));
            if (e.isCancelled()) {
                return;
            }
        }

        for (Rule rule : Rule.rules().keySet()) {
            if (rule.checkChat() && rule.matcher(e.getPlayer(), e.getMessage())) {
                e.setMessage(rule.catcher(e.getPlayer(), e.getMessage(), e));
            }
        }

        if (MDUtil.shouldFormatChat()) {
            Formatter.chat(e);
        }

        ParrotCheck.setLatestChatMessage(e.getMessage());
        SimilarityCheck.getChatHistory().put(e.getPlayer(), e.getMessage());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (DelayCheck.commands(e.getPlayer())) {
            Messenger.sendMD(e.getPlayer(), FileManager.getLocale("command-delay"));
            e.setCancelled(true);
            return;
        }

        if (MovementCheck.commands(e.getPlayer())) {
            Messenger.sendMD(e.getPlayer(), FileManager.getLocale("command-movement"));
            e.setCancelled(true);
            return;
        }

        if (SimilarityCheck.commands(e.getPlayer(), e.getMessage())) {
            Messenger.sendMD(e.getPlayer(), FileManager.getLocale("command-similarity"));
            e.setCancelled(true);
            return;
        }

        if (UppercaseCheck.commands(e.getPlayer(), e.getMessage())) {
            Messenger.sendMD(e.getPlayer(), FileManager.getLocale("command-uppercase"));
            e.setMessage(UppercaseCheck.violate(e, e.getMessage()));
            if (e.isCancelled()) {
                return;
            }
        }

        for (Rule rule : Rule.rules().keySet()) {
            if (rule.checkCommands() && rule.matcher(e.getPlayer(), e.getMessage())) {
                e.setMessage(rule.catcher(e.getPlayer(), e.getMessage(), e));
            }
        }

        SimilarityCheck.getCommandHistory().put(e.getPlayer(), e.getMessage());
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignChange(SignChangeEvent e) {
        String sign = Arrays.toString(e.getLines()).trim();

        for (Rule rule : Rule.rules().keySet()) {
            if (rule.checkSigns() && rule.matcher(e.getPlayer(), sign)) {
                if (rule.cancel() || rule.replace()) {
                    if (MDUtil.shouldDropSigns()) {
                        e.getBlock().breakNaturally();
                    }
                    e.setCancelled(true);
                    rule.sendResponse(e.getPlayer());
                    break;
                }
            }
        }
    }
}