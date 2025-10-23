package dataaccess;

import dataobjects.GameData;

import javax.xml.crypto.Data;
import java.util.Collection;

public interface GameDAO {
    Collection<GameData> getGames() throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void createGame(GameData game) throws DataAccessException;
    void clear() throws DataAccessException;
    void updateGame(int gameID, GameData game) throws DataAccessException;
}
