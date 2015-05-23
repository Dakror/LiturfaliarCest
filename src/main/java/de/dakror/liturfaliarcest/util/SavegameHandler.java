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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.entity.Entity;
import de.dakror.liturfaliarcest.game.entity.creature.Player;
import de.dakror.liturfaliarcest.game.world.World;
import de.dakror.liturfaliarcest.settings.CFG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class SavegameHandler {
	static File dir;
	static {
		dir = new File(CFG.DIR, "saves");
		dir.mkdir();
	}
	
	public static void save(String savename) {
		try {
			if (savename == null) savename = "Spielstand vom " + new SimpleDateFormat("dd.MM.yy HH-mm-ss").format(new Date());
			
			File f = new File(dir, savename + ".save");
			JSONObject o = new JSONObject();
			o.put("w", Game.world.getData());
			
			Helper.setFileContent(f, o.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void load(String savename) {
		try {
			File f = new File(dir, savename + ".save");
			JSONObject o = new JSONObject(Helper.getFileContent(f));
			JSONObject w = o.getJSONObject("w");
			Game.world = new World(w.getString("n"));
			Game.player = new Player(0, 0);
			Game.world.addEntity(Game.player);
			Game.world.init();
			
			JSONArray e = w.getJSONArray("e");
			for (int i = 0; i < e.length(); i++) {
				JSONObject ent = e.getJSONObject(i);
				Entity entity = Game.world.getEntityForUID(ent.getInt("uid"));
				entity.setPos(ent.getJSONArray("pos"));
				if (ent.has("target")) entity.setTarget(ent.getJSONArray("target"));
			}
			
			Game.currentGame.layers.add(0, Game.world);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
