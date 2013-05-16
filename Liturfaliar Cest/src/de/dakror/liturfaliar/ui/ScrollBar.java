package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;

public class ScrollBar extends Component
{
  public static final boolean VERTICAL   = true;
  public static final boolean HORIZONTAL = false;
  int                         value;
  boolean                     dir;
  public boolean              focus;
  DragSelect                  drag;
  
  public ScrollBar(int x, int y, int width, int height, int init, boolean direction)
  {
    super(x, y, width, height);
    this.value = init;
    this.dir = direction;
    this.drag = new DragSelect(x, y, getWidth(), getHeight(), DragSelect.LEFT_BUTTON);
  }
  
  public float getValue()
  {
    return value / (float) (getHeight() - getWidth());
  }
  
  public void draw(Graphics2D g, Viewport v)
  {
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX(), getY(), getWidth(), getHeight(), g, v.w);
    if (this.dir == ScrollBar.VERTICAL)
    {
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX(), getY() + this.value, getWidth(), getWidth(), g, v.w);
    }
    else
    {
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX() + this.value, getY(), getHeight(), getHeight(), g, v.w);
    }
    this.drag.draw(g);
  }
  
  public void mouseDragged(MouseEvent e)
  {
    this.drag.mouseDragged(e);
    if (this.dir == ScrollBar.VERTICAL)
    {
      if (new Area(new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight())).contains(e.getLocationOnScreen()))
      {
        value = e.getYOnScreen() - getY();
        if (value < 0)
          value = 0;
        if (value > getHeight() - getWidth())
          value = getHeight() - getWidth();
      }
    }
    else
    {
      if (new Area(new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight())).contains(e.getLocationOnScreen()))
      {
        value = e.getXOnScreen() - getX();
        if (value < 0)
          value = 0;
        if (value > getWidth() - getHeight())
          value = getWidth() - getHeight();
      }
    }
  }
  
  public void mousePressed(MouseEvent e)
  {
    this.drag.mousePressed(e);
    if (this.dir == ScrollBar.VERTICAL)
    {
      if (new Area(new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight())).contains(e.getLocationOnScreen()) && !new Area(new Rectangle2D.Double(getX(), getY() + this.value, getWidth(), getWidth())).contains(e.getLocationOnScreen()))
      {
        value = e.getYOnScreen() - getY();
        if (value < 0)
          value = 0;
        if (value > getHeight() - getWidth())
          value = getHeight() - getWidth();
      }
    }
    else
    {
      if (new Area(new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight())).contains(e.getLocationOnScreen()) && !new Area(new Rectangle2D.Double(getX() + this.value, getY(), getHeight(), getHeight())).contains(e.getLocationOnScreen()))
      {
        value = e.getXOnScreen() - getX();
        if (value < 0)
          value = 0;
        if (value > getWidth() - getHeight())
          value = getWidth() - getHeight();
      }
    }
  }
  
  public void mouseWheelMoved(MouseWheelEvent e)
  {
    if (!focus)
      return;
    if (this.dir == ScrollBar.VERTICAL)
    {
      if (e.isControlDown())
        return;
      value += e.getWheelRotation() * 5;
      if (value < 0)
        value = 0;
      if (value > getHeight() - getWidth())
        value = getHeight() - getWidth();
    }
    else
    {
      if (!e.isControlDown())
        return;
      value += e.getWheelRotation() * getHeight() / 10;
      if (value < 0)
        value = 0;
      if (value > getWidth() - getHeight())
        value = getWidth() - getHeight();
    }
  }
  
  public void mouseMoved(MouseEvent e, Component parent)
  {
    if (getArea().contains(e.getLocationOnScreen()) || parent.getArea().contains(e.getLocationOnScreen()))
    {
      this.focus = true;
    }
    else
    {
      this.focus = false;
    }
  }
  
  @Override
  public void mouseClicked(MouseEvent e)
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
  
  @Override
  public void mouseMoved(MouseEvent e)
  {}
}
