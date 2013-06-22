package de.dakror.liturfaliar.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;

public class Tooltip extends Component
{
  Component      parent;
  HTMLString[]   text;
  public String  rawText;
  public boolean follow;
  public boolean visible;
  public Point   offset  = new Point(0, 0);
  public String  tileset = "Tooltip";
  
  public Tooltip(String raw, Component p)
  {
    super(p.getX(), p.getY(), 1, 1);
    parent = p;
    visible = false;
    rawText = raw;
    text = HTMLString.decodeString(Database.filterString(raw));
  }
  
  public void mouseMoved(MouseEvent e)
  {
    if (parent.getArea().contains(e.getPoint()))
    {
      visible = true;
      if (follow)
      {
        setX(offset.x + e.getX());
        setY(offset.y + e.getY());
      }
    }
    else
    {
      visible = false;
    }
  }
  
  public void draw(Graphics2D g, Viewport v)
  {
    if (getWidth() < 2)
    {
      int mostwidth = 1;
      for (int i = 0; i < text.length; i++)
      {
        int width = getTotalLineWidth(i, g);
        if (width > mostwidth)
          mostwidth = width;
      }
      setWidth(mostwidth + 32);
    }
    
    int lx = getX();
    int ly = getY();
    
    if (lx + getWidth() > v.w.getWidth())
      lx -= (lx + getWidth()) - v.w.getWidth();
    
    if (ly + getHeight() > v.w.getHeight())
      ly -= (ly + getHeight()) - v.w.getHeight();
    
    Font oldFont = g.getFont();
    g.setFont(new Font("Times New Roman", g.getFont().getStyle(), g.getFont().getSize()));
    
    if (visible)
    {
      if (tileset != null)
      {
        setHeight(getHeightOfPreviousRows(text.length, g) + 32);
        Assistant.stretchTileset(Viewport.loadImage("tileset/" + tileset + ".png"), lx, ly, getWidth() + 16 - (getWidth() % 16), getHeight(), g, v.w);
      }
      else
      {
        setHeight(getHeightOfPreviousRows(text.length, g) + 10);
        Color oldColor = g.getColor();
        g.setColor(Colors.DGRAY);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g.fill(new RoundRectangle2D.Double(lx, ly, getWidth() + 16 - (getWidth() % 16), getHeight(), 8, 8));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        g.setColor(oldColor);
      }
      for (int i = 0; i < text.length; i++)
      {
        if (i > 0)
        {
          Assistant.drawString(text[i].string, lx + 16 + ((!text[i - 1].br) ? text[i - 1].getWidth(g) : 0), ly + getHeightOfPreviousRows(i + ((!text[i - 1].br) ? 0 : 1), g) + ((tileset != null) ? 8 : 0), g, text[i].c, g.getFont().deriveFont(text[i].style, (int) text[i].size));
        }
        else
        {
          Assistant.drawString(text[i].string, lx + 16, ly + getHeightOfPreviousRows(i + 1, g) + ((tileset != null) ? 8 : 0), g, text[i].c, g.getFont().deriveFont(text[i].style, (int) text[i].size));
        }
      }
    }
    text = HTMLString.decodeString(Database.filterString(rawText));
    
    g.setFont(oldFont);
  }
  
  private int getTotalLineWidth(int firstIndex, Graphics2D g)
  {
    int width = 0;
    for (int i = firstIndex; i < text.length; i++)
    {
      width += text[i].getWidth(g);
      if (text[i].br)
        return width;
    }
    return width;
  }
  
  private int getHeightOfPreviousRows(int index, Graphics2D g)
  {
    int height = 0;
    for (int i = 0; i < index; i++)
    {
      if (this.text[(i > 0) ? i - 1 : 0].br || index == 1)
        height += text[i].getHeight(g);
    }
    return height;
  }
  
  @Override
  public void update()
  {}
}
