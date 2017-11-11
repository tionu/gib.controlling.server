package gib.controlling.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import gib.controlling.client.GameSetup;
import gib.controlling.client.setup.AppProperties;
import gib.controlling.persistence.FileTransfer;

public class GameFilesExchange {

	private Logger log;

	public GameFilesExchange() {
		AppProperties.APP_PATH = AppProperties.APP_PATH_STRATEG;
		log = Logger.getLogger(GameSetup.class.getName());
		createWorkingDirectory();
	}

	public void loadAllPlayerFiles() {
		for (int i = 1; i <= 10; i++) {
			Path filePath = Paths.get("KL_STA" + String.format("%02d", i) + ".DAT");
			try {
				Files.deleteIfExists(AppProperties.getWorkingDirectory().resolve(filePath));
				FileTransfer.downloadFile(filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void pushAllPlayerFiles() {
		for (int i = 1; i <= 10; i++) {
			Path filePath = Paths.get("KL_STA" + String.format("%02d", i) + ".DAT");
			if (AppProperties.getWorkingDirectory().resolve(filePath).toFile().exists()) {
				FileTransfer.uploadFile(filePath);
			}
		}
	}

	public void loadGameMasterFile() {
		Path filePath = Paths.get("SL.DAT");
		try {
			Files.deleteIfExists(AppProperties.getWorkingDirectory().resolve(filePath));
			FileTransfer.downloadFile(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void pushGameMasterFile() {
		Path filePath = Paths.get("SL.DAT");
		if (AppProperties.getWorkingDirectory().resolve(filePath).toFile().exists()) {
			FileTransfer.uploadFile(filePath);
		}
	}

	public void createWorkingDirectory() {
		Path workingDirectory = AppProperties.getWorkingDirectory();
		if (!Files.exists(workingDirectory)) {
			log.debug("create working directory: " + workingDirectory.toString());
			new File(workingDirectory.toUri()).mkdir();
		}
	}

}
