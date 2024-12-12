package org.practice.seeyaa.util.fileProperties.networkConnection;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseConnectionChecker {
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public DatabaseConnectionChecker(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    public boolean checkConnection() {
        try {
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isClosed()) {
                    return false;
                }
            }
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (SQLException | DataAccessException e) {
            return false;
        }
    }
}
