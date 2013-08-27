package de.dakror.liturfaliar.map.data;

import java.awt.Graphics2D;
import java.lang.reflect.Modifier;

import org.json.JSONObject;

import de.dakror.liturfaliar.event.Listener;
import de.dakror.liturfaliar.map.Field;
import de.dakror.liturfaliar.map.Map;

public abstract class FieldData implements Listener
{
	public static final String[] DATATYPES = { "Door", "Spawner" };
	
	public abstract void construct();
	
	public abstract void update(Map m, Field f);
	
	public abstract void draw(Map m, Field f, Graphics2D g);
	
	public void loadData(JSONObject data) throws Exception
	{
		for (java.lang.reflect.Field f : getClass().getFields())
		{
			if (Modifier.isFinal(f.getModifiers())) continue;
			
			f.set(this, data.get(f.getName()));
		}
		
		construct();
	}
}
