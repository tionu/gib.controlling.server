package gib.controlling.server.view;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;

import gib.controlling.server.controller.GameFilesExchange;

public class AllGroups extends JPanel {

	private JButton btnReload;
	private JButton btnSave;
	private DashBoard dashboard;
	private JButton btnDownload;
	private JButton btnUpload;
	private JButton btnReset;
	private JButton btnDelete;

	/**
	 * Create the panel.
	 */
	public AllGroups(final DashBoard dashboard) {

		this.dashboard = dashboard;

		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagLayout gbl_groupPanel = new GridBagLayout();
		gbl_groupPanel.columnWidths = new int[] { 0, 0, 30, 30, 0 };
		gbl_groupPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_groupPanel.columnWeights = new double[] { 1.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_groupPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
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
		btnReload.setToolTipText("reload all groups");

		Component horizontalGlue = Box.createHorizontalGlue();
		header.add(horizontalGlue);

		JLabel lblGroupName = new JLabel("Alle Gruppen");
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
		btnSave.setToolTipText("save all groups");

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
		btnDownload.setToolTipText("load game files from all players");
		btnDownload.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
		controls.add(btnDownload);

		btnUpload = new JButton("\u2B61");
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Upload().execute();
			}
		});
		btnUpload.setToolTipText("push game files to all players");
		btnUpload.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
		controls.add(btnUpload);

		btnReset = new JButton("\u21E4");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Reset().execute();
			}
		});
		btnReset.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
		btnReset.setToolTipText("reset all groups to level 0");
		controls.add(btnReset);

		btnDelete = new JButton("\u2715");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Delete().execute();
			}
		});
		btnDelete.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 11));
		btnDelete.setToolTipText("delete all groups");
		controls.add(btnDelete);

	}

	private class Reload extends SwingWorker<Object, Object> {

		private Reload() {
			btnReload.setEnabled(false);
		}

		@Override
		protected Object doInBackground() throws Exception {
			for (Group group : dashboard.getGroups()) {
				group.reload();
			}
			dashboard.getGameSettings().reload();
			return null;
		}

		@Override
		protected void done() {
			btnReload.setEnabled(true);
		}

	}

	private class Save extends SwingWorker<Object, Object> {

		private Save() {
			btnSave.setEnabled(false);
		}

		@Override
		protected Object doInBackground() throws Exception {
			for (Group group : dashboard.getGroups()) {
				group.save();
			}
			return null;
		}

		@Override
		protected void done() {
			btnSave.setEnabled(true);
		}

	}

	private class Download extends SwingWorker<Object, String> {

		String btnText;;

		private Download() {
			btnDownload.setEnabled(false);
			btnText = btnDownload.getText();
		}

		@Override
		protected void process(List<String> chunks) {
			btnDownload.setText(chunks.get(chunks.size() - 1));
		}

		@Override
		protected Object doInBackground() throws Exception {
			GameFilesExchange gameFilesExchange = new GameFilesExchange();

			for (int i = 1; i <= 10; i++) {
				String playerGroup = String.format("%02d", i);
				publish("group " + playerGroup + "...");
				gameFilesExchange.downloadPlayerFiles(i);
			}

			return null;
		}

		@Override
		protected void done() {
			btnDownload.setText(btnText);
			btnDownload.setEnabled(true);
		}
	}

	private class Upload extends SwingWorker<Object, String> {

		String btnText;;

		private Upload() {
			btnUpload.setEnabled(false);
			btnText = btnUpload.getText();
		}

		@Override
		protected void process(List<String> chunks) {
			btnUpload.setText(chunks.get(chunks.size() - 1));
		}

		@Override
		protected Object doInBackground() throws Exception {
			GameFilesExchange gameFilesExchange = new GameFilesExchange();

			for (int i = 1; i <= 10; i++) {
				String playerGroup = String.format("%02d", i);
				publish("group " + playerGroup + "...");
				gameFilesExchange.uploadPlayerFiles(i);
			}

			return null;
		}

		@Override
		protected void done() {
			btnUpload.setText(btnText);
			btnUpload.setEnabled(true);
		}
	}

	private class Delete extends SwingWorker<Object, Object> {

		private Delete() {
			btnDelete.setEnabled(false);
		}

		@Override
		protected Object doInBackground() throws Exception {
			for (Group group : dashboard.getGroups()) {
				group.delete();
			}
			return null;
		}

		@Override
		protected void done() {
			btnDelete.setEnabled(true);
		}

	}

	private class Reset extends SwingWorker<Object, Object> {

		private Reset() {
			btnReset.setEnabled(false);
		}

		@Override
		protected Object doInBackground() throws Exception {
			for (Group group : dashboard.getGroups()) {
				group.reset();
			}
			return null;
		}

		@Override
		protected void done() {
			btnReset.setEnabled(true);
		}

	}

}
