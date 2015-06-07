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


package de.dakror.liturfaliarcest.game.entity;

import java.util.ArrayList;

import de.dakror.liturfaliarcest.game.entity.object.Object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class EntityType {
	public static ArrayList<EntityType> entityTypes;
	
	public static void init() {
		try {
			JSONArray a = new JSONArray(Helper.getURLContent(Object.class.getResource("/entities.entlist")));
			entityTypes = new ArrayList<>();
			for (int i = 0; i < a.length(); i++) {
				JSONObject o = a.getJSONObject(i);
				EntityType ot = new EntityType();
				ot.tileset = o.getString("t");
				ot.tx = o.getInt("x");
				ot.ty = o.getInt("y");
				ot.width = o.getInt("w");
				ot.height = o.getInt("h");
				ot.bumpX = o.getInt("bx");
				ot.bumpY = o.getInt("by");
				ot.bumpWidth = o.getInt("bw");
				ot.bumpHeight = o.getInt("bh");
				entityTypes.add(ot);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String tileset;
	public int tx;
	public int ty;
	public int width;
	public int height;
	public int bumpX;
	public int bumpY;
	public int bumpWidth;
	public int bumpHeight;
}
