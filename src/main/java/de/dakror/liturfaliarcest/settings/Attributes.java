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


package de.dakror.liturfaliarcest.settings;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Dakror
 */
public class Attributes {
	/**
	 * Attributes about speed are expressed as ticks
	 * 
	 * @author Dakror
	 */
	public enum Attribute {
		HEALTH(0),
		HEALTH_MAX(0),
		DAMAGE(0),
		STAMINA(0),
		STAMINA_COST(0),
		STAMINA_MAX(0),
		MANA(0),
		MANA_COST(0),
		MANA_MAX(0),
		ATTACK_SPEED(0),
		SPEED(0),
		
		;
		
		private float defaultValue;
		
		private Attribute(float defaultValue) {
			this.defaultValue = defaultValue;
		}
		
		public float getDefaultValue() {
			return defaultValue;
		}
	}
	
	HashMap<Attribute, Float> attr = new HashMap<>();
	
	public Attributes() {
		for (Attribute t : Attribute.values())
			attr.put(t, t.getDefaultValue());
	}
	
	public Attributes(JSONObject data) throws JSONException {
		this();
		
		JSONArray names = data.names();
		for (int i = 0; i < data.length(); i++) {
			attr.put(Attribute.valueOf(names.getString(i)), (float) data.getDouble(names.getString(i)));
		}
	}
	
	public float get(Attribute t) {
		return attr.get(t);
	}
	
	public Attributes set(Attribute t, float value) {
		attr.put(t, value);
		return this;
	}
	
	public Attributes add(Attribute t, float value) {
		attr.put(t, get(t) + value);
		
		return this;
	}
	
	public void setWithMax(Attribute t, float value) {
		Attribute t2 = Attribute.valueOf(t.name() + "_MAX");
		set(t, value);
		set(t2, value);
	}
	
	public int size() {
		int s = 0;
		
		for (Attribute r : attr.keySet())
			if (attr.get(r) != 0) s++;
		
		return s;
	}
	
	public JSONObject getData() {
		JSONObject o = new JSONObject();
		try {
			for (Attribute a : Attribute.values())
				o.put(a.name(), get(a));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o;
	}
	
	@Override
	public String toString() {
		return attr.toString();
	}
	
	@Override
	public Attributes clone() {
		try {
			return new Attributes(getData());
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
