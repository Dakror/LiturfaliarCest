package de.dakror.liturfaliar.map.data;

import java.awt.Graphics2D;

import org.json.JSONObject;

import de.dakror.liturfaliar.event.Listener;
import de.dakror.liturfaliar.map.Field;
import de.dakror.liturfaliar.map.Map;

public interface FieldData extends Listener
{
  public static final String[] DATATYPES = { "Door", "Spawner" };
  
  public void update(Map m, Field f);
  
  public void draw(Map m, Field f, Graphics2D g);
  
  public void loadData(JSONObject data) throws Exception;
}
