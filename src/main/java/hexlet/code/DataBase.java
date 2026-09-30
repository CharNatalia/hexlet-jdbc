package hexlet.code;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {

    public static void insertPrepared(Connection conn, String name, String phone) throws SQLException {
        var sql = "INSERT INTO users (username, phone) VALUES (?, ?)";
        try (var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phone);
            preparedStatement.executeUpdate();

            try (var generetedKey = preparedStatement.getGeneratedKeys()) {
                if (generetedKey.next()) {
                    System.out.println(generetedKey.getLong(1));
                } else {
                    throw new SQLException("Database did not return an id after inserting the user");
                }
            }
        }
    }

    public static void deleteByName(Connection conn, String name) throws SQLException {
        var sql = "DELETE FROM users WHERE username = ?";
        try (var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            System.out.println(preparedStatement.executeUpdate());
        }
    }

    public static void select(Connection conn) throws SQLException {
        var sql3 = "SELECT * FROM users ORDER BY id";

        try (var statement3 = conn.createStatement()) {
            var resultSet = statement3.executeQuery(sql3);
            while (resultSet.next()) {
                System.out.println(resultSet.getString("username"));
                System.out.println(resultSet.getString("phone"));
            }
        }
    }
}
