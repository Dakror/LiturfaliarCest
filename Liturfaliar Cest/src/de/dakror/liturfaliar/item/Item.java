package de.dakror.liturfaliar.item;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.ui.Component;
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
  
  public static final int SPACING  = 6;
  public static final int SLOTSIZE = 55;
  
  Image                   icon;
  String                  name;
  Types                   type;
  
  int                     ix, iy;
  
  Tooltip                 tooltip;
  
  public Item(Items i)
  {
    super(0, 0, SLOTSIZE - SPACING * 2, SLOTSIZE - SPACING * 2);
    
    type = i.getType();
    name = i.getName();
    icon = ((BufferedImage) Viewport.loadImage("system/icons.png")).getSubimage(i.getIconX() * 24, i.getIconY() * 24, 24, 24).getScaledInstance(getWidth(), getHeight(), BufferedImage.SCALE_SMOOTH);
    
    Area area = Assistant.ImageToArea(icon);
    ix = (int) area.getBounds().getX();
    iy = (int) area.getBounds().getY();
    
    
    tooltip = new Tooltip("<#999999;30;1>" + name + "[br]<#ffffff;17;1>Typ: <#4444ff;17;1>" + type.getName(), this);
    tooltip.follow = true;
  }
  
  public void drawSlot(int x, int y, Graphics2D g, Viewport v)
  {
    setX(x + SPACING);
    setY(y + SPACING);
    
    g.drawImage(icon, x + SPACING - ix, y + SPACING - iy, SLOTSIZE - SPACING * 2, SLOTSIZE - SPACING * 2, v.w);
    
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
