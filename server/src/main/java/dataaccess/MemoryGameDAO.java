package dataaccess;

import dataobjects.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO{
    public final ArrayList<GameData> gameList;

    public MemoryGameDAO () {
        gameList = new ArrayList<>();
    }

    @Override
    public void getGames() {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void createGame(GameData game) {

    }

    @Override
    public void clear() {
        gameList.clear();
    }
}
