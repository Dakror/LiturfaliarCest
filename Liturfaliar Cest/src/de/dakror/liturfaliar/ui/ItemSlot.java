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
  public static final int  SIZE    = 55;
  
  private Item             item;
  private int              hotKey;
  private String           keyString;
  private boolean          mouseKey;
  public String            tileset = "Wood.png";
  
  private Categories       categoryFilter;
  private ArrayList<Types> typesFilter;
  
  private int              ax, ay;
  
  private boolean          hover;
  
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
    item = new Item(other.item);
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
    hotKey = key;
    keyString = (mouse) ? ((key == 1) ? "ML" : "MR") : KeyEvent.getKeyText(key);
    mouseKey = mouse;
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    draw(0, 0, g, v);
  }
  
  public void draw(int x1, int y1, Graphics2D g, Viewport v)
  {
    ax = this.x + x1;
    ay = this.y + y1;
    
    g.drawImage(Viewport.loadImage("tileset/" + tileset), ax, ay, SIZE, SIZE, null);
    
    if (categoryFilter != null && item == null)
    {
      g.drawImage(Viewport.loadImage("system/" + categoryFilter.name().toLowerCase() + "ItemSlotFilter.png"), ax + 4, ay + 4, SIZE - 8, SIZE - 8, null);
    }
    
    if (item != null)
      item.draw(ax, ay, g, v);
    
    if (keyString != null)
    {
      Font font = new Font("Arial", Font.BOLD, 18);
      
      int width = g.getFontMetrics(font).getAscent();
      int height = width;
      if (g.getFontMetrics(font).stringWidth(keyString) > width)
      {
        width = g.getFontMetrics().stringWidth(keyString);
      }
      
      Assistant.Shadow(new RoundRectangle2D.Double(ax, ay + SIZE - height, width, height, 5, 5), Colors.DGRAY, 0.8f, g);
      Assistant.drawHorizontallyCenteredString(keyString, ax, width, ay + SIZE - 2, g, font.getSize() - 2, Colors.GRAY);
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
    if (keyString != null)
    {
      if (item != null && mouseKey && e.getButton() == hotKey)
      {}
    }
    else if (item != null && e.getButton() == 1 && new Rectangle(ax, ay, getWidth(), getHeight()).contains(e.getLocationOnScreen()) && inventory != null)
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
        Item oldPickedUp = new Item(inventory.getPickedUpItemSlot().getItem());
        
        inventory.setPickedUpItemSlot(new ItemSlot(this));
        
        item = oldPickedUp;
        
        item.tooltip.visible = true;
        item.tooltip.setX(e.getXOnScreen());
        item.tooltip.setY(e.getYOnScreen());
        ItemSlotEventDispatcher.dispatchSlotReleased(e, this);
        ItemSlotEventDispatcher.dispatchSlotHovered(e, this);
        return;
      }
    }
    else if (e.getButton() == 1 && new Rectangle(ax, ay, getWidth(), getHeight()).contains(e.getLocationOnScreen()) && inventory != null)
    {
      if (item == null)
      {
        ItemSlot slot = inventory.getPickedUpItemSlot();
        if (slot == null || (categoryFilter != null && !slot.getItem().getType().getCategory().equals(categoryFilter)) || (typesFilter.size() > 0 && slot.getItem().getType().getCategory().equals(categoryFilter) && typesFilter.indexOf(slot.getItem().getType()) == -1))
          return;
        
        this.item = slot.getItem();
        
        inventory.setPickedUpItemSlot(null);
        
        this.item.tooltip.visible = true;
        this.item.tooltip.setX(e.getXOnScreen());
        this.item.tooltip.setY(e.getYOnScreen());
        ItemSlotEventDispatcher.dispatchSlotReleased(e, this);
      }
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
    for (int i = 0; i < w; i++)
    {
      for (int j = 0; j < h; j++)
      {
        slots[i * h + j] = new ItemSlot(x + i * ItemSlot.SIZE, y + j * ItemSlot.SIZE);
      }
    }
    
    return slots;
  }
  
  public JSONObject serializeItemSlot()
  {
    return (item == null) ? new JSONObject() : item.serializeItem();
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
        slots[i].item = (data.getJSONObject(i).length() > 0) ? new Item(data.getJSONObject(i)) : null;
      }
      catch (JSONException e)
      {
        slots[i].item = null;
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
}
