package gib.controlling.server.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import gib.controlling.client.setup.AppProperties;
import gib.controlling.persistence.FileTransfer;

public class GameFilesExchange {

	public GameFilesExchange() {
		AppProperties.APP_PATH = AppProperties.APP_PATH_STRATEG;
		Utils.createWorkingDirectory();
	}

	public void downloadPlayerFiles(int groupNumber) {
		Path filePath = Paths.get("KL_STA" + String.format("%02d", groupNumber) + ".DAT");
		try {
			Files.deleteIfExists(AppProperties.getWorkingDirectory().resolve(filePath));
			FileTransfer.downloadFile(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void downloadAllPlayerFiles() {
		for (int i = 1; i <= 10; i++) {
			downloadPlayerFiles(i);
		}
	}

	public void uploadPlayerFiles(int groupNumber) {
		Path filePath = Paths.get("KL_STA" + String.format("%02d", groupNumber) + ".DAT");
		if (AppProperties.getWorkingDirectory().resolve(filePath).toFile().exists()) {
			FileTransfer.uploadFile(filePath);
		}
	}

	public void uploadAllPlayerFiles() {
		for (int i = 1; i <= 10; i++) {
			uploadPlayerFiles(i);
		}
	}

	public void downloadGameMasterFile() {
		Path filePath = Paths.get("SL.DAT");
		try {
			Files.deleteIfExists(AppProperties.getWorkingDirectory().resolve(filePath));
			FileTransfer.downloadFile(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void uploadGameMasterFile() {
		Path filePath = Paths.get("SL.DAT");
		if (AppProperties.getWorkingDirectory().resolve(filePath).toFile().exists()) {
			FileTransfer.uploadFile(filePath);
		}
	}

}
