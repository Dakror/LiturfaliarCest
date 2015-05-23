/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
 

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
public class JSInvoker {
	public static String mainjs, jsbeautifier;
	
	static {
		mainjs = Helper.getURLContent(JSInvoker.class.getResource("/main.js"));
		jsbeautifier = Helper.getURLContent(JSInvoker.class.getResource("/jsbeautifier.js"));
	}
	
	public static String beautifyJavaScript(String code) {
		try {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
			code = code.replace("\\\"", "\\\\\"");
			code = code.replace("\"", "\\\"");
			engine.eval(jsbeautifier + "var result = js_beautify(\"" + code + "\");");
			return (String) engine.get("result");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void invoke(String code, Object... params) {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		engine.put("game", Game.currentGame);
		engine.put("tilesize", World.TILE_SIZE);
		String p = "";
		for (int i = 0; i < params.length; i++) {
			p += "p" + i + ",";
			engine.put("p" + i, params[i]);
		}
		
		if (p.length() > 0) p = p.substring(0, p.length() - 1);
		
		try {
			engine.eval(mainjs + "(" + code + ")(" + p + ")");
		} catch (Exception e) {
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
