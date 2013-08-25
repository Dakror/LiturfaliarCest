package de.dakror.liturfaliar.item;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.util.Vector;

public class ItemDrop
{
  public static final Comparator<ItemDrop> COMPARATOR = new Comparator<ItemDrop>()
                                                      {
                                                        @Override
                                                        public int compare(ItemDrop o1, ItemDrop o2)
                                                        {
                                                          return o1.getZ() - o2.getZ();
                                                        }
                                                      };
  public static final int                  SIZE       = 24;
  
  Item                                     item;
  int                                      x;
  int                                      y;
  int                                      z;
  String                                   map;
  
  public ItemDrop(Item i, int x, int y, int z, String m)
  {
    item = i;
    item.setWidth(SIZE);
    item.setHeight(SIZE);
    item.showStackSize = true;
    item.init();
    
    this.x = x;
    this.y = y;
    this.z = z;
    map = m;
  }
  
  public JSONObject serializeItemDrop()
  {
    JSONObject o = new JSONObject();
    try
    {
      o.put("x", x);
      o.put("y", y);
      o.put("z", z);
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
  
  public int getZ()
  {
    return z;
  }
  
  public String getMap()
  {
    return map;
  }
  
  public boolean equals(ItemDrop o)
  {
    return o.getItem().equals(item) && o.x == x && o.y == y && o.z == z && o.map.equals(map);
  }
  
  public void setMap(String map)
  {
    this.map = map;
  }
  
  public void draw(Map m, Graphics2D g)
  {
    item.draw(m.getX() + x, m.getY() + y, g);
  }
  
  public void drawWithoutTooltip(Map m, Graphics2D g)
  {
    item.drawWithoutTooltip(m.getX() + x, m.getY() + y, g);
  }
  
  public Area getArea()
  {
    return new Area(new Rectangle2D.Double(x, y, SIZE, SIZE));
  }
  
  public void mouseMoved(MouseEvent e, Map m)
  {
    item.mouseMoved(e);
  }
  
  public void mousePressed(MouseEvent e, Map m)
  {
    if (m.getPlayer().getPos().getDistance(new Vector(x, y)) < CFG.FIELDSIZE * 2)
    {
      for (ItemDrop id : m.getItemDrops())
      {
        if (id.getArea().intersects(getArea().getBounds()) && id.getZ() > z) return;
      }
      m.getPlayer().resetTarget();
      m.getPlayer().putItemInFirstInventorySlot(item);
      m.removeItemDrop(this);
      Viewport.playSound("064-Swing03");
    }
  }
}
