package de.dakror.liturfaliarcest.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.world.World;

/**
 * @author Dakror
 */
public class JSInvoker
{
	public static String mainjs;
	
	static
	{
		mainjs = Helper.getURLContent(JSInvoker.class.getResource("/main/resources/main.js"));
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
		catch (Exception e)
		{
			e.printStackTrace();
			String[] lines = (mainjs + "(" + code + ")(" + p + ")").split("\r\n");
			for (int i = 0; i < lines.length; i++)
				System.err.println((i + 1) + " " + (e instanceof ScriptException && ((ScriptException) e).getLineNumber() == i + 1 ? "ERROR>>> " : "") + lines[i]);
			Game.w.dispose();
			JOptionPane.showMessageDialog(Game.w, "Ein Fehler in der Event Programmierung ist aufgetreten:\n" + e.getMessage(), "Kritischer Fehler!", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
}
