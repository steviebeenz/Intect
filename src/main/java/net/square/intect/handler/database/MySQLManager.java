package net.square.intect.handler.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLManager
{

    private final HikariPool hikariPool;

    public MySQLManager() throws HikariPool.PoolInitializationException
    {

        String address = "";
        String database = "";
        String username = "";
        String password = "";
        int port = 3306;

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

        this.hikariPool = new HikariPool(config);

        // this.createTables();
    }

    public void createTables()
    {
        try (Connection connection = getConnection())
        {

            /*
            Soon table structure for the best experience
             */

            connection.createStatement().execute("");

        } catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }
    }

    public Connection getConnection()
    {
        try
        {
            return this.hikariPool.getConnection();
        } catch (SQLException throwable)
        {
            throwable.printStackTrace();
            return null;
        }
    }

    public void close()
    {
        try
        {
            this.hikariPool.shutdown();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
