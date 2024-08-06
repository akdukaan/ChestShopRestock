package com.birdflop.chestshoprestock;

import com.google.common.base.Throwables;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Lang {
    private static YamlConfiguration config;

    public static String COMMAND_NO_PERMISSION = "<red>You do not have permission for that command.";
    public static String RESTOCK_SUCCESS = "<gold>Your shops have been restocked!";
    public static String COMMAND_PLAYERS_ONLY = "<red>That command can only be used by players.";
    public static String RELOAD_SUCCESS = "<gold>Plugin reloaded!";

    private static void init() {
        COMMAND_NO_PERMISSION = getString("command-no-permission", COMMAND_NO_PERMISSION);
        RESTOCK_SUCCESS = getString("restock-success", RESTOCK_SUCCESS);
        COMMAND_PLAYERS_ONLY = getString("command-players-only", COMMAND_PLAYERS_ONLY);
        RELOAD_SUCCESS = getString("reload-success", RELOAD_SUCCESS);
    }

    // ############################  DO NOT EDIT BELOW THIS LINE  ############################

    /**
     * Reload the language file
     */
    public static void reload() {
        File langFile = new File(ChestShopRestock.plugin.getDataFolder(), "lang.yml");
        config = new YamlConfiguration();
        try {
            config.load(langFile);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load " + "lang.yml", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header("This is the language file for " + ChestShopRestock.plugin.getName());
        config.options().copyDefaults(true);

        Lang.init();

        try {
            config.save(langFile);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + langFile, ex);
        }
    }

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    /**
     * Sends a message to a recipient
     *
     * @param recipient Recipient of message
     * @param message   Message to send
     */
    public static void sendMessage(@NotNull CommandSender recipient, String message) {
        recipient.sendRichMessage(message);
    }
}