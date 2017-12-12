package gib.controlling.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import gib.controlling.server.controller.PlayerData;

public class PlayerDataTest {

	@Before
	public void setUp() throws Exception {
		Properties props = new Properties();
		try {
			props.load(PlayerData.class.getResourceAsStream("setup/log4j.properties"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PropertyConfigurator.configure(props);
	}

	@Test
	public void testGetPlayerLevels() {
		System.out.println("Gruppe:\tLevel:");
		for (int i = 1; i <= 10; i++) {
			PlayerData playerData = new PlayerData(i);
			try {
				System.out.println(i + "\t" + playerData.getUserSettings().getLevel());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testGetLastUpload() {
		System.out.println("Gruppe:\tLetzter Upload:");
		for (int i = 1; i <= 10; i++) {
			PlayerData playerData = new PlayerData(i);
			System.out.println(i + "\t" + convertTimeStamp(playerData.getLastUpload()));
		}

	}

	@Test
	public void testGetKeepAlive() {
		System.out.println("Gruppe:\tKeep Alive:");
		for (int i = 1; i <= 10; i++) {
			PlayerData playerData = new PlayerData(i);
			System.out.println(i + "\t" + convertTimeStamp(playerData.getKeepAlive()));
		}
	}

	@Test
	public void testGetOnlineLog() {
		for (int i = 1; i <= 10; i++) {
			System.out.println("Gruppe: " + i);
			PlayerData playerData = new PlayerData(i);
			for (long timeStamp : playerData.getOnlineLog().keySet()) {
				System.out.println(convertTimeStamp(timeStamp) + ": " + playerData.getOnlineLog().get(timeStamp));
			}
		}
	}

	public static String convertTimeStamp(long time) {
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		format.setTimeZone(TimeZone.getDefault());
		return format.format(date);
	}
}
