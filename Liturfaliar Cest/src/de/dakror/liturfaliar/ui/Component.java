package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.util.Listener;

public abstract class Component implements Listener
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
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {}
  
  @Override
  public void mouseDragged(MouseEvent e)
  {}
  
  @Override
  public void mouseMoved(MouseEvent e)
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
}
