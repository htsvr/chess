package dataaccess;

import dataobjects.GameData;

public interface GameDAO {
    void getGames();
    GameData getGame(int gameID);
    void createGame(GameData game);
    void clear();
}
