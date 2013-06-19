package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.dispatcher.ItemSlotEventDispatcher;
import de.dakror.liturfaliar.item.Categories;
import de.dakror.liturfaliar.item.Inventory;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.Types;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.util.Assistant;

public class ItemSlot extends Component
{
  public static final Font KEYFONT         = new Font("Arial", Font.BOLD, 18);
  public static final Font STACKFONT       = new Font("Arial", Font.BOLD, 22);
  public static final int  SIZE            = 55;
  
  private boolean          mouseKey;
  private boolean          hover;
  private boolean          onlyLabel;
  public boolean           showFilterImage = true;
  
  private int              hotKey;
  private int              ax, ay;
  
  private Item             item;
  public String            tileset         = "Wood.png";
  private String           keyString;
  private Categories       categoryFilter;
  private ArrayList<Types> typesFilter;
  private Inventory        inventory;
  
  public ItemSlot(int x, int y)
  {
    super(x, y, SIZE, SIZE);
    
    hover = false;
    
    typesFilter = new ArrayList<Types>();
  }
  
  public ItemSlot(ItemSlot other)
  {
    super(other.x, other.y, SIZE, SIZE);
    setItem(new Item(other.item));
    hotKey = other.hotKey;
    keyString = other.keyString;
    mouseKey = other.mouseKey;
    tileset = other.tileset;
    categoryFilter = other.categoryFilter;
    typesFilter = other.typesFilter;
    ax = other.ax;
    ay = other.ay;
    hover = other.hover;
    inventory = other.inventory;
    item.setItemSlot(this);
  }
  
  public void setCategoryFilter(Categories c)
  {
    categoryFilter = c;
  }
  
  public void addTypesToFilter(Types... types)
  {
    for (Types c : types)
    {
      typesFilter.add(c);
    }
  }
  
  @Override
  public void update()
  {}
  
  public void setHotKey(int key, boolean mouse)
  {
    if (key == -1)
    {
      keyString = null;
      return;
    }
    
    hotKey = key;
    keyString = (mouse) ? ((key == 1) ? "ML" : "MR") : KeyEvent.getKeyText(key);
    mouseKey = mouse;
  }
  
  public boolean hasHotKey()
  {
    return keyString != null;
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    draw(0, 0, g, v);
  }
  
  public void drawLightWeight(Graphics2D g, Viewport v)
  {
    if (item != null)
      item.draw(g, v);
    
    int ax = item.mouse.x - item.width / 2;
    int ay = item.mouse.y - item.height / 2;
    
    if (keyString != null)
    {
      int width = g.getFontMetrics(KEYFONT).getAscent();
      int height = width;
      if (g.getFontMetrics(KEYFONT).stringWidth(keyString) > width)
      {
        width = g.getFontMetrics().stringWidth(keyString);
      }
      
      Assistant.Shadow(new RoundRectangle2D.Double(ax, ay + SIZE - height, width, height, 5, 5), Colors.DGRAY, 0.8f, g);
      Assistant.drawHorizontallyCenteredString(keyString, ax, width, ay + SIZE - 2, g, KEYFONT.getSize() - 2, Color.white);
    }
    else if (item != null && item.getStack() > 1 && keyString == null)
    {
      int width = g.getFontMetrics(STACKFONT).getAscent();
      int height = width;
      if (g.getFontMetrics(STACKFONT).stringWidth(item.getStack() + "") > width)
      {
        width = g.getFontMetrics().stringWidth(item.getStack() + "");
      }
      Assistant.Shadow(new RoundRectangle2D.Double(ax + SIZE - width, ay + SIZE - height, width, height, 5, 5), Colors.DGRAY, 0.5f, g);
      Assistant.drawHorizontallyCenteredString(item.getStack() + "", ax + SIZE - width, width, ay + SIZE - 2, g, STACKFONT.getSize() - 2, Colors.GRAY);
    }
  }
  
  public void draw(int x1, int y1, Graphics2D g, Viewport v)
  {
    ax = this.x + x1;
    ay = this.y + y1;
    
    g.drawImage(Viewport.loadImage("tileset/" + tileset), ax, ay, SIZE, SIZE, null);
    
    if (categoryFilter != null && item == null && showFilterImage)
    {
      g.drawImage(Viewport.loadImage("system/" + categoryFilter.name().toLowerCase() + "ItemSlotFilter.png"), ax + 4, ay + 4, SIZE - 8, SIZE - 8, null);
    }
    
    if (item != null)
      item.draw(ax, ay, g, v);
    
    if (keyString != null)
    {
      int width = g.getFontMetrics(KEYFONT).getAscent();
      int height = width;
      if (g.getFontMetrics(KEYFONT).stringWidth(keyString) > width)
      {
        width = g.getFontMetrics().stringWidth(keyString);
      }
      
      Assistant.Shadow(new RoundRectangle2D.Double(ax, ay + SIZE - height, width, height, 5, 5), Colors.DGRAY, 0.8f, g);
      Assistant.drawHorizontallyCenteredString(keyString, ax, width, ay + SIZE - 2, g, KEYFONT.getSize() - 2, Color.WHITE);
    }
    
    if (item != null && item.getStack() > 1)
    {
      int width = g.getFontMetrics(STACKFONT).getAscent();
      int height = width;
      if (g.getFontMetrics(STACKFONT).stringWidth(item.getStack() + "") > width)
      {
        width = g.getFontMetrics().stringWidth(item.getStack() + "");
      }
      Assistant.Shadow(new RoundRectangle2D.Double(ax + SIZE - width, ay + SIZE - height, width, height, 5, 5), Colors.DGRAY, 0.5f, g);
      Assistant.drawHorizontallyCenteredString(item.getStack() + "", ax + SIZE - width, width, ay + SIZE - 2, g, STACKFONT.getSize() - 2, Colors.GRAY);
    }
    
    if (hover)
      Assistant.Shadow(new RoundRectangle2D.Double(ax, ay + SIZE - height, width, height, 5, 5), Color.WHITE, 0.2f, g);
  }
  
