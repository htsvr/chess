package dataaccess;

import dataobjects.GameData;
import java.util.Collection;

public interface GameDAO {
    Collection<GameData> getGames();
    GameData getGame(int gameID);
    void createGame(GameData game);
    void clear();
}
