package com.axmor;

import com.axmor.models.ISettings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    DataSource(ISettings settings) {
        config.setJdbcUrl(settings.getDbHost());
        config.setUsername(settings.getDbLogin());
        config.setPassword(settings.getDbPassword());
        ds = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
