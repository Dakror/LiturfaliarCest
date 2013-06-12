package de.dakror.liturfaliar.item;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.Attribute;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.ui.Component;
import de.dakror.liturfaliar.ui.ItemSlot;
import de.dakror.liturfaliar.ui.Tooltip;
import de.dakror.liturfaliar.util.Database;

public class Item extends Component
{
  public static final int SPACING = 4;
  
  public Point            mouse   = new Point(0, 0);
  
  int                     iconx, icony;
  
  Image                   icon;
  String                  name;
  Types                   type;
  String                  charPath;
  
  public Tooltip          tooltip;
  
  private Attributes      attributes, requirements;
  
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
    attributes = i.getAttributes();
    requirements = i.getRequirement();
    
    init();
  }
  
  public Item(Item other)
  {
    super(other.x, other.y, ItemSlot.SIZE - SPACING * 2, ItemSlot.SIZE - SPACING * 2);
    
    type = other.getType();
    name = other.name;
    iconx = other.iconx;
    icony = other.icony;
    charPath = other.getCharPath();
    mouse = other.mouse;
    attributes = other.getAttributes();
    requirements = other.getRequirements();
    
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
      attributes = new Attributes(o.getJSONObject("attr"));
      requirements = new Attributes(o.getJSONObject("req"));
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
    
    if (attributes == null)
      attributes = new Attributes();
    
    if (requirements == null)
      requirements = new Attributes();
    
    updateTooltip();
    
  }
  
  public void updateTooltip()
  {
    String c = "<#ffffff;17;1>";
    String g = "<#cccccc;17;1>";
    
    String req = "";
    for (Attr attr : Attr.values())
    {
      if (requirements.getAttribute(attr).getValue() != 0)
        req += "<" + ((getAttributeFromDatabase(attr.name()) >= requirements.getAttribute(attr).getValue()) ? Colors.BETTER : Colors.WORSE) + ";17;1>" + Attribute.FORMAT.format(requirements.getAttribute(attr).getValue()) + " " + attr.getText() + "[br]";
    }
    
    String att = "";
    for (Attr attr : Attr.values())
    {
      if (attr.equals(Attr.weight))
        continue;
      if (attributes.getAttribute(attr).getValue() != 0)
        att += g + ((attributes.getAttribute(attr).getValue() < 0.0) ? "-" : "+") + Attribute.FORMAT.format(attributes.getAttribute(attr).getValue()) + " " + attr.getText() + "[br]";
    }
    
    String raw = "<#999999;30;1>" + name + "[br]<#6666ff;19;1>" + type.getName() + "[br]" + c + attributes.getAttribute(Attr.weight).getValue() + " kg[br]" + att + ((req.length() > 0) ? c + " [br]" + c + "Benötigt:[br]" + req : "");
    
    if (tooltip == null)
    {
      tooltip = new Tooltip(raw, this);
      tooltip.follow = true;
    }
    else tooltip.rawText = raw;
  }
  
  private int getAttributeFromDatabase(String key)
  {
    try
    {
      return Integer.parseInt(Database.filterString("%ov_inv_attr_" + key + "%"));
    }
    catch (Exception e)
    {
      return 0;
    }
  }
  
  public void draw(int x1, int y1, Graphics2D g, Viewport v)
  {
    setX(x1 + SPACING);
    setY(y1 + SPACING);
    g.drawImage(icon, getX() + (getWidth() / 2 - icon.getWidth(null) / 2), getY() + (getHeight() / 2 - icon.getHeight(null) / 2), icon.getWidth(null), icon.getHeight(null), v.w);
    tooltip.draw(g, v);
  }
  
  @Override
  public void update()
  {}
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    g.drawImage(icon, mouse.x - icon.getWidth(null) / 2, mouse.y - icon.getHeight(null) / 2, icon.getWidth(null), icon.getHeight(null), v.w);
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    mouse = e.getLocationOnScreen();
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
      o.put("attr", attributes.serializeAttributes());
      o.put("req", requirements.serializeAttributes());
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
  
  public Types getType()
  {
    return type;
  }
  
  public Attributes getAttributes()
  {
    return attributes;
  }
  
  public void setAttributes(Attributes attributes)
  {
    this.attributes = attributes;
  }
  
  public Attributes getRequirements()
  {
    return requirements;
  }
  
  public void setRequirements(Attributes requirement)
  {
    this.requirements = requirement;
  }
  
  public boolean areRequirementsSatisfied(Attributes attributes)
  {
    for (Attr attr : Attr.values())
    {
      if (requirements.getAttribute(attr).getValue() > 0)
        if (((attributes == null) ? getAttributeFromDatabase(attr.name()) : attributes.getAttribute(attr).getValue()) < requirements.getAttribute(attr).getValue())
          return false;
    }
    return true;
  }
}
