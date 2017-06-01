package com.henu.swface.Utils;

import android.provider.ContactsContract;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/5/30.
 */

public class DateUtil {

	public static Date getDateFromLong(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return calendar.getTime();
	}
}
