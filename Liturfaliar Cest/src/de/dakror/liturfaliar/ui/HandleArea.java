package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.CFG;

public class HandleArea extends Component
{
  public int     state           = 0;
  public boolean soundMOVER      = true;
  public boolean soundCLICK      = true;
  public boolean allowRightClick = false;
  public boolean mouseReleased   = true;
  Viewport       v;
  
  public HandleArea(int x, int y, int w, int h)
  {
    super(x, y, w, h);
  }
  
  public void update(Viewport v)
  {
    this.v = v;
  }
  
  public void draw(Graphics2D g, Viewport v)
  {
    if (CFG.UIDEBUG)
    {
      Color old = g.getColor();
      g.setColor(Color.green);
      g.draw(getArea());
      g.setColor(old);
    }
  }
  
  public void mouseMoved(MouseEvent e)
  {
    if (this.v == null || this.state == 1)
      return;
    if (getArea().contains(e.getLocationOnScreen()))
    {
      if (this.soundMOVER && this.state == 0)
        this.v.playSound("181-Hover");
      this.state = 2;
    }
    else if (this.state == 2)
      this.state = 0;
  }
  
  public void mouseReleased(MouseEvent e)
  {
    if ((e.getButton() != 1 && !this.allowRightClick) || this.v == null || !this.mouseReleased)
      return;
    if (getArea().contains(e.getLocationOnScreen()))
    {
      if (this.soundCLICK)
        this.v.playSound("182-Click");
      this.state = 1;
    }
    else this.state = 0;
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
  {
    if ((e.getButton() != 1 && !this.allowRightClick) || this.v == null || this.mouseReleased)
      return;
    if (getArea().contains(e.getLocationOnScreen()))
    {
      if (this.soundCLICK)
        this.v.playSound("182-Click");
      this.state = 1;
    }
    else this.state = 0;
  }
  
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
