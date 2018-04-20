package com.mrray.datadesensitiveserver.utils;

import com.mrray.datadesensitiveserver.entity.dto.DatabaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public final class DatabaseUtils {
    private static final String DRIVER = "org.gjt.mm.mysql.Driver";
    private static Logger logger = LoggerFactory.getLogger("DatabaseUtils");

    private DatabaseUtils() {
    }

    public static Connection connect(DatabaseInfo databaseInfo) {
        Connection connection = null;
        String url = "jdbc:mysql://" + databaseInfo.getIp() + ":" + databaseInfo.getPort() + "/" + databaseInfo.getDatabaseName() + "?useServerPrepStmts=false&rewriteBatchedStatements=true&characterEncoding=utf8";
        String username = databaseInfo.getUsername();
        String password = databaseInfo.getPassword();
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(url, username, password);
            if (!connection.isClosed()) {
                logger.info("Succeeded connecting to the Database!");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private static void closePreparedStatement(PreparedStatement cmd) {
        if (cmd != null) {
            try {
                cmd.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public static List<Map<String, Object>> getColumns(Connection connection, DatabaseInfo databaseInfo) throws SQLException {
        List<Map<String, Object>> columnInfos = new ArrayList<>();
        Statement statement = null;
        ResultSet columns = null;
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            statement = connection.createStatement();
            columns = metaData.getColumns(databaseInfo.getDatabaseName(), "%", databaseInfo.getTableName(), "%");
            while (columns.next()) {
                Map<String, Object> columnInfo = new HashMap<>();
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                columnInfo.put("columnName", columnName);
                columnInfo.put("columnType", columnType);
                columnInfos.add(columnInfo);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw e;
        } finally {
            closeResultSet(columns);
            closeStatement(statement);
        }
        return columnInfos;
    }

    public static void getValues(Connection connection, List<Map<String, Object>> columnInfos, String tableName, int offset, int rows) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement cmd = null;
        for (Map<String, Object> columnInfo : columnInfos) {
            String sql = String.format("select `%s` from `%s` limit ?,?", columnInfo.get("columnName"), tableName);
            List<String> values = new ArrayList<>();
            try {
                cmd = connection.prepareStatement(sql);
                cmd.setInt(1, offset);
                cmd.setInt(2, rows);
                resultSet = cmd.executeQuery();
                while (resultSet.next()) {
                    String value = resultSet.getString(1);
                    value = value == null ? null : value.trim();
                    values.add(value);
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
                throw e;
            } finally {
                closeResultSet(resultSet);
                closePreparedStatement(cmd);
            }
            columnInfo.put("values", values);
        }
    }

    public static Long getRows(Connection connection, String tableName) {
        String sql = String.format("SELECT COUNT(*) FROM `%s`", tableName);
        Long rowCount = 0L;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                rowCount = resultSet.getLong(1);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            closeStatement(statement);
            closeResultSet(resultSet);
        }
        return rowCount;
    }

    public static boolean createTable(Connection connection, String tableName, String newTableName) {
        ResultSet resultSet = null;
        PreparedStatement cmd = null;
        Statement statement = null;
        try {
            String sql = String.format("show create table `%s`", tableName);
            cmd = connection.prepareStatement(sql);
            resultSet = cmd.executeQuery();
            while (resultSet.next()) {
                sql = resultSet.getString(2);
            }
            sql = sql.replaceFirst(tableName, newTableName);
            statement = connection.createStatement();
            boolean execute = statement.execute(sql);
            return !execute;
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(cmd);
            closeStatement(statement);
        }
        return false;
    }

    public static void insert(Connection connection, String newTableName, List<List<String>> valuesToInsert, int count) throws SQLException {
        StringBuilder stringBuilder = new StringBuilder("insert into `").append(newTableName).append("` values").append("(");
        for (int i = 0; i < count; i++) {
            if (i < count - 1) {
                stringBuilder.append("?,");
            } else {
                stringBuilder.append("?)");
            }
        }
        PreparedStatement cmd = null;
        try {
            connection.setAutoCommit(false);
            cmd = connection.prepareStatement(stringBuilder.toString());
            for (List<String> row : valuesToInsert) {
                for (int i = 0; i < row.size(); i++) {
                    cmd.setString(i + 1, row.get(i));
                }
                cmd.addBatch();
            }
            cmd.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw e;
        } finally {
            closePreparedStatement(cmd);
        }
    }

    public static void deleteTable(Connection connection, String newTableName) {
        String sql = String.format("DROP TABLE IF EXISTS `%s`", newTableName);
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            closeStatement(statement);
        }
    }
}