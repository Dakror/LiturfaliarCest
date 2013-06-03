package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.listener.ItemSlotEventListener;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.Item.Categories;
import de.dakror.liturfaliar.item.Item.Types;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.util.Assistant;

public class ItemSlot extends Component implements ItemSlotEventListener
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
  
  public ItemSlot(int x, int y)
  {
    super(x, y, SIZE, SIZE);
    
    hover = false;
    
    typesFilter = new ArrayList<Types>();
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
    
    if (categoryFilter != null)
    {
      g.drawImage(Viewport.loadImage("system/" + categoryFilter.name().toLowerCase() + "ItemSlotFilter.png"), ax + 4, ay + 4, SIZE - 8, SIZE - 8, null);
    }
    
    if (item != null)
      item.drawSlot(ax, ay, g, v);
    
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
    
    hover = new Area(new Rectangle2D.Double(ax, ay, width, height)).contains(e.getLocationOnScreen());
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    if (item != null && mouseKey && e.getButton() == hotKey)
      ;
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
  
  @Override
  public void itemPressed(MouseEvent e, ItemSlot slot)
  {}
  
  @Override
  public void itemDragged(MouseEvent e, ItemSlot slot)
  {}
  
  @Override
  public void itemReleased(MouseEvent e, ItemSlot slot)
  {}
}
