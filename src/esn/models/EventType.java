package esn.models;

import java.lang.reflect.Field;

import android.content.res.Resources;
import esn.activities.R;

public class EventType {
	public int EventTypeID;
	public String EventTypeName;
	public String LabelImage;
	public String Slug;
	public int Time;
	public int Status;

	public static int getIconId(int eventTypeID, int level) {
		int default_icon = R.drawable.ic_event_type_0_1;
		try {
			Field field = esn.activities.R.drawable.class
					.getField("ic_event_type_" + eventTypeID + "_" + level);

			if (field != null) {
				String value = field.get(esn.activities.R.class).toString();
				return Integer.parseInt(value);
			} else {
				field = esn.activities.R.drawable.class
						.getField("ic_event_type_0_" + level);
				String value = field.get(esn.activities.R.class).toString();
				return Integer.parseInt(value);

			}
		} catch (NoSuchFieldException e) {

			String value = "0";
			try {
				Field field = esn.activities.R.drawable.class
						.getField("ic_event_type_0_" + level);
				value = field.get(esn.activities.R.class).toString();
				return Integer.parseInt(value);
			} catch (IllegalArgumentException e1) {
				return default_icon;
			} catch (IllegalAccessException e1) {
				return default_icon;
			} catch (NoSuchFieldException ed) {
				return default_icon;
			}

		} catch (IllegalArgumentException e) {
			return default_icon;
		} catch (IllegalAccessException e) {
			return default_icon;
		}
	}

	public static int getID(String eventType) {
		if (eventType.equals("KET_XE")) {
			return 1;
		} else if (eventType.equals("LO_COT")) {
			return 2;
		} else if (eventType.equals("TAI_NAN")) {
			return 3;
		} else if (eventType.equals("LU_LUT")) {
			return 4;
		} else if (eventType.equals("LO_DAT")) {
			return 5;
		} else if (eventType.equals("DUONG_XAU")) {
			return 6;
		} else if (eventType.equals("CHAY_NO")) {
			return 7;
		} else if (eventType.equals("DUONG_CHAN")) {
			return 8;
		} else if (eventType.equals("DONG_DAT")) {
			return 9;
		}
		return 0;
	}
	
	public static String GetName(int id, Resources res)
	{
		switch (id) {
		case 1:
			return res.getString(R.string.event_type_ketxe);
		case 2:
			return res.getString(R.string.event_type_locot);
		case 3:
			return res.getString(R.string.event_type_tainangiaothong);
		case 4:
			return res.getString(R.string.event_type_lulut);
		case 5:
			return res.getString(R.string.event_type_lodat);
		case 6:
			return res.getString(R.string.event_type_duongxau);
		case 7:
			return res.getString(R.string.event_type_chayno);		
		case 8:
			return res.getString(R.string.event_type_duongbichan);
		case 9:
			return res.getString(R.string.event_type_dongdat);
		default:
			return res.getString(R.string.event_type_khac);
		}
	}
}
