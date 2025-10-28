package dataaccess;

import dataobjects.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{
    public SQLUserDAO() throws DataAccessException{
        DatabaseManager.createDatabase();
        try(Connection conn = DatabaseManager.getConnection()) {
            String statement = """
                    CREATE TABLE IF NOT EXISTS  userTable (
                      username varchar(256) NOT NULL,
                      email varchar(256) NOT NULL,
                      password varchar(256) NOT NULL,
                      PRIMARY KEY (username)
                    )
            """;
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, email, password FROM userTable WHERE username = ?";
            try(PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    String email = rs.getString(2);
                    String password = rs.getString(3);
                    return (new UserData(username, password, email));
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO userTable (username, email, password) values (?, ?, ?)";
            try(PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, userData.email());
                preparedStatement.setString(3, userData.password());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException{
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement preparedStatement = conn.prepareStatement("TRUNCATE TABLE userTable")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
