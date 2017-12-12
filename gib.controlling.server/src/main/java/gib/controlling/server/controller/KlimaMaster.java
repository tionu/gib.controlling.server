package gib.controlling.server.controller;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import gib.controlling.client.setup.AppProperties;
import gib.controlling.server.GameFilesExchange;
import gib.controlling.server.view.DashBoard;
import gib.controlling.server.view.Group;

public class KlimaMaster {

	private static final DashBoard DASHBOARD = new DashBoard();

	public static void main(String[] args) throws Exception {

		initLog();
		AppProperties.APP_PATH = AppProperties.APP_PATH_STRATEG;
		DASHBOARD.getFrame().setVisible(true);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					for (Group group : DASHBOARD.getGroups()) {
						group.reload();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	private static void initLog() {
		Properties props = new Properties();
		try {
			props.load(GameFilesExchange.class.getResourceAsStream("setup/log4j.properties"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PropertyConfigurator.configure(props);
	}

}