  public void drawTooltip(Graphics2D g, Viewport v)
  {
    if (item != null)
      item.tooltip.draw(g, v);
  }
  
  public Item getItem()
  {
    return item;
  }
  
  public void setItem(Item item)
  {
    this.item = item;
    
    if (this.item != null)
      this.item.setItemSlot(this);
  }
  
  public void addItem()
  {
    if (item.getStack() + 1 <= item.getType().getStackSize())
      item.setStack(item.getStack() + 1);
  }
  
  public void subItem()
  {
    item.setStack(item.getStack() - 1);
    
    if (item.getStack() > 0)
      return;
    
    item = null;
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    if (item != null)
      item.mouseMoved(e);
    
    if (hover && !new Area(new Rectangle2D.Double(ax, ay, width, height)).contains(e.getLocationOnScreen()))
      ItemSlotEventDispatcher.dispatchSlotExited(e, this);
    
    hover = new Area(new Rectangle2D.Double(ax, ay, width, height)).contains(e.getLocationOnScreen());
    
    if (hover)
      ItemSlotEventDispatcher.dispatchSlotHovered(e, this);
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    boolean clip = new Rectangle(ax, ay, getWidth(), getHeight()).contains(e.getLocationOnScreen());
    
    if (onlyLabel && clip)
    {
      if (e.getButton() == 1 && inventory != null && inventory.getPickedUpItemSlot() != null)
      {
        if ((categoryFilter != null && !inventory.getPickedUpItemSlot().getItem().getType().getCategory().equals(categoryFilter)) || (typesFilter.size() > 0 && inventory.getPickedUpItemSlot().getItem().getType().getCategory().equals(categoryFilter) && typesFilter.indexOf(inventory.getPickedUpItemSlot().getItem().getType()) == -1))
          return;
        
        setItem(new Item(inventory.getPickedUpItemSlot().getItem()));
        
        ItemSlotEventDispatcher.dispatchSlotReleased(e, this);
        ItemSlotEventDispatcher.dispatchSlotHovered(e, this);
        
      }
      else if (e.getButton() == 3)
      {
        setItem(null);
        
        ItemSlotEventDispatcher.dispatchSlotPressed(e, this);
        ItemSlotEventDispatcher.dispatchSlotHovered(e, this);
      }
    }
    else if (clip)
    {
      if (item != null && e.getButton() == 1 && clip && inventory != null)
      {
        if (inventory.getPickedUpItemSlot() == null)
        {
          item.mouse = e.getLocationOnScreen();
          
          ItemSlotEventDispatcher.dispatchSlotPressed(e, this);
          ItemSlotEventDispatcher.dispatchSlotHovered(e, this);
        }
        else if ((categoryFilter != null && !inventory.getPickedUpItemSlot().getItem().getType().getCategory().equals(categoryFilter)) || (typesFilter.size() > 0 && inventory.getPickedUpItemSlot().getItem().getType().getCategory().equals(categoryFilter) && typesFilter.indexOf(inventory.getPickedUpItemSlot().getItem().getType()) == -1))
          return;
        else
        {
          ItemSlot oldPickedUp = new ItemSlot(inventory.getPickedUpItemSlot());
          
          if (oldPickedUp.getItem().equals(item) && item.getStack() < item.getType().getStackSize())
          {
            int sum = item.getStack() + oldPickedUp.getItem().getStack();
            if (sum <= item.getType().getStackSize())
            {
              item.setStack(item.getStack() + oldPickedUp.getItem().getStack());
              inventory.setPickedUpItemSlot(null);
            }
            else
            {
              item.setStack(item.getType().getStackSize());
              inventory.getPickedUpItemSlot().getItem().setStack(sum - item.getStack());
            }
          }
          else
          {
            inventory.setPickedUpItemSlot(new ItemSlot(this));
            setItem(new Item(oldPickedUp.getItem()));
          }
          item.tooltip.visible = true;
          item.tooltip.setX(e.getXOnScreen() + item.tooltip.offset.x);
          item.tooltip.setY(e.getYOnScreen() + item.tooltip.offset.y);
          ItemSlotEventDispatcher.dispatchSlotReleased(e, this);
          ItemSlotEventDispatcher.dispatchSlotHovered(e, this);
          return;
          
        }
      }
      else if (e.getButton() == 1 && clip && inventory != null)
      {
        if (item == null)
        {
          ItemSlot slot = inventory.getPickedUpItemSlot();
          if (slot == null || (categoryFilter != null && !slot.getItem().getType().getCategory().equals(categoryFilter)) || (typesFilter.size() > 0 && slot.getItem().getType().getCategory().equals(categoryFilter) && typesFilter.indexOf(slot.getItem().getType()) == -1))
            return;
          
          if (categoryFilter != null && !slot.getItem().areRequirementsSatisfied(null))
            return;
          
          setItem(new Item(slot.getItem()));
          
          inventory.setPickedUpItemSlot(null);
          
          this.item.tooltip.visible = true;
          this.item.tooltip.setX(e.getXOnScreen());
          this.item.tooltip.setY(e.getYOnScreen());
          ItemSlotEventDispatcher.dispatchSlotReleased(e, this);
        }
      }
    }
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    if (isOnlyLabel())
      return;
    
    boolean clip = new Rectangle(ax, ay, getWidth(), getHeight()).contains(e.getLocationOnScreen());
    
    if (e.getButton() == 3 && item != null && inventory != null && inventory.getPickedUpItemSlot() == null && clip)
    {
      item.tooltip.visible = false;
      inventory.showContextMenu(this, e.getXOnScreen(), e.getYOnScreen());
    }
    else if (e.getButton() == 3 && clip && inventory != null)
    {
      ItemSlot slot = inventory.getPickedUpItemSlot();
      
      if (slot == null || slot.getItem().getStack() < 1)
        return;
      
      if (item != null && !item.equals(slot.getItem()))
        return;
      
      if (item != null && item.equals(slot.getItem()) && item.getStack() == item.getType().getStackSize())
        return;
      
      if (item == null)
      {
        setItem(new Item(slot.getItem()));
        getItem().setStack(1);
        slot.subItem();
      }
      else
      {
        addItem();
        slot.subItem();
      }
      
      if (slot.getItem() == null)
        inventory.setPickedUpItemSlot(null);
      
      ItemSlotEventDispatcher.dispatchSlotReleased(e, this);
      ItemSlotEventDispatcher.dispatchSlotHovered(e, this);
    }
  }
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    if (item != null && !mouseKey && e.getKeyCode() == hotKey)
      ;
  }
  
  public static ItemSlot[] createSlotRow(int x, int y, int length)
  {
    ItemSlot[] slots = new ItemSlot[length];
    for (int i = 0; i < length; i++)
    {
      slots[i] = new ItemSlot(x + i * ItemSlot.SIZE, y);
    }
    
    return slots;
  }
  
  public static ItemSlot[] createSlotGrid(int x, int y, int w, int h)
  {
    ItemSlot[] slots = new ItemSlot[w * h];
    for (int i = 0; i < h; i++)
    {
      for (int j = 0; j < w; j++)
      {
        slots[i * w + j] = new ItemSlot(x + j * ItemSlot.SIZE, y + i * ItemSlot.SIZE);
      }
    }
    
    return slots;
  }
  
  public static JSONObject serializeFakeItemSlot(Item item)
  {
    JSONObject o = new JSONObject();
    try
    {
      if (item != null)
      {
        o.put("item", item.serializeItem());
      }
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return o;
  }
  
  public JSONObject serializeItemSlot()
  {
    JSONObject o = new JSONObject();
    try
    {
      if (item != null)
      {
        o.put("item", item.serializeItem());
      }
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return o;
    
  }
  
  public static JSONArray serializeItemSlots(ItemSlot... slots)
  {
    JSONArray array = new JSONArray();
    
    for (ItemSlot is : slots)
    {
      array.put(is.serializeItemSlot());
    }
    
    return array;
  }
  
  public static void loadItemSlots(JSONArray data, ItemSlot... slots)
  {
    for (int i = 0; i < slots.length; i++)
    {
      try
      {
        JSONObject o = data.getJSONObject(i);
        if (o.length() == 0)
          continue;
        
        slots[i].setItem(new Item(o.getJSONObject("item")));
      }
      catch (JSONException e)
      {
        slots[i].setItem(null);
      }
    }
  }
  
  public Inventory getInventory()
  {
    return inventory;
  }
  
  public void setInventory(Inventory inventory)
  {
    this.inventory = inventory;
  }
  
  public ArrayList<Types> getTypesFilter()
  {
    return typesFilter;
  }
  
  public void setTypesFilter(ArrayList<Types> typesFilter)
  {
    this.typesFilter = typesFilter;
  }
  
  public Categories getCategoryFilter()
  {
    return categoryFilter;
  }
  
  public double getWeight()
  {
    if (item == null)
      return 0;
    
    return item.getWeight();
  }
  
  public boolean isOnlyLabel()
  {
    return onlyLabel;
  }
  
  public void setOnlyLabel(boolean onlyLabel)
  {
    this.onlyLabel = onlyLabel;
  }
}
