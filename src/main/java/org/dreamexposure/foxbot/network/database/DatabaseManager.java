package org.dreamexposure.foxbot.network.database;


import com.zaxxer.hikari.HikariDataSource;
import org.dreamexposure.foxbot.conf.Settings;
import org.dreamexposure.foxbot.objects.guild.GuildSettings;
import org.dreamexposure.foxbot.objects.role.RoleMirror;
import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.novautils.database.DatabaseSettings;
import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import discord4j.core.object.util.Snowflake;

@SuppressWarnings({"SqlResolve", "UnusedReturnValue", "SqlNoDataSourceInspection", "Duplicates"})
public class DatabaseManager {
    private static DatabaseManager instance;
    private DatabaseInfo info;

    private DatabaseManager() {
    } //Prevent initialization.

    /**
     * Gets the instance of the {@link DatabaseManager}.
     *
     * @return The instance of the {@link DatabaseManager}
     */
    public static DatabaseManager getManager() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Connects to the MySQL server specified.
     */
    public void connectToMySQL() {
        try {
            DatabaseSettings settings = new DatabaseSettings(Settings.SQL_HOST.get(), Settings.SQL_PORT.get(),
                Settings.SQL_DB.get(), Settings.SQL_USER.get(), Settings.SQL_PASS.get(), Settings.SQL_PREFIX.get());

            info = org.dreamexposure.novautils.database.DatabaseManager.connectToMySQL(settings);
            System.out.println("Connected to MySQL database!");
        } catch (Exception e) {
            System.out.println("Failed to connect to MySQL database! Is it properly configured?");
            e.printStackTrace();
        }
    }

    /**
     * Disconnects from the MySQL server if still connected.
     */
    @SuppressWarnings("unused")
    public void disconnectFromMySQL() {
        try {
            org.dreamexposure.novautils.database.DatabaseManager.disconnectFromMySQL(info);
            System.out.println("Successfully disconnected from MySQL Database!");
        } catch (Exception e) {
            System.out.println("MySQL Connection may not have closed properly! Data may be invalidated!");
        }
    }

