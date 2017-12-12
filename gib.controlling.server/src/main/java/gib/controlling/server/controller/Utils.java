package gib.controlling.server.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import gib.controlling.client.setup.AppProperties;

public class Utils {

	public static String convertTimeStamp(long time) {
		if (time == 0) {
			return "-";
		}
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat(AppProperties.DATE_FORMAT);
		format.setTimeZone(TimeZone.getDefault());
		return format.format(date);
	}

}
