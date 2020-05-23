package rorys.library.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLManager {

    private final JavaPlugin plugin;

    private Connection connection;
    private String host, database, tableName, username, password;
    private int port;

    public SQLManager(JavaPlugin plugin, String tableName) {
        this.plugin = plugin;
        this.tableName = tableName;

        FileConfiguration config = plugin.getConfig();
        host = config.getString("mysql.host", "localhost");
        port = config.getInt("mysql.port", 3306);
        database = config.getString("mysql.database", "mcserver");
        username = config.getString("mysql.username", "root");
        password = config.getString("mysql.password", "root");

        try {
            checkConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Keep SQL Connection active
        (new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.createStatement().execute("SELECT 1");
                    }
                } catch (SQLException e) {
                    connection = createConnection();
                }
            }
        }).runTaskTimerAsynchronously(plugin, 60 * 20, 60 * 20);

    }

    public Connection createConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            Connection connection = DriverManager.getConnection(url, username, password);
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean checkConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = createConnection();
            if (connection == null || connection.isClosed()) {
                return false;
            }
        }
        return true;
    }

    public void clearTable() {
        try {
            connection.createStatement().execute("TRUNCATE TABLE " + tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean execute(String sql) throws SQLException {
        boolean success = connection.createStatement().execute(sql);
        return success;
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

}
