package com.index;

import org.mariadb.jdbc.MariaDbPoolDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * AUTHOR
 * MOBIUS
 */
public class DataBaseConnection {

    static String url = "jdbc:mariadb://localhost:3306/" + Config.DB_NAME + "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    static String user = Config.DB_USER;
    static long pass = Config.DB_PASS;
    static int max_con = Config.DB_MAX_CON;

    private static MariaDbPoolDataSource DATABASE_POOL;

    static {
        try {
            DATABASE_POOL = new MariaDbPoolDataSource(url + "&user=" + user  + "&password=" + pass + "&maxPoolSize=" + max_con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void init()
    {
        // Test if connection is valid.
        try
        {
            DATABASE_POOL.getConnection().close();
            System.out.println("Database: Initialized.");
        }
        catch (Exception e)
        {
            System.out.println("Database: Problem on initialize. " + e);
        }
    }

    public static Connection getConnection()
    {
        Connection con = null;
        while (con == null)
        {
            try
            {
                con = DATABASE_POOL.getConnection();
            }
            catch (Exception e)
            {
                System.out.println("DatabaseFactory: Cound not get a connection. " + e);
            }
        }
        return con;
    }

    public static void close()
    {
        try
        {
            DATABASE_POOL.close();
        }
        catch (Exception e)
        {
            System.out.println("DatabaseFactory: There was a problem closing the data source. " + e);
        }
    }
}
