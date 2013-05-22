package de.dakror.liturfaliar.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.RoundRectangle2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;

public class Tooltip extends Component
{
  Component      parent;
  HTMLString[]   text;
  String         rawText;
  public boolean follow;
  public boolean visible;
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
    if (parent.getArea().contains(e.getLocationOnScreen()))
    {
      visible = true;
      if (follow)
      {
        setX(e.getXOnScreen());
        setY(e.getYOnScreen());
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
    
    if (visible)
    {
      if (tileset != null)
      {
        setHeight(getHeightOfPreviousRows(text.length, g) + 32);
        Assistant.stretchTileset(Viewport.loadImage("tileset/" + tileset + ".png"), getX(), getY(), getWidth() + 16 - (getWidth() % 16), getHeight(), g, v.w);
      }
      else
      {
        setHeight(getHeightOfPreviousRows(text.length, g) + 10);
        Color oldColor = g.getColor();
        g.setColor(Color.decode("#222222"));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g.fill(new RoundRectangle2D.Double(getX(), getY(), getWidth() + 16 - (getWidth() % 16), getHeight(), 8, 8));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        g.setColor(oldColor);
      }
      for (int i = 0; i < text.length; i++)
      {
        if (i > 0)
        {
          Assistant.drawString(text[i].string, getX() + 16 + ((!text[i - 1].br) ? text[i - 1].getWidth(g) : 0), getY() + getHeightOfPreviousRows(i + ((!text[i - 1].br) ? 0 : 1), g), g, text[i].c, g.getFont().deriveFont(text[i].style, (int) text[i].size));
        }
        else
        {
          Assistant.drawString(text[i].string, getX() + 16, getY() + getHeightOfPreviousRows(i + 1, g), g, text[i].c, g.getFont().deriveFont(text[i].style, (int) text[i].size));
        }
      }
    }
    text = HTMLString.decodeString(Database.filterString(rawText));
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
      // if (this.text[(i > 0) ? i - 1 : 0].br)
      height += text[i].getHeight(g);
    }
    
    return height;
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {}
  
  @Override
  public void mouseDragged(MouseEvent e)
  {}
  
  @Override
  public void mouseClicked(MouseEvent e)
  {}
  
  @Override
  public void mousePressed(MouseEvent e)
  {}
  
  @Override
  public void mouseReleased(MouseEvent e)
  {}
  
  @Override
  public void mouseEntered(MouseEvent e)
  {}
  
  @Override
  public void mouseExited(MouseEvent e)
  {}
  
  @Override
  public void keyTyped(KeyEvent e)
  {}
  
  @Override
  public void keyPressed(KeyEvent e)
  {}
  
  @Override
  public void keyReleased(KeyEvent e)
  {}
  
  @Override
  public void update()
  {}
}
