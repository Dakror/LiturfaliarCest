package de.dakror.liturfaliarcest.game.entity.object;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class ObjectType
{
	public static ArrayList<ObjectType> objectTypes;
	
	public static void init()
	{
		try
		{
			JSONArray a = new JSONArray(Helper.getURLContent(Object.class.getResource("/entities.entlist")));
			objectTypes = new ArrayList<>();
			for (int i = 0; i < a.length(); i++)
			{
				JSONObject o = a.getJSONObject(i);
				ObjectType ot = new ObjectType();
				ot.tileset = o.getString("t");
				ot.tx = o.getInt("x");
				ot.ty = o.getInt("y");
				ot.width = o.getInt("w");
				ot.height = o.getInt("h");
				ot.bumpX = o.getInt("bx");
				ot.bumpY = o.getInt("by");
				ot.bumpWidth = o.getInt("bw");
				ot.bumpHeight = o.getInt("bh");
				objectTypes.add(ot);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	
	String tileset;
	int tx;
	int ty;
	int width;
	int height;
	int bumpX;
	int bumpY;
	int bumpWidth;
	int bumpHeight;
}
