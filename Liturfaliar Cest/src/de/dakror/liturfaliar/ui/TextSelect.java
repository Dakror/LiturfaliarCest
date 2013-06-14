package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;

public class TextSelect extends Component
{
  Area            clip;
  public Button[] elements;
  public boolean  soundMOVER = false;
  public boolean  soundCLICK = false;
  private int     pulledindex = -1, selected = -1, oldselected = -1;
  ScrollBar       v;
  
  public TextSelect(int x, int y, int w, int h, Object... elements)
  {
    super(x, y, w, h);
    this.elements = new Button[elements.length];
    int th = 28 * elements.length;
    if (th > h)
    {
      this.v = new ScrollBar(x + w - 48, y, 48, h, 0, ScrollBar.VERTICAL);
    }
    for (int i = 0; i < elements.length; i++)
    {
      Button b = new Button(x + 9, (int) (y + 8 + (i * 28)), w - 18 - ((this.v != null) ? 38 : 0), (String) elements[i], Color.white, 22);
      b.tileset = null;
      b.hovermod = 0;
      b.clickmod = 0;
      b.round = false;
      this.elements[i] = b;
    }
  }
  
  public void setSelected(String elem)
  {
    try
    {
      for (int i = 0; i < this.elements.length; i++)
      {
        if (this.elements[i].title.equals(elem))
        {
          setSelected(i);
          return;
        }
      }
    }
    catch (Exception e)
    {}
  }
  
  public void setSelected(int index)
  {
    try
    {
      this.setOldSelected(this.selected);
      if (this.selected != -1)
        this.elements[this.selected].setState(0);
      this.elements[index].setState(1);
      if (this.v != null)
      {
        this.v.value = (int) (index * 2.4);
      }
      this.selected = index;
    }
    catch (ArrayIndexOutOfBoundsException e)
    {}
  }
  
  public String getSelected(boolean reset)
  {
    if (this.selected != -1)
    {
      if (reset)
      {
        if (this.pulledindex == this.selected)
          return null;
        this.pulledindex = this.selected;
      }
      return this.elements[this.selected].title;
    }
    return null;
  }
  
  public int getSelectedIndex(boolean reset)
  {
    if (this.selected != -1)
    {
      if (reset)
      {
        if (this.pulledindex == this.selected)
          return -1;
        this.pulledindex = this.selected;
      }
      return this.selected;
    }
    return -1;
  }
  
  @Override
  public void update()
  {
    for (int i = 0; i < this.elements.length; i++)
    {
      Button b = this.elements[i];
      b.soundCLICK = this.soundCLICK;
      b.soundMOVER = this.soundMOVER;
      b.update();
      if (b.getState() == 1)
      {
        this.oldselected = this.selected;
        this.selected = i;
      }
    }
    if (this.selected != -1)
      this.elements[this.selected].setState(1);
    if (this.v != null)
    {
      int scrollY = (int) ((getFullHeight() - getHeight()) * this.v.getValue());
      for (int i = 0; i < this.elements.length; i++)
      {
        this.elements[i].setY(this.getY() + 8 + i * this.elements[i].getHeight() - scrollY);
      }
    }
  }
  
  private int getFullHeight()
  {
    int height = 16;
    for (Button b : this.elements)
    {
      height += b.getHeight();
    }
    return height;
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX(), getY(), getWidth(), getHeight(), g, v.w);
    g.setClip(new Area(new Rectangle2D.Double(getX() + 9, getY() + 8, getWidth() - 18, getHeight() - 16)));
    int sel = -1;
    for (int i = 0; i < this.elements.length; i++)
    {
      this.elements[i].draw(g, v);
      if (this.elements[i].getState() == 2)
        sel = i;
    }
    g.setClip(null);
    if (sel != -1 && this.elements[sel].tooltip != null)
      this.elements[sel].tooltip.draw(g, v);
    if (this.v != null)
      this.v.draw(g, v);
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {
    if (this.v != null)
      this.v.mouseWheelMoved(e);
  }
  
  @Override
  public void mouseDragged(MouseEvent e)
  {
    if (this.v != null)
      this.v.mouseDragged(e);
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    if (new Area(new Rectangle2D.Double(getX() + 9, getY() + 8, getWidth() - 18, getHeight() - 16)).contains(e.getLocationOnScreen()))
    {
      for (Button b : this.elements)
      {
        b.mouseMoved(e);
      }
    }
    if (this.v != null)
      this.v.mouseMoved(e, this);
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    if (this.v != null)
      this.v.mousePressed(e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    if (new Area(new Rectangle2D.Double(getX() + 9, getY() + 8, getWidth() - 18, getHeight() - 16)).contains(e.getLocationOnScreen()))
    {
      for (Button b : this.elements)
      {
        b.mouseReleased(e);
      }
    }
  }
  
  public int getOldSelected()
  {
    return oldselected;
  }
  
  public void setOldSelected(int oldselected)
  {
    this.oldselected = oldselected;
  }
}
