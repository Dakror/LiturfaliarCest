package de.dakror.liturfaliarcest.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.settings.CFG;

/**
 * @author Dakror
 */
public class SavegameHandler
{
	static File dir;
	static
	{
		dir = new File(CFG.DIR, "saves");
		dir.mkdir();
	}
	
	public static void save(String savename)
	{
		try
		{
			if (savename == null) savename = "Spielstand vom " + new SimpleDateFormat("dd.MM.yy HH-mm-ss").format(new Date());
			
			File f = new File(dir, savename + ".save");
			JSONObject o = new JSONObject();
			o.put("w", Game.world.getData());
			
			Helper.setFileContent(f, o.toString());
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
}
