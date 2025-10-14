package services;

import dataaccess.MemoryGameDAO;
import dataaccess.GameDAO;

public class GameServices {
    private static final GameDAO gameDataAccess = new MemoryGameDAO();

    public static void clear() {
        gameDataAccess.clear();
    }
}
