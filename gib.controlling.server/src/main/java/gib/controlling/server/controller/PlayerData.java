package gib.controlling.server.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import com.google.gson.Gson;

import gib.controlling.client.mappings.TimeStamp;
import gib.controlling.client.mappings.TimeStampLog;
import gib.controlling.client.mappings.UserSettings;
import gib.controlling.client.setup.AppProperties;
import gib.controlling.persistence.HiDrivePersistenceProvider;
import gib.controlling.persistence.PersistenceProvider;

public class PlayerData {

	public enum OnlineState {
		LOGIN, LOGOUT;
	}

	private PersistenceProvider cloudPersistence;
	private String playerGroup;
	private UserSettings cloudSettings;
	private long lastUpload;
	private List<Long> logins;
	private List<Long> logouts;
	private long keepAlive;

	public PlayerData(int playerId) {
		if (playerId < 1 || playerId > 10) {
			throw new IllegalArgumentException();
		}
		this.playerGroup = String.format("%02d", playerId);
		lastUpload = 0;
		keepAlive = 0;
		cloudPersistence = new HiDrivePersistenceProvider();
	}

	public UserSettings getUserSettings() throws IOException {
		if (cloudSettings == null) {
			loadUserSettings();
		}
		return cloudSettings;
	}

	public void loadUserSettings() throws IOException {
		byte[] settingsByteArray = new byte[0];
		settingsByteArray = cloudPersistence.read(Paths.get(playerGroup + "_" + AppProperties.USER_SETTINGS_FILENAME));
		cloudSettings = new Gson().fromJson(new String(settingsByteArray), UserSettings.class);
	}

	public long getLastUpload() {
		if (lastUpload == 0) {
			loadLastUpload();
		}
		return lastUpload;
	}

	public void loadLastUpload() {
		byte[] lastUploadByteArray = new byte[0];
		try {
			lastUploadByteArray = cloudPersistence
					.read(Paths.get(playerGroup + "_" + AppProperties.UPLOAD_LOG_FILENAME));
			TimeStamp lastUploadTimeStamp = new Gson().fromJson(new String(lastUploadByteArray), TimeStamp.class);
			lastUpload = lastUploadTimeStamp.getTimeStamp();
		} catch (IOException e) {
		}
	}

	public List<Long> getLogins() {
		if (logins == null) {
			loadLogins();
		}
		return logins;
	}

	public void loadLogins() {
		byte[] loginsByteArray = new byte[0];
		try {
			loginsByteArray = cloudPersistence.read(Paths.get(playerGroup + "_" + AppProperties.LOGIN_LOG_FILENAME));
			TimeStampLog loginTimeStamps = new Gson().fromJson(new String(loginsByteArray), TimeStampLog.class);
			logins = loginTimeStamps.getTimeStamps();
		} catch (IOException e) {
		}
	}

	public List<Long> getLogouts() {
		if (logouts == null) {
			loadLogouts();
		}
		return logouts;
	}

	public void loadLogouts() {
		byte[] logoutByteArray = new byte[0];
		try {
			logoutByteArray = cloudPersistence.read(Paths.get(playerGroup + "_" + AppProperties.LOGOUT_LOG_FILENAME));
			TimeStampLog logoutTimeStamps = new Gson().fromJson(new String(logoutByteArray), TimeStampLog.class);
			logouts = logoutTimeStamps.getTimeStamps();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public long getKeepAlive() {
		if (keepAlive == 0) {
			loadKeepAlive();
		}
		return keepAlive;
	}

	public void loadKeepAlive() {
		byte[] keepAliveByteArray = new byte[0];
		try {
			keepAliveByteArray = cloudPersistence
					.read(Paths.get(playerGroup + "_" + AppProperties.KEEP_ALIVE_LOG_FILENAME));
			TimeStamp keepAliveTimeStamp = new Gson().fromJson(new String(keepAliveByteArray), TimeStamp.class);
			keepAlive = keepAliveTimeStamp.getTimeStamp();
		} catch (IOException e) {
		}
	}

	public TreeMap<Long, OnlineState> getOnlineLog() {
		TreeMap<Long, OnlineState> onlineLog = new TreeMap<Long, OnlineState>(Collections.reverseOrder());
		for (long timeStamp : getLogins()) {
			onlineLog.put(timeStamp, OnlineState.LOGIN);
		}
		for (long timeStamp : getLogouts()) {
			onlineLog.put(timeStamp, OnlineState.LOGOUT);
		}
		return onlineLog;
	}

}
