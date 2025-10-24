package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataobjects.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO{
    public SQLGameDAO() throws DataAccessException{
        DatabaseManager.createDatabase();
        try(Connection conn = DatabaseManager.getConnection()) {
            String statement = """
                    CREATE TABLE IF NOT EXISTS  gameTable (
                      gameID int NOT NULL AUTO_INCREMENT,
                      whiteUsername varchar(256),
                      blackUsername varchar(256),
                      gameName varchar(256) NOT NULL,
                      game longtext NOT NULL,
                      PRIMARY KEY (gameID)
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
    public Collection<GameData> getGames() throws DataAccessException{
        try(Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameTable";
            try(PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                ResultSet rs = preparedStatement.executeQuery();
                ArrayList<GameData> games = new ArrayList<>();
                Gson serializer = new Gson();
                int gameID;
                String whiteUsername;
                String blackUsername;
                String gameName;
                ChessGame gameObj;
                while(rs.next()) {
                    gameID = rs.getInt(1);
                    whiteUsername = rs.getString(2);
                    blackUsername = rs.getString(3);
                    gameName = rs.getString(4);
                    gameObj = serializer.fromJson(rs.getString(5), ChessGame.class);
                    games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, gameObj));
                }
                return games;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameTable WHERE gameID = ?";
            try(PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                ResultSet rs = preparedStatement.executeQuery();
                rs.next();
                Gson serializer = new Gson();
                String whiteUsername = rs.getString(2);
                String blackUsername = rs.getString(3);
                String gameName = rs.getString(4);
                ChessGame gameObj = serializer.fromJson(rs.getString(5), ChessGame.class);
                return(new GameData(gameID, whiteUsername, blackUsername, gameName, gameObj));
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO gameTable (gameID, whiteUsername, blackUsername, gameName, game) values (?, ?, ?, ?, ?)";
            try(PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, game.gameID());
                preparedStatement.setString(2, game.whiteUsername());
                preparedStatement.setString(3, game.blackUsername());
                preparedStatement.setString(4, game.gameName());
                Gson serializer = new Gson();
                String gameJson = serializer.toJson(game.game());
                preparedStatement.setObject(5, gameJson);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement preparedStatement = conn.prepareStatement("TRUNCATE TABLE gameTable")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void updateGame(int gameID, GameData game) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM gameTable WHERE gameID = ?")) {
                preparedStatement.setInt(1, gameID);
                ResultSet rs = preparedStatement.executeQuery();
                if(!rs.next()) {
                    throw new DataAccessException("game doesn't exist");
                }
            }
            String statement = "UPDATE gameTable SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?, gameID = ? WHERE gameID = ?";
            try(PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, game.whiteUsername());
                preparedStatement.setString(2, game.blackUsername());
                preparedStatement.setString(3, game.gameName());
                Gson serializer = new Gson();
                String gameJson = serializer.toJson(game.game());
                preparedStatement.setString(4, gameJson);
                preparedStatement.setInt(5, game.gameID());
                preparedStatement.setInt(6, gameID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
