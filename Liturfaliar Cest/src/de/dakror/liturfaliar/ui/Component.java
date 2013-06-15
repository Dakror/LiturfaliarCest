package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.CFG;

public abstract class Component
{
  protected int x;
  protected int y;
  protected int width;
  protected int height;
  
  protected Component(int x, int y, int w, int h)
  {
    setX(x);
    setY(y);
    setWidth(w);
    setHeight(h);
  }
  
  public int getX()
  {
    return x;
  }
  
  public void setX(int x)
  {
    this.x = x;
  }
  
  public int getWidth()
  {
    return width;
  }
  
  public void setWidth(int width)
  {
    this.width = width;
  }
  
  public int getY()
  {
    return y;
  }
  
  public void setY(int y)
  {
    this.y = y;
  }
  
  public int getHeight()
  {
    return height;
  }
  
  public void setHeight(int height)
  {
    this.height = height;
  }
  
  public Area getArea()
  {
    return new Area(new Rectangle2D.Double(x, y, width, height));
  }
  
  public void HelpOverlayClicked(MouseEvent e, String desc)
  {
    if (CFG.HELPOVERLAYCREATE && getArea().contains(e.getLocationOnScreen()))
    {
      System.out.println("new HelpOverlayContainer(" + getX() + ", " + getY() + ", " + getWidth() + ", " + getHeight() + ", \"" + desc + "\")");
    }
  }
  
  public abstract void update();
  
  public abstract void draw(Graphics2D g, Viewport v);
  
  
  public void mouseWheelMoved(MouseWheelEvent e)
  {}
  
  
  public void mouseDragged(MouseEvent e)
  {}
  
  
  public void mouseMoved(MouseEvent e)
  {}
  
  
  public void mouseClicked(MouseEvent e)
  {}
  
  
  public void mousePressed(MouseEvent e)
  {}
  
  
  public void mouseReleased(MouseEvent e)
  {}
  
  
  public void mouseEntered(MouseEvent e)
  {}
  
  
  public void mouseExited(MouseEvent e)
  {}
  
  
  public void keyTyped(KeyEvent e)
  {}
  
  
  public void keyPressed(KeyEvent e)
  {}
  
  
  public void keyReleased(KeyEvent e)
  {}
}
