package gib.controlling.server.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import gib.controlling.client.mappings.TimeStamp;
import gib.controlling.client.mappings.UserSettings;
import gib.controlling.client.setup.AppProperties;
import gib.controlling.persistence.FileTransfer;
import gib.controlling.persistence.SettingsPersistence;
import gib.controlling.server.controller.GameFilesExchange;
import gib.controlling.server.controller.PlayerData;
import gib.controlling.server.controller.Utils;
import gib.controlling.server.controller.PlayerData.OnlineState;
import gib.controlling.server.model.SessionLog;

public class Group extends JPanel {

	private JLabel lblLastUpload;
	private JSpinner spnLevel;
	private JTextField txtId;
	private JTextField txtVersion;
	private JTable tblSessions;
	private PlayerData playerData;
	private JButton btnReload;
	private JButton btnSave;
	private JButton btnDownload;
	private JButton btnUpload;
	private JButton btnReset;
	private JButton btnDelete;
	private int groupNumber;

	/**
	 * Create the panel.
	 */
	public Group(int groupNumber) {
		this.groupNumber = groupNumber;

		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagLayout gbl_groupPanel = new GridBagLayout();
		gbl_groupPanel.columnWidths = new int[] { 0, 0, 30, 30, 0 };
		gbl_groupPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_groupPanel.columnWeights = new double[] { 1.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_groupPanel.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
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
				new Reload().execute();
			}
		});
		btnReload.setHorizontalAlignment(SwingConstants.LEADING);
		header.add(btnReload);
		btnReload.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 11));
		btnReload.setToolTipText("reload");

		Component horizontalGlue = Box.createHorizontalGlue();
		header.add(horizontalGlue);

		JLabel lblGroupName = new JLabel("Gruppe " + String.format("%02d", groupNumber));
		lblGroupName.setFont(new Font("Dialog", Font.BOLD, 13));
		lblGroupName.setHorizontalAlignment(SwingConstants.CENTER);
		header.add(lblGroupName);

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
				new Download().execute();
			}
		});
		btnDownload.setToolTipText("load game files from player");
		btnDownload.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
		controls.add(btnDownload);

		btnUpload = new JButton("\u2B61");
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Upload().execute();
			}
		});
		btnUpload.setToolTipText("push game files to player");
		btnUpload.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
		controls.add(btnUpload);

		btnReset = new JButton("\u21E4");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Reset().execute();
			}
		});
		btnReset.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
		btnReset.setToolTipText("reset group to level 0");
		controls.add(btnReset);

		btnDelete = new JButton("\u2715");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Delete().execute();
			}
		});
		btnDelete.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 11));
		btnDelete.setToolTipText("delete group");
		controls.add(btnDelete);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 0;
		gbc_scrollPane.gridwidth = 4;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		add(scrollPane, gbc_scrollPane);

		JPanel gameData = new JPanel();
		scrollPane.setViewportView(gameData);
		GridBagLayout gbl_gameData = new GridBagLayout();
		gbl_gameData.columnWidths = new int[] { 0, 0, 0 };
		gbl_gameData.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_gameData.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gameData.setLayout(gbl_gameData);

		JLabel lblLastUploadTitle = new JLabel("Last Upload:");
		GridBagConstraints gbc_lblLastUploadTitle = new GridBagConstraints();
		gbc_lblLastUploadTitle.anchor = GridBagConstraints.WEST;
		gbc_lblLastUploadTitle.insets = new Insets(0, 5, 5, 5);
		gbc_lblLastUploadTitle.gridx = 0;
		gbc_lblLastUploadTitle.gridy = 0;
		gameData.add(lblLastUploadTitle, gbc_lblLastUploadTitle);

		lblLastUpload = new JLabel("");
		GridBagConstraints gbc_lblLastUpload = new GridBagConstraints();
		gbc_lblLastUpload.anchor = GridBagConstraints.WEST;
		gbc_lblLastUpload.insets = new Insets(0, 0, 5, 0);
		gbc_lblLastUpload.gridx = 1;
		gbc_lblLastUpload.gridy = 0;
		gameData.add(lblLastUpload, gbc_lblLastUpload);

		JLabel lblLevelTitle = new JLabel("Level:");
		GridBagConstraints gbc_lblLevelTitle = new GridBagConstraints();
		gbc_lblLevelTitle.anchor = GridBagConstraints.WEST;
		gbc_lblLevelTitle.insets = new Insets(0, 5, 5, 5);
		gbc_lblLevelTitle.gridx = 0;
		gbc_lblLevelTitle.gridy = 1;
		gameData.add(lblLevelTitle, gbc_lblLevelTitle);

		spnLevel = new JSpinner();
		spnLevel.setModel(new SpinnerNumberModel(0, 0, 10, 1));
		GridBagConstraints gbc_spnLevel = new GridBagConstraints();
		gbc_spnLevel.anchor = GridBagConstraints.WEST;
		gbc_spnLevel.insets = new Insets(0, 0, 5, 0);
		gbc_spnLevel.gridx = 1;
		gbc_spnLevel.gridy = 1;
		gameData.add(spnLevel, gbc_spnLevel);

		JLabel lblIdTitle = new JLabel("ID:");
		GridBagConstraints gbc_lblIdTitle = new GridBagConstraints();
		gbc_lblIdTitle.anchor = GridBagConstraints.WEST;
		gbc_lblIdTitle.insets = new Insets(0, 5, 5, 5);
		gbc_lblIdTitle.gridx = 0;
		gbc_lblIdTitle.gridy = 2;
		gameData.add(lblIdTitle, gbc_lblIdTitle);

		txtId = new JTextField();
		GridBagConstraints gbc_txtId = new GridBagConstraints();
		gbc_txtId.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtId.insets = new Insets(0, 0, 5, 0);
		gbc_txtId.gridx = 1;
		gbc_txtId.gridy = 2;
		gameData.add(txtId, gbc_txtId);
		txtId.setColumns(10);

		JLabel lblVersionTitle = new JLabel("Version:");
		GridBagConstraints gbc_lblVersionTitle = new GridBagConstraints();
		gbc_lblVersionTitle.anchor = GridBagConstraints.WEST;
		gbc_lblVersionTitle.insets = new Insets(0, 5, 5, 5);
		gbc_lblVersionTitle.gridx = 0;
		gbc_lblVersionTitle.gridy = 3;
		gameData.add(lblVersionTitle, gbc_lblVersionTitle);

		txtVersion = new JTextField();
		GridBagConstraints gbc_txtVersion = new GridBagConstraints();
		gbc_txtVersion.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtVersion.insets = new Insets(0, 0, 5, 0);
		gbc_txtVersion.gridx = 1;
		gbc_txtVersion.gridy = 3;
		gameData.add(txtVersion, gbc_txtVersion);
		txtVersion.setColumns(10);

		JPanel sessionLog = new JPanel();
		GridBagConstraints gbc_sessionLog = new GridBagConstraints();
		gbc_sessionLog.insets = new Insets(0, 1, 5, 0);
		gbc_sessionLog.anchor = GridBagConstraints.WEST;
		gbc_sessionLog.gridwidth = 2;
		gbc_sessionLog.gridx = 0;
		gbc_sessionLog.gridy = 4;
		gameData.add(sessionLog, gbc_sessionLog);
		sessionLog.setLayout(new BorderLayout(0, 0));

		tblSessions = new JTable();
		tblSessions.setBackground(UIManager.getColor("Button.background"));
		tblSessions.setShowHorizontalLines(false);
		tblSessions.setRowSelectionAllowed(false);
		sessionLog.add(tblSessions, BorderLayout.CENTER);
		tblSessions.setModel(new DefaultTableModel(new Object[][] { { "-", "-", "-" }, },
				new String[] { "Login:", "Keep Alive:", "Minutes:" }) {
			Class[] columnTypes = new Class[] { String.class, String.class, String.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		sessionLog.add(tblSessions.getTableHeader(), BorderLayout.NORTH);

		Component verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 0, 5);
		gbc_verticalStrut.weighty = 1.0;
		gbc_verticalStrut.gridx = 0;
		gbc_verticalStrut.gridy = 5;
		gameData.add(verticalStrut, gbc_verticalStrut);

	}

	private class Reload extends SwingWorker<Object, Object> {

		private String lastUpload;
		private int level;
		private String id;
		private String version;
		private SessionLog sessionLog;

		private Reload() {
			btnReload.setEnabled(false);
			sessionLog = new SessionLog();
		}

		@Override
		protected Object doInBackground() throws Exception {
			try {
				playerData = new PlayerData(groupNumber);
				lastUpload = Utils.convertTimeStamp(playerData.getLastUpload());
				level = playerData.getUserSettings().getLevel();
				id = playerData.getUserSettings().getPlayerUuid();
				version = String.valueOf(playerData.getUserSettings().getClientVersion());

				sessionLog.removeRow(0);

				TreeMap<Long, OnlineState> onlineLog = playerData.getOnlineLog();

				List<Entry<Long, OnlineState>> onlineLogEntries = new ArrayList<Map.Entry<Long, PlayerData.OnlineState>>(
						onlineLog.entrySet());
				ListIterator<Map.Entry<Long, PlayerData.OnlineState>> onlineLogIterator = onlineLogEntries
						.listIterator();

				while (onlineLogIterator.hasNext()) {
					Map.Entry<Long, PlayerData.OnlineState> logEntry = onlineLogIterator.next();

					long login = 0;
					long logout = 0;
					Long minutes = null;

					if (logEntry.getValue() == OnlineState.LOGIN) {
						login = logEntry.getKey();
						if (onlineLogIterator.previousIndex() == 0) {
							logout = playerData.getKeepAlive();
							if (logout >= login) {
								minutes = TimeUnit.MILLISECONDS.toMinutes(logout - login);
							}
						}
					} else if (logEntry.getValue() == OnlineState.LOGOUT) {
						logout = logEntry.getKey();

						if (onlineLogIterator.hasNext()) {
							Map.Entry<Long, PlayerData.OnlineState> logEntryNext = onlineLogIterator.next();
							if (logEntryNext.getValue() == OnlineState.LOGIN) {
								login = logEntryNext.getKey();
								if (logout >= login) {
									minutes = TimeUnit.MILLISECONDS.toMinutes(logout - login);
								}
							} else if (logEntryNext.getValue() == OnlineState.LOGOUT) {
								if (onlineLogIterator.hasPrevious()) {
									onlineLogIterator.previous();
								}
							}
						}
					}

					sessionLog.addRow(
							new Object[] { Utils.convertTimeStamp(login), Utils.convertTimeStamp(logout), minutes });

				}

			} catch (IOException e) {
				btnReload.setEnabled(true);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void done() {

			lblLastUpload.setText(lastUpload);
			spnLevel.setValue(level);
			txtId.setText(id);
			txtVersion.setText(version);
			tblSessions.setModel(sessionLog);
			tblSessions.getColumnModel().getColumn(0).setPreferredWidth(82);
			tblSessions.getColumnModel().getColumn(1).setPreferredWidth(82);
			tblSessions.getColumnModel().getColumn(2).setPreferredWidth(58);
			btnReload.setEnabled(true);

		}
	}

	private class Download extends SwingWorker<Object, Object> {

		private Download() {
			btnDownload.setEnabled(false);
		}

		@Override
		protected Object doInBackground() throws Exception {
			GameFilesExchange gameFilesExchange = new GameFilesExchange();
			gameFilesExchange.downloadPlayerFiles(groupNumber);

			return null;
		}

		@Override
		protected void done() {
			btnDownload.setEnabled(true);
		}
	}

	private class Upload extends SwingWorker<Object, Object> {

		private Upload() {
			btnUpload.setEnabled(false);
		}

		@Override
		protected Object doInBackground() throws Exception {
			GameFilesExchange gameFilesExchange = new GameFilesExchange();
			gameFilesExchange.uploadPlayerFiles(groupNumber);
			return null;
		}

		@Override
		protected void done() {
			btnUpload.setEnabled(true);
		}
	}

	private class Save extends SwingWorker<Object, Object> {

		String btnText;

		private Save() {
			btnSave.setEnabled(false);
			btnText = btnSave.getText();
		}

		@Override
		protected Object doInBackground() throws Exception {
			UserSettings userSettings = null;
			try {
				userSettings = playerData.getUserSettings();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (userSettings == null || userSettings.getPlayerGroup() != groupNumber) {
				btnSave.setText("error");
				Thread.sleep(1000);
				btnSave.setText(btnText);
				btnSave.setEnabled(true);
				return null;
			}
			userSettings.setLevel((Integer) spnLevel.getValue());
			userSettings.setPlayerUuid(txtId.getText());
			userSettings.setClientVersion(txtVersion.getText());

			SettingsPersistence settingsPersistence = SettingsPersistence.getInstance();

			settingsPersistence.setCloudSettings(userSettings);
			settingsPersistence.saveCloudSettings();

			return null;
		}

		@Override
		protected void done() {
			btnSave.setText(btnText);
			btnSave.setEnabled(true);
		}
	}

	private class Delete extends SwingWorker<Object, String> {

		String btnText;

		private Delete() {
			btnDelete.setEnabled(false);
			btnText = btnDelete.getText();
		}

		@Override
		protected void process(List<String> chunks) {
			btnDelete.setText(chunks.get(chunks.size() - 1));
		}

		@Override
		protected Object doInBackground() throws Exception {

			String playerGroup = String.format("%02d", groupNumber);
			List<String> cloudFiles = new ArrayList<String>();
			cloudFiles.add(playerGroup + "_" + AppProperties.KEEP_ALIVE_LOG_FILENAME);
			cloudFiles.add(playerGroup + "_" + AppProperties.UPLOAD_LOG_FILENAME);
			cloudFiles.add(playerGroup + "_" + AppProperties.LOGIN_LOG_FILENAME);
			cloudFiles.add(playerGroup + "_" + AppProperties.LOGOUT_LOG_FILENAME);
			cloudFiles.add(playerGroup + "_" + AppProperties.USER_SETTINGS_FILENAME);
			cloudFiles.add(playerGroup + "_" + AppProperties.RESET_GAME_FILENAME);
			cloudFiles.add("ENTERG" + playerGroup + ".DAT");
			cloudFiles.add("KL_STA" + playerGroup + ".DAT");
			for (String file : cloudFiles) {
				publish(file + "...");
				FileTransfer.deleteFile(Paths.get(file));
			}

			List<String> localFiles = new ArrayList<String>();
			localFiles.add("KL_STA" + playerGroup + ".DAT");
			for (int i = 0; i <= 10; i++) {
				localFiles.add("KLIMAG" + playerGroup + ".P" + String.format("%02d", i));
			}

			for (String file : localFiles) {
				publish(file + "...");
				FileUtils.deleteQuietly(AppProperties.getWorkingDirectory().resolve(file).toFile());
			}

			reload();

			return null;
		}

		@Override
		protected void done() {
			btnDelete.setText(btnText);
			btnDelete.setEnabled(true);
		}
	}

	private class Reset extends SwingWorker<Object, String> {

		String btnText;

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

			UserSettings userSettings = null;
			try {
				userSettings = playerData.getUserSettings();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (userSettings == null || userSettings.getPlayerGroup() != groupNumber) {
				btnReset.setText("n/a");
				Thread.sleep(1000);
				btnReset.setText(btnText);
				btnReset.setEnabled(true);
				return null;
			}

			String playerGroup = String.format("%02d", groupNumber);

			TimeStamp resetTimeStamp = new TimeStamp();
			String resetTimeStampJson = new Gson().toJson(resetTimeStamp);
			try {
				FileTransfer.getCloudPersistence().write(
						Paths.get(playerGroup + "_" + AppProperties.RESET_GAME_FILENAME),
						resetTimeStampJson.getBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			List<String> localFiles = new ArrayList<String>();
			localFiles.add("KL_STA" + playerGroup + ".DAT");
			for (int i = 0; i <= 10; i++) {
				localFiles.add("KLIMAG" + playerGroup + ".P" + String.format("%02d", i));
			}

			for (String file : localFiles) {
				publish(file + "...");
				FileUtils.deleteQuietly(AppProperties.getWorkingDirectory().resolve(file).toFile());
			}

			reload();

			return null;
		}

		@Override
		protected void done() {
			btnReset.setText(btnText);
			btnReset.setEnabled(true);
		}
	}

	public void reload() {
		new Reload().execute();
	}

	public void save() {
		new Save().execute();
	}

	public void delete() {
		new Delete().execute();
	}

	public void reset() {
		new Reset().execute();
	}

}
