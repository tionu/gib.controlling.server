package gib.controlling.server.view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class DashBoard {

	private JFrame frmKlimaMaster;

	private List<Group> groups;

	private GameMaster gameSettings;

	public DashBoard() {
		groups = new ArrayList<Group>();
		initialize();
	}

	private void initialize() {

		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		frmKlimaMaster = new JFrame();
		frmKlimaMaster.setTitle("Klima Master");
		frmKlimaMaster.setBounds(100, 100, 1024, 720);
		frmKlimaMaster.setExtendedState(Frame.MAXIMIZED_BOTH);
		frmKlimaMaster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel settings = new JPanel();
		frmKlimaMaster.getContentPane().add(settings, BorderLayout.CENTER);
		settings.setLayout(new GridLayout(0, 4, 0, 0));

		Group group01 = new Group(1);
		groups.add(group01);
		settings.add(group01);

		Group group02 = new Group(2);
		groups.add(group02);
		settings.add(group02);

		Group group03 = new Group(3);
		groups.add(group03);
		settings.add(group03);

		Group group04 = new Group(4);
		groups.add(group04);
		settings.add(group04);

		Group group05 = new Group(5);
		groups.add(group05);
		settings.add(group05);

		Group group06 = new Group(6);
		groups.add(group06);
		settings.add(group06);

		Group group07 = new Group(7);
		groups.add(group07);
		settings.add(group07);

		Group group08 = new Group(8);
		groups.add(group08);
		settings.add(group08);

		Group group09 = new Group(9);
		groups.add(group09);
		settings.add(group09);

		Group group10 = new Group(10);
		groups.add(group10);
		settings.add(group10);

		AllGroups allGroups = new AllGroups(this);
		settings.add(allGroups);

		gameSettings = new GameMaster();
		settings.add(gameSettings);
	}

	public JFrame getFrame() {
		return frmKlimaMaster;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public GameMaster getGameSettings() {
		return gameSettings;
	}

}
