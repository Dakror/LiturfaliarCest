package de.dakror.liturfaliar.map.data;

import java.awt.Graphics2D;

import org.json.JSONObject;

import de.dakror.liturfaliar.event.Event;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.map.Field;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.NPC;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.util.Database;
import de.dakror.liturfaliar.util.Vector;

public class Spawner implements FieldData
{
  public static final String[] ARGS = { "int_radius", "int_distance", "int_speed", "boolean_respawn", "jsonobject_npc" };
  public int                   radius, distance, speed;
  public boolean               respawn;
  
  public JSONObject            npc;
  
  boolean                      checkedRespawn;
  boolean                      spawn;
  
  long                         time;
  
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
    
    if (spawn && System.currentTimeMillis() - time > speed)
    {
      if (m.getPlayer().getPos().getDistance(f.getNode()) > distance) // player out of range
      {
        try
        {
          Vector rp = getRandomPoint();
          JSONObject random = npc.getJSONObject("random");
          NPC mob = new NPC((int) Math.round(rp.x + f.getNode().x), (int) Math.round(rp.y + f.getNode().y), npc.getInt("w"), npc.getInt("h"), (int) (Math.random() * 4), npc.getString("name"), npc.getString("char"), npc.getDouble("speed"), random.getBoolean("move"), random.getBoolean("look"), random.getInt("moveT"), random.getInt("lookT"), npc.getBoolean("hostile"), -1, new Attributes(npc.getJSONObject("attr")), new Equipment(npc.getJSONObject("equip")), npc.getJSONArray("talk"), npc.getString("ai"));
          m.creatures.add(mob);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
      time = System.currentTimeMillis();
    }
  }
  
  public Vector getRandomPoint()
  {
    double rad = Math.random() * radius;
    double angle = Math.random() * 360;
    
    return new Vector(Math.cos(angle) * rad, Math.sin(angle) * rad);
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
    time = 0;
  }
}
