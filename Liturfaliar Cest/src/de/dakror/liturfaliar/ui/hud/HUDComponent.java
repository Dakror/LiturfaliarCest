package de.dakror.liturfaliar.ui.hud;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;

public abstract class HUDComponent
{
  protected int     x, y, width, height, layer;
  protected boolean visible;
  
  public HUDComponent(int x, int y, int w, int h, int layer)
  {
    this.visible = false;
    this.x = x;
    this.y = y;
    this.width = w;
    this.height = h;
    this.layer = layer;
  }
  
  public abstract void update(Map m);
  
  public abstract void draw(Graphics2D g, Viewport v, Map m);
  
  public void mouseWheelMoved(MouseWheelEvent e, Map m)
  {}
  
  public void mouseDragged(MouseEvent e, Map m)
  {}
  
  public void mouseMoved(MouseEvent e, Map m)
  {}
  
  public void mouseClicked(MouseEvent e, Map m)
  {}
  
  public void mousePressed(MouseEvent e, Map m)
  {}
  
  public void mouseReleased(MouseEvent e, Map m)
  {}
  
  public void mouseEntered(MouseEvent e, Map m)
  {}
  
  public void mouseExited(MouseEvent e, Map m)
  {}
  
  public void keyTyped(KeyEvent e, Map m)
  {}
  
  public void keyPressed(KeyEvent e, Map m)
  {}
  
  public void keyReleased(KeyEvent e, Map m)
  {}
  
  public int getX()
  {
    return x;
  }
  
  public void setX(int x)
  {
    this.x = x;
  }
  
  public int getY()
  {
    return y;
  }
  
  public void setY(int y)
  {
    this.y = y;
  }
  
  public int getWidth()
  {
    return width;
  }
  
  public void setWidth(int width)
  {
    this.width = width;
  }
  
  public int getHeight()
  {
    return height;
  }
  
  public void setHeight(int height)
  {
    this.height = height;
  }
  
  public int getLayer()
  {
    return layer;
  }
  
  public void setLayer(int layer)
  {
    this.layer = layer;
  }
  
  public Area getArea()
  {
    return new Area(new Rectangle2D.Double(x, y, width, height));
  }
}
