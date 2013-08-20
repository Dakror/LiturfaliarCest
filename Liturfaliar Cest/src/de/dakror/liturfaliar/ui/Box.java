package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

public class Box extends Component
{
  ArrayList<Component> components = new ArrayList<Component>();
  
  public Box(int x, int y, int w, int h)
  {
    super(x, y, w, h);
  }
  
  public void addComponent(Component... cm)
  {
    for (Component c : cm)
    {
      c.setX(getX() + c.getX());
      c.setY(getY() + c.getY());
      if (c.getWidth() > getWidth()) c.setWidth(getWidth());
      if (c.getHeight() > getHeight()) c.setHeight(getHeight());
      components.add(c);
    }
  }
  
  public void removeComponent(Component c)
  {
    components.set(components.indexOf(c), null);
  }
  
  public Component getComponent(int i)
  {
    return components.get(i);
  }
  
  @Override
  public void update()
  {
    for (Component c : components)
    {
      if (c != null) c.update();
    }
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    for (Component c : components)
    {
      if (c != null) c.draw(g);
    }
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {
    for (Component c : components)
    {
      if (c != null) c.mouseWheelMoved(e);
    }
  }
  
  @Override
  public void mouseDragged(MouseEvent e)
  {
    for (Component c : components)
    {
      if (c != null) c.mouseDragged(e);
    }
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    for (Component c : components)
    {
      if (c != null) c.mouseMoved(e);
    }
  }
  
  @Override
  public void mouseClicked(MouseEvent e)
  {
    for (Component c : components)
    {
      if (c != null) c.mouseClicked(e);
    }
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    for (Component c : components)
    {
      if (c != null) c.mousePressed(e);
    }
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    for (Component c : components)
    {
      if (c != null) c.mouseReleased(e);
    }
  }
  
  @Override
  public void mouseEntered(MouseEvent e)
  {
    for (Component c : components)
    {
      if (c != null) c.mouseEntered(e);
    }
  }
  
  @Override
  public void mouseExited(MouseEvent e)
  {
    for (Component c : components)
    {
      if (c != null) c.mouseExited(e);
    }
  }
  
  @Override
  public void keyTyped(KeyEvent e)
  {
    for (Component c : components)
    {
      if (c != null) c.keyTyped(e);
    }
  }
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    for (Component c : components)
    {
      if (c != null) c.keyPressed(e);
    }
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {
    for (Component c : components)
    {
      if (c != null) c.keyReleased(e);
    }
  }
}
