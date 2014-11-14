package de.dakror.liturfaliarcest.util;

import org.json.JSONArray;
import org.json.JSONException;

import de.dakror.gamesetup.util.Vector;

/**
 * @author Dakror
 */
public class Assistant {
	public static JSONArray serializeVector(Vector v) throws JSONException {
		JSONArray a = new JSONArray();
		a.put(v.x);
		a.put(v.y);
		return a;
	}
}
