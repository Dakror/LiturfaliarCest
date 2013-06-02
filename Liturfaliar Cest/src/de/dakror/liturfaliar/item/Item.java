package de.dakror.liturfaliar.item;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.ui.Component;
import de.dakror.liturfaliar.ui.ItemSlot;
import de.dakror.liturfaliar.ui.Tooltip;
import de.dakror.liturfaliar.util.Assistant;

public class Item extends Component
{
  public static enum Types
  {
    SWORD("Schwert");
    
    private String name;
    
    private Types(String name)
    {
      this.name = name;
    }
    
    public String getName()
    {
      return name;
    }
  }
  
  public static final int SPACING = 6;
  
  Image                   icon;
  String                  name;
  Types                   type;
  
  Tooltip                 tooltip;
  
  Area                    area;
  
  public Item(Items i)
  {
    super(0, 0, ItemSlot.SIZE - SPACING * 2, ItemSlot.SIZE - SPACING * 2);
    
    type = i.getType();
    name = i.getName();
    icon = ((BufferedImage) Viewport.loadImage("system/icons.png")).getSubimage(i.getIconX() * 24, i.getIconY() * 24, 24, 24).getScaledInstance(getWidth(), getHeight(), BufferedImage.SCALE_SMOOTH);
    
    area = Assistant.toArea(icon);
    
    int x = (int) area.getBounds().x;
    int y = (int) area.getBounds().y;
    
    icon = Assistant.toBufferedImage(icon).getSubimage(x, y, width - x, height - y);
    
    tooltip = new Tooltip("<#999999;30;1>" + name + "[br]<#ffffff;17;1>Typ: <#4444ff;17;1>" + type.getName(), this);
    tooltip.follow = true;
  }
  
  public void drawSlot(int x, int y, Graphics2D g, Viewport v)
  {
    setX(x + SPACING);
    setY(y + SPACING);
    
    g.drawImage(icon, x + SPACING + getWidth() / 2 - icon.getWidth(null) / 2, y + SPACING + getHeight() / 2 - icon.getHeight(null) / 2, icon.getWidth(null), icon.getHeight(null), v.w);
    
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
}
