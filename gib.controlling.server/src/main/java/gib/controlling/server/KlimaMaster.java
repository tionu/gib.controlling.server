package gib.controlling.server;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.PropertyConfigurator;

import gib.controlling.client.setup.AppProperties;
import gib.controlling.server.controller.Utils;
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
				if (!Files.exists(AppProperties.APP_PATH_STRATEG)) {
					Utils.createWorkingDirectory();
					URL source = KlimaMaster.class.getResource("setup/master.bin");
					byte[] buffer = new byte[1024];
					ZipInputStream zis;
					try {
						zis = new ZipInputStream(source.openStream());
						ZipEntry zipEntry = zis.getNextEntry();
						while (zipEntry != null) {
							String fileName = zipEntry.getName();
							File newFile = AppProperties.APP_PATH_STRATEG.getParent().resolve(fileName).toFile();
							FileOutputStream fos = new FileOutputStream(newFile);
							int len;
							while ((len = zis.read(buffer)) > 0) {
								fos.write(buffer, 0, len);
							}
							fos.close();
							zipEntry = zis.getNextEntry();
						}
						zis.closeEntry();
						zis.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

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
			props.load(KlimaMaster.class.getResourceAsStream("setup/log4j.properties"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PropertyConfigurator.configure(props);
	}

}
