package com.birdflop.chestshoprestock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;


public class Database {
    public final String DATABASE_NAME = "database";
    public final String TABLE_NAME = "chestshops";

    ChestShopRestock plugin;
    Connection connection;

    public Database(ChestShopRestock instance){
        plugin = instance;
        init();
    }

    private void init() {
        createDatabaseFile();
        connection = getSQLConnection();
        if (connection == null) {
            plugin.getLogger().log(Level.SEVERE,"Connection to database was lost");
        }
        try {
            Statement s = connection.createStatement();
            String SQLiteCreateTokensTable =
                    "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "player TEXT NOT NULL, " +
                    "item TEXT NOT NULL, " +
                    "world TEXT NOT NULL, " +
                    "locationx INTEGER NOT NULL, " +
                    "locationy INTEGER NOT NULL, " +
                    "locationz INTEGER NOT NULL, " +
                    "UNIQUE(world, locationx, locationy, locationz)" +
                    ");";
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createDatabaseFile() {
        File pluginFolder = plugin.getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
        File dataFolder = new File(pluginFolder, DATABASE_NAME + ".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: " + DATABASE_NAME + ".db");
            }
        }
    }

    private Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), DATABASE_NAME + ".db");
        try {
            if(connection != null && !connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "Missing the SQLite JBDC library.");
        }
        return null;
    }

    private void close(AutoCloseable closeable){
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Tried to close a resource that cannot be closed");
        }
    }

    public void addEntry(String player, String item, String world, int locationx, int locationy, int locationz) {
        Connection conn = getSQLConnection();
        if (conn == null) {
            plugin.getLogger().log(Level.SEVERE, "Could not get connection to database");
            return;
        }

        try (PreparedStatement statement = conn.prepareStatement("INSERT OR REPLACE INTO " + TABLE_NAME + " (player, item, world, locationx, locationy, locationz) VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, player);
            statement.setString(2, item);
            statement.setString(3, world);
            statement.setInt(4, locationx);
            statement.setInt(5, locationy);
            statement.setInt(6, locationz);
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error adding entry to database", e);
        }
    }

    public void removeEntry(String world, int locationx, int locationy, int locationz) {
        Connection conn = getSQLConnection();
        if (conn == null) {
            plugin.getLogger().log(Level.SEVERE, "Could not get connection to database");
            return;
        }

        try (PreparedStatement statement = conn.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE world = ? AND locationx = ? AND locationy = ? AND locationz = ?")) {
            statement.setString(1, world);
            statement.setInt(2, locationx);
            statement.setInt(3, locationy);
            statement.setInt(4, locationz);
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error removing entry from database", e);
        }
    }

    public ArrayList<Location> getLocations(String player, String item) {
        ArrayList<Location> locations = new ArrayList<>();
        Connection conn = getSQLConnection();
        if (conn == null) {
            plugin.getLogger().log(Level.SEVERE, "Could not get connection to database");
            return locations;
        }
        try (PreparedStatement statement = conn.prepareStatement("SELECT world, locationx, locationy, locationz FROM " + TABLE_NAME + " WHERE player = ? AND item = ?")) {
            statement.setString(1, player);
            statement.setString(2, item);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String worldName = resultSet.getString("world");
                World world = Bukkit.getWorld(worldName);
                int x = resultSet.getInt("locationx");
                int y = resultSet.getInt("locationy");
                int z = resultSet.getInt("locationz");
                locations.add(new Location(world, x, y, z));
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error getting locations from database", e);
        }
        return locations;
    }
}