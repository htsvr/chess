package dataaccess;

import dataobjects.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    public final HashMap<Integer, GameData> gameList;

    public MemoryGameDAO () {
        gameList = new HashMap<>();
    }

    @Override
    public Collection<GameData> getGames() {
        return gameList.values();
    }

    @Override
    public GameData getGame(int gameID) {
        return gameList.get(gameID);
    }

    @Override
    public void createGame(GameData game) {
        gameList.put(game.gameID(), game);
    }

    @Override
    public void clear() {
        gameList.clear();
    }
}
