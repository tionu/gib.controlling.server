package gib.controlling.server.view;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import gib.controlling.client.mappings.GameState;
import gib.controlling.client.mappings.GameState.State;
import gib.controlling.client.mappings.Level;
import gib.controlling.client.mappings.PlayerRequest;
import gib.controlling.client.setup.AppProperties;
import gib.controlling.persistence.FileTransfer;
import gib.controlling.persistence.HiDrivePersistenceProvider;
import gib.controlling.persistence.PersistenceProvider;
import gib.controlling.server.GameFilesExchange;
import gib.controlling.server.GameProgress;
import gib.controlling.server.controller.Utils;

public class GameMaster extends JPanel {

	private JButton btnReload;
	private JButton btnSave;
	private JComboBox<GameState.State> cmbGameState;
	private JSpinner spnLevel;
	private JLabel lblLastPlayerRequest;
	private JLabel lblGameMasterFile;
	private JButton btnDownload;
	private JButton btnUpload;
	private JButton btnBackup;
	private JButton btnPushNextLevel;
	private JButton btnUploadDefaultGame;
	private JButton btnReset;

	/**
	 * Create the panel.
	 */
	public GameMaster() {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagLayout gbl_groupPanel = new GridBagLayout();
		gbl_groupPanel.columnWidths = new int[] { 0, 0, 30, 30, 0 };
		gbl_groupPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_groupPanel.columnWeights = new double[] { 1.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_groupPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gbl_groupPanel);

		JPanel header = new JPanel();
		GridBagConstraints gbc_header = new GridBagConstraints();
		gbc_header.gridwidth = 4;
		gbc_header.insets = new Insets(0, 1, 5, 0);
		gbc_header.fill = GridBagConstraints.HORIZONTAL;
		gbc_header.gridx = 0;
		gbc_header.gridy = 0;
		add(header, gbc_header);
		header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));

		btnReload = new JButton("\u21BB");
		btnReload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reload();
			}
		});

		btnReload.setHorizontalAlignment(SwingConstants.LEADING);
		header.add(btnReload);
		btnReload.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 11));
		btnReload.setToolTipText("reload");

		Component horizontalGlue = Box.createHorizontalGlue();
		header.add(horizontalGlue);

		JLabel lblTitle = new JLabel("Spielleiter");
		lblTitle.setFont(new Font("Dialog", Font.BOLD, 13));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		header.add(lblTitle);

		Component horizontalGlue_1 = Box.createHorizontalGlue();
		header.add(horizontalGlue_1);

		btnSave = new JButton("\u2713");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Save().execute();
			}
		});
		btnSave.setHorizontalAlignment(SwingConstants.TRAILING);
		header.add(btnSave);
		btnSave.setToolTipText("save");

		JPanel controls = new JPanel();
		GridBagConstraints gbc_controls = new GridBagConstraints();
		gbc_controls.fill = GridBagConstraints.HORIZONTAL;
		gbc_controls.gridwidth = 4;
		gbc_controls.insets = new Insets(0, 0, 5, 0);
		gbc_controls.gridx = 0;
		gbc_controls.gridy = 1;
		add(controls, gbc_controls);
		controls.setLayout(new GridLayout(0, 4, 0, 0));

		btnDownload = new JButton("\u2B63");
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new DownloadGameMasterFile().execute();
			}
		});
		btnDownload.setToolTipText("load game master file");
		btnDownload.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
		controls.add(btnDownload);

		btnUpload = new JButton("\u2B61");
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new UploadGameMasterFile().execute();
			}
		});
		btnUpload.setToolTipText("push game master file");
		btnUpload.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
		controls.add(btnUpload);

		btnReset = new JButton("\u21E4");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Reset().execute();
			}
		});
		btnReset.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
		btnReset.setToolTipText("reset game master to level 0");
		controls.add(btnReset);

		btnUploadDefaultGame = new JButton("\u2B61 default game");
		btnUploadDefaultGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new UploadDefaultGame().execute();
			}
		});
		btnUploadDefaultGame.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 11));
		btnUploadDefaultGame.setToolTipText("upload default game files");
		controls.add(btnUploadDefaultGame);

		JLabel lblGameState = new JLabel("Game State:");
		GridBagConstraints gbc_lblGameState = new GridBagConstraints();
		gbc_lblGameState.anchor = GridBagConstraints.WEST;
		gbc_lblGameState.insets = new Insets(0, 5, 5, 5);
		gbc_lblGameState.gridx = 0;
		gbc_lblGameState.gridy = 3;
		add(lblGameState, gbc_lblGameState);

		cmbGameState = new JComboBox<State>();
		cmbGameState.setModel(new DefaultComboBoxModel<State>(State.values()));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.anchor = GridBagConstraints.WEST;
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 3;
		add(cmbGameState, gbc_comboBox);

		JLabel lblLevel = new JLabel("Level:");
		GridBagConstraints gbc_lblLevel = new GridBagConstraints();
		gbc_lblLevel.anchor = GridBagConstraints.WEST;
		gbc_lblLevel.insets = new Insets(0, 5, 5, 5);
		gbc_lblLevel.gridx = 0;
		gbc_lblLevel.gridy = 4;
		add(lblLevel, gbc_lblLevel);

		spnLevel = new JSpinner();
		spnLevel.setModel(new SpinnerNumberModel(0, 0, 10, 1));
		GridBagConstraints gbc_spnLevel = new GridBagConstraints();
		gbc_spnLevel.anchor = GridBagConstraints.WEST;
		gbc_spnLevel.insets = new Insets(0, 0, 5, 5);
		gbc_spnLevel.gridx = 1;
		gbc_spnLevel.gridy = 4;
		add(spnLevel, gbc_spnLevel);

		JLabel lblLastPlayerRequestTitle = new JLabel("Last Player Request:");
		lblLastPlayerRequestTitle.setToolTipText("last request to join the game of a player group");
		GridBagConstraints gbc_lblLastPlayerRequestTitle = new GridBagConstraints();
		gbc_lblLastPlayerRequestTitle.anchor = GridBagConstraints.WEST;
		gbc_lblLastPlayerRequestTitle.insets = new Insets(0, 5, 5, 5);
		gbc_lblLastPlayerRequestTitle.gridx = 0;
		gbc_lblLastPlayerRequestTitle.gridy = 5;
		add(lblLastPlayerRequestTitle, gbc_lblLastPlayerRequestTitle);

		lblLastPlayerRequest = new JLabel("");
		GridBagConstraints gbc_lblLastPlayerRequest = new GridBagConstraints();
		gbc_lblLastPlayerRequest.anchor = GridBagConstraints.WEST;
		gbc_lblLastPlayerRequest.insets = new Insets(0, 0, 5, 5);
		gbc_lblLastPlayerRequest.gridx = 1;
		gbc_lblLastPlayerRequest.gridy = 5;
		add(lblLastPlayerRequest, gbc_lblLastPlayerRequest);

		JLabel lblGameMasterFileTitle = new JLabel("Game Master File:");
		lblGameMasterFileTitle.setToolTipText("Last modified date/time of local game master file");
		GridBagConstraints gbc_lblGameMasterFileTitle = new GridBagConstraints();
		gbc_lblGameMasterFileTitle.anchor = GridBagConstraints.WEST;
		gbc_lblGameMasterFileTitle.insets = new Insets(0, 5, 5, 5);
		gbc_lblGameMasterFileTitle.gridx = 0;
		gbc_lblGameMasterFileTitle.gridy = 6;
		add(lblGameMasterFileTitle, gbc_lblGameMasterFileTitle);

		lblGameMasterFile = new JLabel("");
		GridBagConstraints gbc_lblGameMasterFile = new GridBagConstraints();
		gbc_lblGameMasterFile.anchor = GridBagConstraints.WEST;
		gbc_lblGameMasterFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblGameMasterFile.gridx = 1;
		gbc_lblGameMasterFile.gridy = 6;
		add(lblGameMasterFile, gbc_lblGameMasterFile);

		JPanel strategControl = new JPanel();
		GridBagConstraints gbc_strategControl = new GridBagConstraints();
		gbc_strategControl.gridwidth = 4;
		gbc_strategControl.insets = new Insets(0, 0, 5, 0);
		gbc_strategControl.fill = GridBagConstraints.BOTH;
		gbc_strategControl.gridx = 0;
		gbc_strategControl.gridy = 2;
		add(strategControl, gbc_strategControl);
		strategControl.setLayout(new GridLayout(0, 4, 0, 0));

		JButton btnStrateg = new JButton("strateg");
		btnStrateg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProcessBuilder processBuilder = new ProcessBuilder(AppProperties.APP_PATH_STRATEG.toString());
				processBuilder.directory(AppProperties.APP_PATH_STRATEG.getParent().toFile());
				try {
					processBuilder.start();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnStrateg.setToolTipText("launch strateg app");
		btnStrateg.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
		strategControl.add(btnStrateg);

		btnBackup = new JButton("backup");
		btnBackup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Backup().execute();
			}
		});
		btnBackup.setToolTipText("backup all game data (local and cloud)");
		btnBackup.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
		strategControl.add(btnBackup);

		JButton btnFolder = new JButton("folder");
		btnFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					File workingFolder = AppProperties.APP_PATH_STRATEG.getParent().toFile();
					if (!workingFolder.exists()) {
						workingFolder.mkdirs();
					}
					Desktop.getDesktop().open(workingFolder);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnFolder.setToolTipText("open local working folder");
		btnFolder.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
		strategControl.add(btnFolder);

		btnPushNextLevel = new JButton("next level");
		btnPushNextLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new PushNextLevel().execute();
			}
		});
		strategControl.add(btnPushNextLevel);
		btnPushNextLevel.setToolTipText("upload all game files and increments to the next level");
		btnPushNextLevel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));

		reload();

	}

	public void reload() {
		new ReloadView().execute();
	}

	private class ReloadView extends SwingWorker<Object, Object> {

		private GameState cloudGameState;
		private PersistenceProvider cloudPersistence;
		private Level cloudLevel;
		private long lastPlayerRequest;
		private long gameMasterFileLastModified;

		private ReloadView() {
			btnReload.setEnabled(false);
			cloudPersistence = new HiDrivePersistenceProvider();
			cloudGameState = null;
			cloudLevel = null;
			lastPlayerRequest = 0;
			gameMasterFileLastModified = 0;
		}

		@Override
		protected Object doInBackground() throws Exception {

			byte[] gameStateByteArray = new byte[0];
			byte[] levelByteArray = new byte[0];
			try {
				gameStateByteArray = cloudPersistence.read(AppProperties.GAME_STATE_FILENAME);
				cloudGameState = new Gson().fromJson(new String(gameStateByteArray), GameState.class);
				levelByteArray = cloudPersistence.read(AppProperties.LEVEL_FILENAME);
				cloudLevel = new Gson().fromJson(new String(levelByteArray), Level.class);
			} catch (IOException e) {
				btnReload.setEnabled(true);
			}

			lastPlayerRequest = loadPlayerRequest().getTimestamp();

			FileTime fileTime = Files
					.getLastModifiedTime(AppProperties.getWorkingDirectory().resolve(Paths.get("SL.DAT")));
			gameMasterFileLastModified = fileTime.toMillis();

			return null;
		}

		@Override
		protected void done() {
			cmbGameState.setSelectedItem(cloudGameState.getGameState());
			spnLevel.setValue(cloudLevel.getLevel());
			btnReload.setEnabled(true);
			lblLastPlayerRequest.setText(Utils.convertTimeStamp(lastPlayerRequest));
			lblGameMasterFile.setText(Utils.convertTimeStamp(gameMasterFileLastModified));
		}

		private PlayerRequest loadPlayerRequest() {
			byte[] lastPlayerByteArray = new byte[0];
			try {
				lastPlayerByteArray = cloudPersistence.read(AppProperties.LAST_PLAYER_REQUEST_FILENAME);
			} catch (IOException e) {
				e.printStackTrace();
			}
			PlayerRequest lastPlayerRequest = new Gson().fromJson(new String(lastPlayerByteArray), PlayerRequest.class);
			if (lastPlayerRequest != null) {
				return lastPlayerRequest;
			} else {
				PlayerRequest emptyRequest = new PlayerRequest();
				emptyRequest.setTimestamp(0);
				return emptyRequest;
			}

		}
	}

	private class Save extends SwingWorker<Object, Object> {

		private Save() {
			btnSave.setEnabled(false);
		}

		@Override
		protected Object doInBackground() throws Exception {

			GameState.State newGameState = (GameState.State) cmbGameState.getSelectedItem();

			GameProgress gameProgress = new GameProgress();
			gameProgress.setGameState(newGameState);
			gameProgress.setLevel((Integer) spnLevel.getValue());

			return null;
		}

		@Override
		protected void done() {
			btnSave.setEnabled(true);

		}
	}

	private class DownloadGameMasterFile extends SwingWorker<Object, Object> {

		private long gameMasterFileLastModified;

		private DownloadGameMasterFile() {
			btnDownload.setEnabled(false);
		}

		@Override
		protected Object doInBackground() throws Exception {
			GameFilesExchange gameFilesExchange = new GameFilesExchange();
			gameFilesExchange.downloadGameMasterFile();
			FileTime fileTime = Files
					.getLastModifiedTime(AppProperties.getWorkingDirectory().resolve(Paths.get("SL.DAT")));
			gameMasterFileLastModified = fileTime.toMillis();

			return null;
		}

		@Override
		protected void done() {
			lblGameMasterFile.setText(Utils.convertTimeStamp(gameMasterFileLastModified));
			btnDownload.setEnabled(true);
		}
	}

	private class UploadGameMasterFile extends SwingWorker<Object, Object> {

		private UploadGameMasterFile() {
			btnUpload.setEnabled(false);
		}

		@Override
		protected Object doInBackground() throws Exception {
			GameFilesExchange gameFilesExchange = new GameFilesExchange();
			gameFilesExchange.uploadGameMasterFile();

			return null;
		}

		@Override
		protected void done() {
			btnUpload.setEnabled(true);
		}
	}

	private class Backup extends SwingWorker<Object, String> {

		String backupTimeStamp;
		String btnText;
		Path backupPath;
		Path cloudBackupPath;

		private Backup() {
			btnBackup.setEnabled(false);
			btnText = btnBackup.getText();
			backupTimeStamp = getTimeStampString();
			backupPath = AppProperties.APP_PATH_STRATEG.getParent()
					.resolve("backups" + File.separator + backupTimeStamp);
			cloudBackupPath = Paths.get("backups" + File.separator + backupTimeStamp + File.separator + "cloud");
		}

		@Override
		protected void process(List<String> chunks) {
			btnBackup.setText(chunks.get(chunks.size() - 1));
		}

		@Override
		protected Object doInBackground() throws Exception {

			new File(backupPath.toUri()).mkdirs();
			publish("local backup...");
			File localDirectory = AppProperties.APP_PATH_STRATEG.getParent().toFile();
			File[] localFiles = localDirectory.listFiles();
			for (File sourceFile : localFiles) {
				if (sourceFile.isFile()) {
					File targetFile = backupPath.resolve("local" + File.separator + sourceFile.getName()).toFile();
					FileUtils.copyFile(sourceFile, targetFile);
				}
			}

			Path cloudBackupFullPath = AppProperties.APP_PATH_STRATEG.getParent().resolve(cloudBackupPath);
			File cloudBackupFilesPath = new File(cloudBackupFullPath.toUri());
			cloudBackupFilesPath.mkdirs();
			for (int i = 1; i <= 10; i++) {
				String playerGroup = String.format("%02d", i);
				publish("cloud group " + playerGroup + "...");
				download(Paths.get(playerGroup + "_" + AppProperties.KEEP_ALIVE_LOG_FILENAME));
				download(Paths.get(playerGroup + "_" + AppProperties.UPLOAD_LOG_FILENAME));
				download(Paths.get(playerGroup + "_" + AppProperties.LOGIN_LOG_FILENAME));
				download(Paths.get(playerGroup + "_" + AppProperties.LOGOUT_LOG_FILENAME));
				download(Paths.get(playerGroup + "_" + AppProperties.USER_SETTINGS_FILENAME));
				download(Paths.get(playerGroup + "_" + AppProperties.RESET_GAME_FILENAME));
				download(Paths.get("ENTERG" + playerGroup + ".DAT"));
				download(Paths.get("KL_STA" + playerGroup + ".DAT"));
			}
			publish("cloud master files...");
			download(AppProperties.GAME_STATE_FILENAME);
			download(AppProperties.LAST_PLAYER_REQUEST_FILENAME);
			download(AppProperties.LEVEL_FILENAME);
			download(Paths.get("SL.DAT"));

			cloudBackupPath = cloudBackupPath.resolve(AppProperties.NEW_GAME_FILES_CLOUD_PATH);
			publish("cloud new game...");
			new File(AppProperties.APP_PATH_STRATEG.getParent().resolve(cloudBackupPath).toUri()).mkdirs();
			for (int i = 1; i <= 10; i++) {
				String playerGroup = String.format("%02d", i);
				download(AppProperties.NEW_GAME_FILES_CLOUD_PATH.resolve("KL_STA" + playerGroup + ".DAT"));
			}
			download(AppProperties.NEW_GAME_FILES_CLOUD_PATH.resolve("SL.DAT"));

			return null;
		}

		@Override
		protected void done() {
			btnBackup.setText(btnText);
			btnBackup.setEnabled(true);
		}

		private void download(Path path) {
			FileTransfer.downloadFile(path, cloudBackupPath.resolve(path.getFileName()));
		}

		private String getTimeStampString() {
			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat format = new SimpleDateFormat("YYY-MM-dd_HH-mm-ss");
			format.setTimeZone(TimeZone.getDefault());
			return format.format(date);
		}

	}

	private class PushNextLevel extends SwingWorker<Object, String> {

		String btnText;;

		private PushNextLevel() {
			btnPushNextLevel.setEnabled(false);
			btnText = btnPushNextLevel.getText();
		}

		@Override
		protected void process(List<String> chunks) {
			btnPushNextLevel.setText(chunks.get(chunks.size() - 1));
		}

		@Override
		protected Object doInBackground() throws Exception {

			if (getCurrentLevel() == 10) {
				publish("max reached!");
				reload();
				Thread.sleep(1500);
				return null;
			}

			GameFilesExchange gameFilesExchange = new GameFilesExchange();

			for (int i = 1; i <= 10; i++) {
				String playerGroup = String.format("%02d", i);
				publish("group " + playerGroup + "...");
				gameFilesExchange.uploadPlayerFiles(i);
			}

			publish("game master file...");
			gameFilesExchange.uploadGameMasterFile();

			publish("level...");
			GameProgress gameProgress = new GameProgress();
			gameProgress.setGameState(State.GAME_ON);
			gameProgress.setLevel(getCurrentLevel() + 1);
			reload();

			return null;
		}

		@Override
		protected void done() {
			btnPushNextLevel.setText(btnText);
			btnPushNextLevel.setEnabled(true);
		}

		public int getCurrentLevel() {
			PersistenceProvider cloudPersistence = new HiDrivePersistenceProvider();
			byte[] levelByteArray = new byte[0];
			Level cloudLevel = null;
			try {
				levelByteArray = cloudPersistence.read(AppProperties.LEVEL_FILENAME);
				cloudLevel = new Gson().fromJson(new String(levelByteArray), Level.class);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return cloudLevel.getLevel();
		}
	}

	private class UploadDefaultGame extends SwingWorker<Object, String> {

		String btnText;;

		private UploadDefaultGame() {
			btnUploadDefaultGame.setEnabled(false);
			btnText = btnUploadDefaultGame.getText();
		}

		@Override
		protected void process(List<String> chunks) {
			btnUploadDefaultGame.setText(chunks.get(chunks.size() - 1));
		}

		@Override
		protected Object doInBackground() throws Exception {
			for (int i = 1; i <= 10; i++) {
				String playerGroup = String.format("%02d", i);
				publish("group " + playerGroup + "...");
				FileTransfer
						.deleteFile(AppProperties.NEW_GAME_FILES_CLOUD_PATH.resolve("KL_STA" + playerGroup + ".DAT"));
				FileTransfer.uploadFile(AppProperties.getWorkingDirectory().resolve("KL_STA" + playerGroup + ".DAT"),
						AppProperties.NEW_GAME_FILES_CLOUD_PATH.resolve("KL_STA" + playerGroup + ".DAT"));
			}

			publish("game master file...");
			FileTransfer.deleteFile(AppProperties.NEW_GAME_FILES_CLOUD_PATH.resolve("SL.DAT"));
			FileTransfer.uploadFile(AppProperties.getWorkingDirectory().resolve("SL.DAT"),
					AppProperties.NEW_GAME_FILES_CLOUD_PATH.resolve("SL.DAT"));
			return null;
		}

		@Override
		protected void done() {
			btnUploadDefaultGame.setText(btnText);
			btnUploadDefaultGame.setEnabled(true);
		}

	}

	private class Reset extends SwingWorker<Object, String> {

		String btnText;;

		private Reset() {
			btnReset.setEnabled(false);
			btnText = btnReset.getText();
		}

		@Override
		protected void process(List<String> chunks) {
			btnReset.setText(chunks.get(chunks.size() - 1));
		}

		@Override
		protected Object doInBackground() throws Exception {
			publish("reset state...");
			GameProgress gameProgress = new GameProgress();
			gameProgress.setGameState(State.OPEN_FOR_NEW_PLAYERS);
			gameProgress.setLevel(0);
			publish("master file...");
			FileTransfer.deleteFile(Paths.get("SL.DAT"));
			FileTransfer.downloadFile(AppProperties.NEW_GAME_FILES_CLOUD_PATH.resolve("SL.DAT"), Paths.get("SL.DAT"));

			for (int i = 1; i <= 10; i++) {
				String playerGroup = String.format("%02d", i);
				publish("group " + playerGroup + "...");
				for (int j = 0; j <= 10; j++) {
					FileUtils.deleteQuietly(AppProperties.getWorkingDirectory()
							.resolve("KLIMAG" + playerGroup + ".P" + String.format("%02d", j)).toFile());
				}

				String fileName = "KL_STA" + playerGroup + ".DAT";
				FileUtils.deleteQuietly(AppProperties.getWorkingDirectory().resolve(fileName).toFile());
			}
			FileUtils.deleteQuietly(AppProperties.getWorkingDirectory().resolve("Auswertung.txt").toFile());
			reload();
			return null;
		}

		@Override
		protected void done() {
			btnReset.setText(btnText);
			btnReset.setEnabled(true);
		}

	}

}
