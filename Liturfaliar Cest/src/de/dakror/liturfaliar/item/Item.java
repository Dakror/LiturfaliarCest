package de.dakror.liturfaliar.item;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.action.EmptyAction;
import de.dakror.liturfaliar.item.action.ItemAction;
import de.dakror.liturfaliar.item.action.PotionAction;
import de.dakror.liturfaliar.item.action.SkillAction;
import de.dakror.liturfaliar.map.Map;
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
  public static final int SPACING        = 4;
  
  public Point            mouse          = new Point(0, 0);
  
  int                     stack          = 0;
  
  int                     iconx, icony;
  
  Image                   icon;
  String                  name;
  Types                   type;
  String                  charPath;
  
  public Tooltip          tooltip;
  
  private Attributes      attributes, requirements;
  
  ItemAction              action;
  
  ItemSlot                itemSlot;
  
  public boolean          showSkillCosts = false;
  
  public Item(Types t, String path, int s)
  {
    super(0, 0, ItemSlot.SIZE - SPACING * 2, ItemSlot.SIZE - SPACING * 2);
    type = t;
    charPath = path;
    iconx = 0;
    icony = 0;
    name = "";
    stack = s;
    init();
  }
  
  public Item(Items i, int s)
  {
    super(0, 0, ItemSlot.SIZE - SPACING * 2, ItemSlot.SIZE - SPACING * 2);
    
    type = i.getType();
    name = i.getName();
    iconx = i.getIconX();
    icony = i.getIconY();
    charPath = i.getCharPath();
    attributes = i.getAttributes();
    requirements = i.getRequirement();
    action = i.getItemAction();
    stack = s;
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
    action = other.getAction();
    stack = other.stack;
    showSkillCosts = other.showSkillCosts;
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
      action = ItemAction.load(o.getJSONObject("action"));
      stack = o.getInt("stack");
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
    
    if (action == null)
      action = new EmptyAction();
    
    updateTooltip();
    
  }
  
  public void updateTooltip()
  {
    String c = "<#ffffff;17;1>";
    String g = "<#cccccc;17;1>";
    String b = "<#cc7777;17;1>";
    
    String req = "";
    for (Attr attr : Attr.values())
    {
      if (requirements.getAttribute(attr).getValue() != 0)
        req += "<" + ((getAttributeFromDatabase(attr.name()) >= requirements.getAttribute(attr).getValue()) ? Colors.BETTER : Colors.WORSE) + ";17;1>" + Attribute.FORMAT.format(requirements.getAttribute(attr).getValue()) + " " + attr.getText() + "[br]";
    }
    
    String att = "";
    for (Attr attr : Attr.values())
    {
      if (attr.equals(Attr.weight) || attr.equals(Attr.cooldown))
        continue;
      if (attributes.getAttribute(attr).getValue() != 0)
        att += g + ((attributes.getAttribute(attr).getValue() < 0.0) ? "" : "+") + Attribute.FORMAT.format(attributes.getAttribute(attr).getValue()) + " " + attr.getText() + "[br]";
    }
    
    String name = "<#999999;30;1>" + this.name;
    String category = "<#6666ff;19;1>" + type.getName();
    String weight = c + Attribute.FORMAT.format(attributes.getAttribute(Attr.weight).getValue() * stack) + " kg";
    String cooldown = ((!attributes.getAttribute(Attr.cooldown).isEmpty()) ? b + Attribute.FORMAT.format(attributes.getAttribute(Attr.cooldown).getValue()) + "s " + Attr.cooldown.getText() : "");
    String required = ((req.length() > 0) ? c + " [br]" + c + "Benötigt:[br]" + req : "");
    String action = "";
    
    if (type.equals(Types.POTION) && this.action instanceof PotionAction)
    {
      action = "[br]";
      for (Attr attr : Attr.values())
      {
        PotionAction potion = (PotionAction) this.action;
        if (potion.getChanges().getAttribute(attr).getValue() != 0)
          action += g + ((potion.getChanges().getAttribute(attr).getValue() < 0.0) ? "" : "+") + Attribute.FORMAT.format(potion.getChanges().getAttribute(attr).getValue()) + " " + attr.getText() + "[br]";
      }
    }
    
    String raw = name + "[br]" + category + "[br]" + weight + "[br]" + att + required + action + cooldown;
    
    if (type.getCategory().equals(Categories.SKILL))
    {
      
      SkillAction sa = (SkillAction) this.action;
      int sp = 0;
      int lvl = 0;
      try
      {
        sp = Integer.parseInt(Database.getStringVar("player_sp"));
        lvl = Integer.parseInt(Database.getStringVar("player_level"));
      }
      catch (Exception e)
      {}
      
      String skillpoints = "<" + ((sp >= requirements.getAttribute(Attr.skillpoint).getValue()) ? "#cccccc" : Colors.WORSE) + ";17;1>" + (int) requirements.getAttribute(Attr.skillpoint).getValue() + " " + Attr.skillpoint.getText();
      
      String level = c + " [br]";
      if (!requirements.getAttribute(Attr.level).isEmpty())
        level += "<" + ((lvl >= requirements.getAttribute(Attr.level).getValue()) ? "#cccccc" : Colors.WORSE) + ";17;1>" + Attr.level.getText() + ": " + (int) requirements.getAttribute(Attr.level).getValue() + "[br]";
      
      if (!showSkillCosts)
      {
        level = "";
        skillpoints = "";
      }
      
      raw = name + "[br]" + sa.getDescription() + "[br]" + level + skillpoints;
    }
    
    if (tooltip == null)
    {
      tooltip = new Tooltip(raw, this);
      tooltip.follow = true;
      tooltip.offset = new Point(16, 16);
    }
    else tooltip.rawText = raw;
  }
  
  private int getAttributeFromDatabase(String key)
  {
    try
    {
      return Integer.parseInt(Database.getStringVar("%ov_inv_attr_" + key + "%"));
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
    
    if (tooltip != null)
      tooltip.draw(g, v);
  }
  
  public void drawWithoutTooltip(int x1, int y1, Graphics2D g, Viewport v)
  {
    setX(x1 + SPACING);
    setY(y1 + SPACING);
    g.drawImage(icon, getX() + (getWidth() / 2 - icon.getWidth(null) / 2), getY() + (getHeight() / 2 - icon.getHeight(null) / 2), icon.getWidth(null), icon.getHeight(null), v.w);
  }
  
  @Override
  public void update()
  {}
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    g.drawImage(icon, mouse.x - width / 2, mouse.y - height / 2, width, height, v.w);
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    mouse = e.getPoint();
    if (tooltip != null)
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
      o.put("action", action.serializeItemAction());
      o.put("stack", stack);
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
  
  public ItemAction getAction()
  {
    return action;
  }
  
  public int getStack()
  {
    return stack;
  }
  
  public void setStack(int stack)
  {
    this.stack = (stack <= type.getStackSize()) ? stack : type.getStackSize();
    
    updateTooltip();
  }
  
  public void setAction(ItemAction action)
  {
    this.action = action;
  }
  
  public ItemSlot getItemSlot()
  {
    return itemSlot;
  }
  
  public void setItemSlot(ItemSlot itemSlot)
  {
    this.itemSlot = itemSlot;
  }
  
  public double getWeight()
  {
    return attributes.getAttribute(Attr.weight).getValue() * ((stack > 0) ? stack : 1);
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    this.name = name;
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
  
  public String toString()
  {
    return serializeItem().toString();
  }
  
  public void triggerAction(Map m, Viewport v)
  {
    action.actionTriggered(this, m, v);
  }
  
  public boolean equals(Item o)
  {
    return iconx == o.iconx && icony == o.icony && name.equals(o.name) && type.equals(o.type) && charPath.equals(o.charPath) && action.equals(o.action) && attributes.equals(o.attributes) && requirements.equals(o.requirements);
  }
  
  public Point getIconPoint()
  {
    return new Point(iconx, icony);
  }
}
