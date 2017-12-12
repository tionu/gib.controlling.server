package gib.controlling.server;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import gib.controlling.client.mappings.Level;
import gib.controlling.client.setup.AppProperties;
import gib.controlling.persistence.HiDrivePersistenceProvider;
import gib.controlling.persistence.PersistenceProvider;
import gib.controlling.server.controller.GameFilesExchange;
import gib.controlling.server.controller.GameProgress;

public class GameFilesExchangeTest {

	@Before
	public void setUp() throws Exception {
		Properties props = new Properties();
		try {
			props.load(GameFilesExchange.class.getResourceAsStream("setup/log4j.properties"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PropertyConfigurator.configure(props);
	}

	@Test
	public void testLoadAllPlayerFiles() {
		GameFilesExchange gameFilesExchange = new GameFilesExchange();
		gameFilesExchange.downloadAllPlayerFiles();
		for (int i = 1; i <= 10; i++) {
			File file = AppProperties.getWorkingDirectory()
					.resolve(Paths.get("KL_STA" + String.format("%02d", i) + ".DAT")).toFile();
			assertTrue(file.exists());
		}
	}

	@Test
	public void testLoadGameMasterFile() {
		GameFilesExchange gameFilesExchange = new GameFilesExchange();
		gameFilesExchange.downloadGameMasterFile();
		File file = AppProperties.getWorkingDirectory().resolve(Paths.get("SL.DAT")).toFile();
		assertTrue(file.exists());
	}

	@Test
	public void testPushNextLevel() {
		int currentLevel = getCurrentLevel();

		GameFilesExchange gameFilesExchange = new GameFilesExchange();
		gameFilesExchange.uploadGameMasterFile();
		gameFilesExchange.uploadAllPlayerFiles();

		GameProgress gameProgress = new GameProgress();
		gameProgress.setLevel(currentLevel + 1);

	}

	@Test
	public void testPushGameMasterFile() {
		GameFilesExchange gameFilesExchange = new GameFilesExchange();
		gameFilesExchange.uploadGameMasterFile();
	}

	@Test
	public void testPushAllPlayerFiles() {
		GameFilesExchange gameFilesExchange = new GameFilesExchange();
		gameFilesExchange.uploadAllPlayerFiles();
	}

	@Test
	public void testGetCurrentLevel() {
		int currentLevel = getCurrentLevel();
		System.out.print("Level: ");
		System.out.println(currentLevel);
	}

	public int getCurrentLevel() {
		PersistenceProvider cloudPersistence = new HiDrivePersistenceProvider();
		byte[] levelByteArray = new byte[0];
		Level cloudLevel = null;
		try {
			levelByteArray = cloudPersistence.read(AppProperties.LEVEL_FILENAME);
			cloudLevel = new Gson().fromJson(new String(levelByteArray), Level.class);
		} catch (IOException e) {
		}

		return cloudLevel.getLevel();
	}

}
