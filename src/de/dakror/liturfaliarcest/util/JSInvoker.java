package de.dakror.liturfaliarcest.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.world.World;

/**
 * @author Dakror
 */
public class JSInvoker
{
	static String mainjs;
	
	static
	{
		mainjs = Helper.getURLContent(JSInvoker.class.getResource("/main.js"));
	}
	
	public static void invoke(String code, Object... params)
	{
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		engine.put("game", Game.currentGame);
		engine.put("tilesize", World.TILE_SIZE);
		String p = "";
		for (int i = 0; i < params.length; i++)
		{
			p += "p" + i + ",";
			engine.put("p" + i, params[i]);
		}
		
		if (p.length() > 0) p = p.substring(0, p.length() - 1);
		
		try
		{
			engine.eval(mainjs + "(" + code + ")(" + p + ")");
		}
		catch (ScriptException e)
		{
			e.printStackTrace();
		}
	}
}
