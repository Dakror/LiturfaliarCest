package de.dakror.liturfaliar.map.data;

import java.awt.Graphics2D;

import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Field;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.event.MapEventListener;

public interface FieldData extends MapEventListener
{
  public static final String[] DATATYPES = { "Door" };
  
  public void update(Map m, Field f);
  
  public void draw(Map m, Field f, Graphics2D g, Viewport v);
  
  public void loadData(JSONObject data) throws Exception;
}