    public void handleMigrations() {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("prefix", Settings.SQL_PREFIX.get());

        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(info.getSource())
                    .cleanDisabled(true)
                    .baselineOnMigrate(true)
                    .table(Settings.SQL_PREFIX.get() + "schema_history")
                    .placeholders(placeholders)
                    .load();
            int sm = flyway.migrate(); //Number of migrations applied.
        } catch (Exception e) {
            System.exit(2);
        }
    }

    public boolean updateSettings(GuildSettings settings) {
        try (final Connection connection = info.getSource().getConnection()) {
            String dataTableName = String.format("%sguild_settings", info.getSettings().getPrefix());

            String query = "SELECT * FROM " + dataTableName + " WHERE guild_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, settings.getGuildId().asLong());

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (!hasStuff || res.getLong("guild_id") != 0) {
                //Data not present, add to DB.
                String insertCommand = "INSERT INTO " + dataTableName +
                        "(guild_id, lang, prefix, patron_guild, dev_guild)" +
                        " VALUES (?, ?, ?, ?, ?);";
                PreparedStatement ps = connection.prepareStatement(insertCommand);
                ps.setLong(1, settings.getGuildId().asLong());
                ps.setString(2, settings.getLang());
                ps.setString(3, settings.getPrefix());
                ps.setBoolean(4, settings.isPatron());
                ps.setBoolean(5, settings.isDev());


                ps.executeUpdate();
                ps.close();
                statement.close();
            } else {
                //Data present, update.
                String update = "UPDATE " + dataTableName
                        + " SET lang = ?, prefix = ?, patron_guild = ?, dev_guild = ?, " +
                        " WHERE guild_id = ?";
                PreparedStatement ps = connection.prepareStatement(update);

                ps.setString(1, settings.getLang());
                ps.setString(2, settings.getPrefix());
                ps.setBoolean(3, settings.isPatron());
                ps.setBoolean(4, settings.isDev());
                ps.setLong(5, settings.getGuildId().asLong());

                ps.executeUpdate();

                ps.close();
                statement.close();
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to input data into database! Error Code: 00101");
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateRoleMirror(RoleMirror mirror) {
        try (final Connection connection = info.getSource().getConnection()) {
            String dataTableName = String.format("%srole_mirror", info.getSettings().getPrefix());

            String query = "SELECT * FROM " + dataTableName + " WHERE primary_guild_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, mirror.getPrimaryGuildId().asLong());

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (!hasStuff || res.getLong("primary_guild_id") != 0) {
                //Data not present, add to DB.
                String insertCommand = "INSERT INTO " + dataTableName +
                        "(primary_guild_id, primary_guild_role, secondary_guild_id, secondary_guild_role)" +
                        " VALUES (?, ?, ?, ?);";
                PreparedStatement ps = connection.prepareStatement(insertCommand);

                ps.setLong(1, mirror.getPrimaryGuildId().asLong());
                ps.setLong(2, mirror.getPrimaryGuildRole().asLong());
                ps.setLong(3, mirror.getSecondaryGuildId().asLong());
                ps.setLong(4, mirror.getSecondaryGuildRole().asLong());

                ps.executeUpdate();
                ps.close();
                statement.close();
            } else {
                //Data present, update.
                String update = "UPDATE " + dataTableName
                        + " SET primary_guild_role = ?, secondary_guild_id = ?, secondary_guild_role = ?, " +
                        " WHERE primary_guild_id ?";
                PreparedStatement ps = connection.prepareStatement(update);

                ps.setLong(1, mirror.getPrimaryGuildRole().asLong());
                ps.setLong(2, mirror.getSecondaryGuildId().asLong());
                ps.setLong(3, mirror.getSecondaryGuildRole().asLong());
                ps.setLong(4, mirror.getPrimaryGuildId().asLong());

                ps.executeUpdate();

                ps.close();
                statement.close();
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to input data into database! Error Code: 00101");
            e.printStackTrace();
        }
        return false;
    }


    public GuildSettings getSettings(Snowflake guildId) {
        GuildSettings settings = new GuildSettings(guildId);
        try (final Connection connection = info.getSource().getConnection()) {
            String dataTableName = String.format("%sguild_settings", info.getSettings().getPrefix());

            String query = "SELECT * FROM " + dataTableName + " WHERE guild_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, guildId.asLong());

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (hasStuff && res.getLong("guild_id") != 0) {
                settings.setLang(res.getString("lang"));
                settings.setPrefix(res.getString("prefix"));
                settings.setPatron(res.getBoolean("patron_guild"));
                settings.setDev(res.getBoolean("dev_guild"));

                statement.close();
            } else {
                //Data not present.
                statement.close();
                return settings;
            }
        } catch (SQLException e) {
            //TODO: Send to logger
        }
        return settings;
    }

    public List<RoleMirror> getRoleMirrorsByPrimary(Snowflake primaryGuildId) {
        List<RoleMirror> mirrors = new ArrayList<>();
        try (final Connection connection = info.getSource().getConnection()) {
            String dataTableName = String.format("%srole_mirror", info.getSettings().getPrefix());

            String query = "SELECT * FROM " + dataTableName + " WHERE primary_guild_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, primaryGuildId.asLong());

            ResultSet res = statement.executeQuery();
            while (res.next()) {
                RoleMirror mirror = new RoleMirror();
                mirror.setPrimaryGuildId(primaryGuildId);
                mirror.setPrimaryGuildRole(Snowflake.of(res.getLong("primary_guild_role")));
                mirror.setSecondaryGuildId(Snowflake.of(res.getLong("secondary_guild_id")));
                mirror.setSecondaryGuildRole(Snowflake.of(res.getLong("secondary_guild_role")));

                mirrors.add(mirror);
            }

            statement.close();
        } catch (SQLException e) {
            //TODO: Send to logger
        }

        return mirrors;
    }

    public List<RoleMirror> getRoleMirrorBySecondary(Snowflake secondaryGuildId) {
        List<RoleMirror> mirrors = new ArrayList<RoleMirror>();
        try (final Connection connection = info.getSource().getConnection()) {
            String dataTableName = String.format("%srole_mirror", info.getSettings().getPrefix());

            String query = "SELECT * FROM " + dataTableName + " WHERE secondary_guild_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, secondaryGuildId.asLong());

            ResultSet res = statement.executeQuery();

            while (res.next()) {
                RoleMirror mirror = new RoleMirror();
                mirror.setPrimaryGuildId(Snowflake.of(res.getLong("primary_guild_id")));
                mirror.setPrimaryGuildRole(Snowflake.of(res.getLong("primary_guild_role")));
                mirror.setSecondaryGuildId(secondaryGuildId);
                mirror.setSecondaryGuildRole(Snowflake.of(res.getLong("secondary_guild_role")));

                mirrors.add(mirror);
            }

            statement.close();
        } catch (SQLException e) {
            //TODO: Send to logger
        }
        return mirrors;
    }
}
