package dataaccess;

import dataobjects.GameData;

import javax.xml.crypto.Data;
import java.util.Collection;

public interface GameDAO {
    Collection<GameData> getGames();
    GameData getGame(int gameID) throws DataAccessException;
    void createGame(GameData game) throws DataAccessException;
    void clear();
    void updateGame(int gameID, GameData game) throws DataAccessException;
}
