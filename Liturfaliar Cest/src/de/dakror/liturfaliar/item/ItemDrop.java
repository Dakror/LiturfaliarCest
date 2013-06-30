package de.dakror.liturfaliar.item;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.settings.CFG;

public class ItemDrop
{
  Item   item;
  int    x;
  int    y;
  String map;
  
  public ItemDrop(Item i, int x, int y, String m)
  {
    item = i;
    item.setWidth(24);
    item.setHeight(24);
    item.setStack(1);
    item.init();
    
    this.x = x;
    this.y = y;
    map = m;
  }
  
  public JSONObject serializeItemDrop()
  {
    JSONObject o = new JSONObject();
    try
    {
      o.put("x", x);
      o.put("y", y);
      o.put("item", item.serializeItem());
      o.put("map", map);
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return o;
  }
  
  public Item getItem()
  {
    return item;
  }
  
  public void setItem(Item item)
  {
    this.item = item;
  }
  
  public int getX()
  {
    return x;
  }
  
  public void setX(int x)
  {
    this.x = x;
  }
  
  public int getY()
  {
    return y;
  }
  
  public void setY(int y)
  {
    this.y = y;
  }
  
  public String getMap()
  {
    return map;
  }
  
  public boolean equals(ItemDrop o)
  {
    return o.getItem().equals(item) && o.x == x && o.y == y && o.map.equals(map);
  }
  
  public void setMap(String map)
  {
    this.map = map;
  }
  
  public void draw(Map m, Graphics2D g, Viewport v)
  {
    item.draw(m.getX() + x, m.getY() + y, g, v);
  }
  
  public void drawWithoutTooltip(Map m, Graphics2D g, Viewport v)
  {
    item.drawWithoutTooltip(m.getX() + x, m.getY() + y, g, v);
  }
  
  
  public Area getArea(Map m)
  {
    return new Area(new Rectangle2D.Double(m.getX() + x, m.getY() + y, 24, 24));
  }
  
  public void mouseMoved(MouseEvent e, Map m)
  {
    item.mouseMoved(e);
  }
  
  public void mousePressed(MouseEvent e, Map m, Viewport v)
  {
    int[] p = m.getPlayer().getRelativePos();
    if (new Point(p[0], p[1]).distance(x, y) < CFG.FIELDSIZE * 2)
    {
      m.getPlayer().putItemInFirstInventorySlot(item);
      m.removeItemDrop(this);
      v.playSound("064-Swing03");
    }
  }
}
