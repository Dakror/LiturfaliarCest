package de.dakror.liturfaliar.map.data;

import java.awt.Graphics2D;

import org.json.JSONObject;

import de.dakror.liturfaliar.event.Event;
import de.dakror.liturfaliar.map.Field;
import de.dakror.liturfaliar.map.Map;

public class Spawner implements FieldData
{
  public static final String[] ARGS = { "int_radius", "int_distance", "long_speed", "boolean_respawn", "jsonobject_npc" };
  int                          radius, distance;
  long                         speed;
  boolean                      respawn;
  
  /**
   * Fake NPC data.<br>
   * Includes only: <br>
   * equip(JSONObject), attr(JSONObject), random(JSONObject), char(String),<br>
   * ai(String), name(String), hostile(boolean), speed(double), h(int), w(int)
   */
  JSONObject                   npc;
  
  @Override
  public void onEvent(Event e)
  {}
  
  @Override
  public void update(Map m, Field f)
  {}
  
  @Override
  public void draw(Map m, Field f, Graphics2D g)
  {}
  
  @Override
  public void loadData(JSONObject data) throws Exception
  {
    for (String s : ARGS)
    {
      String name = s.substring(s.indexOf("_") + 1);
      java.lang.reflect.Field f = getClass().getField(name);
      f.set(this, data.get(name));
    }
  }
}
