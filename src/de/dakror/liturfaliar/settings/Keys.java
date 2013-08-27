package de.dakror.liturfaliar.settings;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

import org.json.JSONObject;

public class Keys
{
	public static int PAUSE = KeyEvent.VK_ESCAPE;
	public static int INVENTORY = KeyEvent.VK_I;
	public static int SKILLS = KeyEvent.VK_J;
	
	public static int UP = KeyEvent.VK_W;
	public static int LEFT = KeyEvent.VK_A;
	public static int DOWN = KeyEvent.VK_S;
	public static int RIGHT = KeyEvent.VK_D;
	
	public static int SPRINT = KeyEvent.VK_SHIFT;
	
	public static void loadKeys(JSONObject o)
	{
		for (Field f : Keys.class.getFields())
		{
			try
			{
				f.setInt(null, o.getInt(f.getName()));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static JSONObject saveKeys()
	{
		JSONObject o = new JSONObject();
		
		for (Field f : Keys.class.getFields())
		{
			try
			{
				o.put(f.getName(), f.getInt(null));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return o;
	}
}
