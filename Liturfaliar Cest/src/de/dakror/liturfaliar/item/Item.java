package de.dakror.liturfaliar.item;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.ui.Component;
import de.dakror.liturfaliar.ui.ItemSlot;
import de.dakror.liturfaliar.ui.Tooltip;

public class Item extends Component
{
  public static final int SPACING = 4;
  
  int                     iconx, icony;
  
  Image                   icon;
  String                  name;
  Types                   type;
  String                  charPath;
  
  public Tooltip          tooltip;
  
  public Item(Types t, String path)
  {
    super(0, 0, ItemSlot.SIZE - SPACING * 2, ItemSlot.SIZE - SPACING * 2);
    type = t;
    charPath = path;
    iconx = 0;
    icony = 0;
    name = "";
    
    init();
  }
  
  public Item(Items i)
  {
    super(0, 0, ItemSlot.SIZE - SPACING * 2, ItemSlot.SIZE - SPACING * 2);
    
    type = i.getType();
    name = i.getName();
    iconx = i.getIconX();
    icony = i.getIconY();
    charPath = i.getCharPath();
    
    init();
  }
  
  public Item(JSONObject o)
  {
    super(0, 0, ItemSlot.SIZE - SPACING * 2, ItemSlot.SIZE - SPACING * 2);
    
    try
    {
      type = Types.valueOf(o.getString("type"));
      name = o.getString("name");
      iconx = o.getInt("iconx");
      icony = o.getInt("icony");
      charPath = o.getString("char");
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    
    init();
  }
  
  public void init()
  {
    icon = ((BufferedImage) Viewport.loadImage("system/icons.png")).getSubimage(iconx * 24, icony * 24, 24, 24).getScaledInstance(getWidth(), getHeight(), BufferedImage.SCALE_REPLICATE);
    
    tooltip = new Tooltip("<#999999;30;1>" + name + "[br]<#ffffff;17;1>Typ: <#4444ff;17;1>" + type.getName(), this);
    tooltip.follow = true;
  }
  
  public void drawSlot(int x, int y, Graphics2D g, Viewport v)
  {
    setX(x + SPACING);
    setY(y + SPACING);
    
    g.drawImage(icon, getX() + (getWidth() / 2 - icon.getWidth(null) / 2), getY() + (getHeight() / 2 - icon.getHeight(null) / 2), icon.getWidth(null), icon.getHeight(null), v.w);
    
    tooltip.draw(g, v);
  }
  
  @Override
  public void update()
  {}
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {}
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    tooltip.mouseMoved(e);
  }
  
  public JSONObject serializeItem()
  {
    JSONObject o = new JSONObject();
    try
    {
      o.put("type", type.name());
      o.put("name", name);
      o.put("iconx", iconx);
      o.put("icony", icony);
      o.put("char", charPath);
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return o;
  }
  
  public String getCharPath()
  {
    return charPath;
  }
}
