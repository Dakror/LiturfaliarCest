package de.dakror.liturfaliar.map.data;

import java.awt.Graphics2D;

import org.json.JSONObject;

import de.dakror.liturfaliar.event.Event;
import de.dakror.liturfaliar.map.Field;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.util.Database;

public class Spawner implements FieldData
{
  public static final String[] ARGS = { "int_radius", "int_distance", "int_speed", "boolean_respawn", "jsonobject_npc" };
  public int                   radius, distance, speed;
  public boolean               respawn;
  
  public JSONObject            npc;
  
  boolean                      checkedRespawn;
  boolean                      spawn;
  
  @Override
  public void onEvent(Event e)
  {}
  
  @Override
  public void update(Map m, Field f)
  {
    if (!checkedRespawn && !respawn && Database.getBooleanVar("spawner_" + f.uID() + "_respawn"))
    {
      if (Database.getBooleanVar("spawner_" + f.uID() + "_respawn")) spawn = false;
      else Database.setBooleanVar("spawner_" + f.uID() + "_respawn", true);
      checkedRespawn = true;
    }
  }
  
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
    checkedRespawn = false;
    spawn = true;
  }
}
