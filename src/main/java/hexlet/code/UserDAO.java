package hexlet.code;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection conn) {
        connection = conn;
    }

    public void save(User user) throws SQLException {
        if (user.getId() == null) {
            var sql = "INSERT INTO users (username, phone) VALUES (?, ?)";
            try (var preparedStatement =
                         connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getPhone());
                preparedStatement.executeUpdate();
                try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Database did not return an id");
                    }
                }
            }
        } else {
            var sql = "UPDATE users SET username = ?, phone = ? WHERE id = ?";
            try (var preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getPhone());
                preparedStatement.setLong(3, user.getId());
                if (preparedStatement.executeUpdate() != 1) {
                    throw new SQLException("User not found: " + user.getId());
                }
            }
        }
    }

    public Optional<User> find(Long id) throws SQLException {
        var sql = "SELECT username, phone FROM users WHERE id = ?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (var resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    var username = resultSet.getString("username");
                    var phone = resultSet.getString("phone");
                    var user = new User(username, phone);
                    user.setId(id);
                    return Optional.of(user);
                }
                return Optional.empty();
            }
        }
    }

    public boolean delete(Long id) throws SQLException {
        User user = find(id).orElse(null);
        if (user == null) {
            return false;
        }
        int result;
        var sql = "DELETE FROM users WHERE id = ?";
        try(var preparedStatment = connection.prepareStatement(sql)) {
            preparedStatment.setLong(1, id);
            result = preparedStatment.executeUpdate();
        }

        return result != 0;
    }

    public void select() throws SQLException {
        var sql3 = "SELECT * FROM users ORDER BY id";

        try (var statement3 = connection.createStatement()) {
            var resultSet = statement3.executeQuery(sql3);
            while (resultSet.next()) {
                System.out.println(resultSet.getString("username"));
                System.out.println(resultSet.getString("phone"));
            }
        }
    }
}
