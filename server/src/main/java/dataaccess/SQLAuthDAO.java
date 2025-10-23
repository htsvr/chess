package dataaccess;

import dataobjects.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
        try(Connection conn = DatabaseManager.getConnection()) {
            String statement = """
            CREATE TABLE IF NOT EXISTS  authTable (
              authToken varchar(256) NOT NULL,
              username varchar(256) NOT NULL,
              PRIMARY KEY (authToken)
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
    public void createAuth(AuthData authData) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO authTable values(?, ?)")) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.setString(2, authData.username());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement preparedStatement = conn.prepareStatement("SELECT authToken, username from authTable where authToken = ?")) {
                preparedStatement.setString(1, authToken);
                ResultSet rs = preparedStatement.executeQuery();
                rs.next();
                return new AuthData(rs.getString(2), authToken);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement("DELETE from authTable where authToken = ? AND username = ?")) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.setString(2, authData.username());
                if(preparedStatement.executeUpdate() == 0) {
                    throw new DataAccessException("auth data doesn't exist");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement preparedStatement = conn.prepareStatement("TRUNCATE TABLE authTable")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
