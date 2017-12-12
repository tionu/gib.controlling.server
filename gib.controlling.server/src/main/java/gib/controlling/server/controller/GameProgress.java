package gib.controlling.server.controller;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import gib.controlling.client.GameStateWatcher;
import gib.controlling.client.mappings.GameState;
import gib.controlling.client.mappings.GameState.State;
import gib.controlling.client.mappings.Level;
import gib.controlling.client.setup.AppProperties;
import gib.controlling.persistence.HiDrivePersistenceProvider;
import gib.controlling.persistence.PersistenceProvider;

public class GameProgress {

	private PersistenceProvider cloudPersistence;
	private Logger log;

	public GameProgress() {
		cloudPersistence = new HiDrivePersistenceProvider();
		log = Logger.getLogger(GameStateWatcher.class.getName());
	}

	public void setLevel(int level) {
		log.info("set level to: " + level);
		Level gameLevel = new Level();
		gameLevel.setLevel(level);
		String levelJson = new Gson().toJson(gameLevel);
		try {
			cloudPersistence.write(AppProperties.LEVEL_FILENAME, levelJson.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setGameState(State state) {
		log.info("set game state to: " + state);
		GameState gameState = new GameState();
		gameState.setGameState(state);
		String gameStateJson = new Gson().toJson(gameState);
		try {

			cloudPersistence.write(AppProperties.GAME_STATE_FILENAME, gameStateJson.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
