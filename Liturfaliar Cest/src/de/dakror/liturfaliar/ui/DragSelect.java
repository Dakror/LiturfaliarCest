package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.util.Assistant;

public class DragSelect extends Component
{
  public Area             parent;
  public int              button;
  public static final int LEFT_BUTTON   = 16;
  public static final int MIDDLE_BUTTON = 8;
  public static final int RIGHT_BUTTON  = 4;
  
  public DragSelect(int x, int y, int w, int h, int button)
  {
    super(x, y, w, h);
    this.button = button;
    this.parent = new Area(new Rectangle2D.Double(x, y, w, h));
  }
  
  public Rectangle2D getSelection()
  {
    return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
  }
  
  public void draw(Graphics2D g)
  {
    int w1 = this.getX() + this.getWidth();
    int h1 = this.getY() + this.getHeight();
    int x = (this.getWidth() > 0) ? this.getX() : w1;
    int y = (this.getHeight() > 0) ? this.getY() : h1;
    if (CFG.UIDEBUG)
      Assistant.Rect(x, y, Math.abs(this.getWidth()), Math.abs(this.getHeight()), Color.white, null, g);
  }
  
  public void mousePressed(MouseEvent e)
  {
    if (e.getButton() == 1 && this.parent.contains(e.getLocationOnScreen()))
    {
      this.setWidth(0);
      this.setHeight(0);
      this.setX(e.getXOnScreen());
      this.setY(e.getYOnScreen());
    }
  }
  
  public void mouseDragged(MouseEvent e)
  {
    if (e.getModifiers() != this.button)
      return;
    if (this.parent.contains(new Point(e.getXOnScreen(), getY())))
      this.setWidth(e.getXOnScreen() - this.getX());
    if (this.parent.contains(new Point(getX(), e.getYOnScreen())))
      this.setHeight(e.getYOnScreen() - this.getY());
  }
  
  public int roundToGrid(int i, int cell)
  {
    return (int) i - (i % cell);
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {}
  
  @Override
  public void mouseMoved(MouseEvent e)
  {}
  
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
  public void draw(Graphics2D g, Viewport v)
  {}
}
