package gib.controlling.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import gib.controlling.client.mappings.GameState;
import gib.controlling.client.mappings.Level;
import gib.controlling.client.setup.AppProperties;
import gib.controlling.persistence.PersistenceProvider;
import gib.controlling.zohoAPI.ZohoPersistenceProvider;

public class GameProgressTest {

	@Before
	public void setUp() throws Exception {
		Properties props = new Properties();
		try {
			props.load(GameProgress.class.getResourceAsStream("setup/log4j.properties"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PropertyConfigurator.configure(props);
	}

	@Test
	public void testSetGameState() {

		GameState.State newGameState = GameState.State.OPEN_FOR_NEW_PLAYERS;

		GameProgress gameProgress = new GameProgress();
		gameProgress.setGameState(newGameState);

		PersistenceProvider cloudPersistence = new ZohoPersistenceProvider(AppProperties.ZOHO_AUTH_TOKEN);
		byte[] gameStateByteArray = new byte[0];
		GameState cloudGameState = null;
		try {
			gameStateByteArray = cloudPersistence.read(AppProperties.GAME_STATE_FILENAME);
			cloudGameState = new Gson().fromJson(new String(gameStateByteArray), GameState.class);
		} catch (IOException e) {
		}

		assertEquals(newGameState, cloudGameState.getGameState());

	}

	@Test
	public void testSetLevel() {

		int newLevel = 0;

		GameProgress gameProgress = new GameProgress();
		gameProgress.setLevel(newLevel);

		PersistenceProvider cloudPersistence = new ZohoPersistenceProvider(AppProperties.ZOHO_AUTH_TOKEN);
		byte[] levelByteArray = new byte[0];
		Level cloudLevel = null;
		try {
			levelByteArray = cloudPersistence.read(AppProperties.LEVEL_FILENAME);
			cloudLevel = new Gson().fromJson(new String(levelByteArray), Level.class);
		} catch (IOException e) {
		}

		assertEquals(newLevel, cloudLevel.getLevel());

	}

}
