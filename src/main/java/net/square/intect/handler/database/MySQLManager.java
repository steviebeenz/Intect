package net.square.intect.handler.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQLManager
{
    private final HikariDataSource hikariPool;

    public int logs;

    private final String prefix;

    public MySQLManager(String address, String database, String username, String password, int port, String prefix)
    {

        this.prefix = prefix;

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + address + ":" + port + "/" + database);

        config.setUsername(username);
        config.setPassword(password);

        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("useLocalSessionState", true);
        config.addDataSourceProperty("rewriteBatchedStatements", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("maintainTimeStats", false);
        config.addDataSourceProperty("cacheCallableStmts", true);
        config.addDataSourceProperty("alwaysSendSetIsolation", true);

        this.hikariPool = new HikariDataSource(config);

        this.createTables();

        logs = getLogsCount();
    }

    public void createTables()
    {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement())
        {
            statement.execute(
                String.format(
                    "CREATE TABLE IF NOT EXISTS %slogs(uuid VARCHAR(64), name VARCHAR(32), timestamp BIGINT, "
                        + "checkType VARCHAR(64), violations BIGINT, ping INT);",
                    prefix));

        } catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }
    }

    public void createLog(UUID uuid, String username, String check, int vio, int ping)
    {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement())
        {
            statement.execute(
                "INSERT INTO " + prefix + "logs(uuid, name, timestamp , checkType, violations, ping) VALUES "
                    + "('" + uuid + "', '" + username + "', '" + System.currentTimeMillis() + "', '" + check + "', '"
                    + vio + "', '" + ping + "')");
        } catch (SQLException exception)
        {
            exception.printStackTrace();
        }
    }

    public int getLogsCount()
    {
        try (ResultSet rs = getResult("SELECT COUNT(*) FROM " + prefix + "logs;"))
        {
            if (rs.next())
            {
                return rs.getInt(1);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    public ResultSet getResult(final String query)
    {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query))
        {
            return ps.executeQuery();
        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getInformationOfPlayer(String playerName, int length)
    {
        List<String> informationList = new ArrayList<>();

        try (ResultSet rs = getResult(String.format(
            "SELECT * FROM " + prefix + "logs WHERE name = '%s' ORDER BY timestamp DESC LIMIT %d;", playerName, length)
        ))
        {
            while (rs.next())
            {
                String result = String.format(
                    "%s %s failed %s | VL: +%d (Ping: %d)",
                    time(System.currentTimeMillis() - rs.getLong("timestamp")),
                    rs.getString("name"),
                    rs.getString("checkType"), rs.getInt("violations"), rs.getInt("ping")
                );

                informationList.add(result);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
        return informationList;
    }

    public String time(long milliSecs)
    {
        long secs = milliSecs / 1000L;
        long min = secs / 60L;
        long hours = min / 60L;
        min %= 60L;
        long days = hours / 24L;
        hours %= 24L;
        return String.format(
            "[%s %s %s ago]",
            pluralFormat("d", days),
            pluralFormat("h", hours),
            pluralFormat("m", min)
        );
    }

    private String pluralFormat(final String word, final long value)
    {
        return value + word;
    }

    private Connection getConnection() throws SQLException
    {
        return this.hikariPool.getConnection();
    }

    public void close()
    {
        this.hikariPool.close();
    }
}
