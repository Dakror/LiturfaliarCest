package de.dakror.liturfaliar.map.creature;

import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.map.Field;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.Balance;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.ui.Talk;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Vector;

public class Player extends Creature
{
  private boolean   init                = true;
  
  public JSONObject data;
  
  // -- up -- left -- right -- down -- //
  boolean[]         dirs                = { false, false, false, false };
  Vector            lastPos;
  Vector            relPos;
  
  public boolean    preventTargetChoose = false;
  public int        dirAfterReachedGoal = -1;
  
  boolean           sprint;
  long              time;
  
  public Player(JSONObject save, Window w)
  {
    super(CFG.MAPCENTER.x, CFG.MAPCENTER.y, CFG.HUMANBOUNDS[0], CFG.HUMANBOUNDS[1]);
    
    setHuman();
    
    massive = true;
    layer = CFG.PLAYERLAYER;
    setData(save);
    frozen = false;
    try
    {
      equipment = new Equipment(save.getJSONObject("char").getJSONObject("equip"));
      
      relPos = goTo = new Vector(save.getJSONObject("mappack").getJSONObject("pos").getInt("x"), save.getJSONObject("mappack").getJSONObject("pos").getInt("y"));
      
      attr.loadAttributes(save.getJSONObject("attr"));
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  @Override
  public void move(Map map)
  {
    if (!frozen)
    {
      Vector targetVector = relPos.sub(goTo);
      double distance = targetVector.length;
      if (targetVector.length >= getSpeed())
      {
        distance = getSpeed();
      }
      if (!map.getBumpMap().contains(new Rectangle2D.Double(pos.sub(targetVector.setLength(distance)).coords[0] + bx, pos.sub(targetVector.setLength(distance)).coords[1] + by, bw, bh)))
      {
        setTarget((int) relPos.coords[0], (int) relPos.coords[1]);
        return;
      }
      for (Creature c : map.creatures)
      {
        if (c instanceof Player || (c instanceof NPC && !((NPC) c).isHostile()))
          continue;
        if (c.getBumpArea(map).intersects(new Rectangle2D.Double(relPos.sub(targetVector.setLength(distance)).coords[0] + bx, relPos.sub(targetVector.setLength(distance)).coords[1] + by, bw, bh)))
        {
          setTarget((int) relPos.coords[0], (int) relPos.coords[1]);
          return;
        }
      }
      lastPos = relPos;
      relPos = relPos.sub(targetVector.setLength(distance));
    }
  }
  
  public String getName()
  {
    try
    {
      return getData().getJSONObject("char").getString("name");
    }
    catch (JSONException e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
  @Override
  public void update(long timePassed, Map m)
  {
    if (init)
    {
      m.setPos(CFG.MAPCENTER.x - getRelativePos(m)[0], CFG.MAPCENTER.y - getRelativePos(m)[1]);
      init = false;
    }
    
    
    if (!sprint)
    {
      if ((System.currentTimeMillis() - time) > Balance.Player.STAMINAREGEN && attr.getAttribute(Attr.stamina).getValue() < attr.getAttribute(Attr.stamina).getMaximum())
      {
        attr.getAttribute(Attr.stamina).increaseValue(1);
        time = System.currentTimeMillis();
      }
    }
    
    if (sprint && (dirs[0] || dirs[1] || dirs[2] || dirs[3]) && (System.currentTimeMillis() - time) > Balance.Player.STAMINADECREASE && attr.getAttribute(Attr.stamina).getValue() > 0 && !m.isPeaceful())
    {
      attr.getAttribute(Attr.stamina).decreaseValue(1);
      time = System.currentTimeMillis();
    }
    if (attr.getAttribute(Attr.stamina).getValue() == 0)
    {
      sprint = false;
    }
    
    
    
    setSpeed((sprint) ? Balance.Player.SPRINT : Balance.Player.WALK);
    
    double x = 0, y = 0;
    if (dirs[0] && !dirs[3] && m.getBumpMap().contains(new Rectangle2D.Double(m.getX() + relPos.coords[0] + bx, m.getY() + relPos.coords[1] + by - getSpeed() * 2, bw, bh)))
      y -= getSpeed();
    else if (dirs[3] && !dirs[0] && m.getBumpMap().contains(new Rectangle2D.Double(m.getX() + relPos.coords[0] + bx, m.getY() + relPos.coords[1] + by + getSpeed() * 2, bw, bh)))
      y += getSpeed();
    if (dirs[1] && !dirs[2] && m.getBumpMap().contains(new Rectangle2D.Double(m.getX() + relPos.coords[0] + bx - getSpeed() * 2, m.getY() + relPos.coords[1] + by, bw, bh)))
      x -= getSpeed();
    else if (dirs[2] && !dirs[1] && m.getBumpMap().contains(new Rectangle2D.Double(m.getX() + relPos.coords[0] + bx + getSpeed() * 2, m.getY() + relPos.coords[1] + by, bw, bh)))
      x += getSpeed();
    
    if (x != 0 || y != 0)
    {
      goTo = new Vector(getRelativePos(m)[0] + x, getRelativePos(m)[1] + y);
      m.setPos(CFG.MAPCENTER.x - getRelativePos(m)[0], CFG.MAPCENTER.y - getRelativePos(m)[1]);
      move(m);
    }
    for (Field f : m.fields)
    {
      if (getBumpArea(m).contains(new Point2D.Double(f.getX() + CFG.FIELDSIZE * 0.5, f.getY() + CFG.FIELDSIZE * 0.5)))
      {
        f.fieldTriggered(this, m);
      }
      else if (getBumpArea(m).intersects(f.getX(), f.getY(), CFG.FIELDSIZE, CFG.FIELDSIZE))
      {
        f.fieldTouched(this, m);
      }
    }
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v, Map m)
  {
    super.draw(g, v, m);
    
    int frame = 0;
    
    int x = 0, y = 0;
    
    if (dirs[0])
      y--;
    if (dirs[3])
      y++;
    if (dirs[1])
      x--;
    if (dirs[2])
      x++;
    
    if ((x != 0 || y != 0) && !frozen)
      frame = v.getFrame((sprint) ? 0.3f : 0.5f);
    
    if (dirs[0] && !dirs[3])
      dir = 3;
    if (dirs[1] && !dirs[2])
      dir = 1;
    if (dirs[2] && !dirs[1])
      dir = 2;
    if (dirs[3] && !dirs[0])
      dir = 0;
    
    Assistant.drawChar(CFG.MAPCENTER.x, CFG.MAPCENTER.y, w, h, dir, frame, equipment, g, v.w, true);
  }
  
  @Override
  public void keyPressed(KeyEvent e, Map m)
  {
    if (frozen)
      return;
    switch (e.getExtendedKeyCode())
    {
      case KeyEvent.VK_W:
      {
        dirs[0] = true;
        break;
      }
      case KeyEvent.VK_A:
      {
        dirs[1] = true;
        break;
      }
      case KeyEvent.VK_D:
      {
        dirs[2] = true;
        break;
      }
      case KeyEvent.VK_S:
      {
        dirs[3] = true;
        break;
      }
      case KeyEvent.VK_SHIFT:
      {
        if (!sprint)
          time = System.currentTimeMillis();
        
        sprint = true;
        break;
      }
    }
  }
  
  @Override
  public void keyReleased(KeyEvent e, Map m)
  {
    switch (e.getExtendedKeyCode())
    {
      case KeyEvent.VK_W:
      {
        dirs[0] = false;
        break;
      }
      case KeyEvent.VK_A:
      {
        dirs[1] = false;
        break;
      }
      case KeyEvent.VK_D:
      {
        dirs[2] = false;
        break;
      }
      case KeyEvent.VK_S:
      {
        dirs[3] = false;
        break;
      }
      case KeyEvent.VK_SHIFT:
      {
        if (sprint)
          time = System.currentTimeMillis();
        
        sprint = false;
        break;
      }
    }
  }
  
  @Override
  public Area getBumpArea(Map map)
  {
    return new Area(new Rectangle2D.Double(getRelativePos(map)[0] + bx, getRelativePos(map)[1] + by, bw, bh));
  }
  
  public void setRelativePos(double x, double y)
  {
    relPos = new Vector(x, y);
  }
  
  @Override
  public int[] getRelativePos(Map m)
  {
    return new int[] { (int) relPos.coords[0], (int) relPos.coords[1] };
  }
  
  public JSONObject getData()
  {
    try
    {
      data.getJSONObject("char").put("equip", equipment.serializeEquipment());
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return data;
  }
  
  public void setData(JSONObject data)
  {
    this.data = data;
  }
  
  @Override
  public void talkStarted(Talk t, Map m)
  {
    frozen = true;
  }
  
  @Override
  public void talkEnded(Talk t, Map m)
  {
    frozen = false;
  }
  
  public void setInventory(JSONArray o)
  {
    try
    {
      data.getJSONObject("char").put("inventory", o);
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  public JSONArray getInventory()
  {
    try
    {
      return data.getJSONObject("char").getJSONArray("inventory");
    }
    catch (JSONException e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
  @Override
  public Attributes getAttributes()
  {
    return getAttributes(false);
  }
  
  public Attributes getAttributes(boolean equip)
  {
    if (equip)
      return equipment.getAttributes().add(attr);
    
    return attr;
  }
}
