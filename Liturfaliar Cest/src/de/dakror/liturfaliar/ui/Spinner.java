package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.util.Assistant;

public class Spinner extends Component
{
  public boolean disabled;
  public boolean showMax;
  public int     decDigits = 0;
  String         title;
  double         min;
  double         max;
  double         value;
  double         step;
  Button         up;
  Button         down;
  HandleArea     h;
  
  public Spinner(int x, int y, int w, String title, double init, double min, double max, double step)
  {
    super(x, y, w, (int) (22 * 1.425f));
    this.value = init;
    this.min = min;
    this.max = max;
    this.step = step;
    this.title = title;
    this.down = new Button(x, y, getHeight(), getHeight(), "sq_prev_icon");
    this.down.soundMOVER = false;
    this.down.clickmod = 0;
    this.down.hovermod = 0;
    this.up = new Button(x + w - getHeight(), y, getHeight(), getHeight(), "sq_next_icon");
    this.up.soundMOVER = false;
    this.up.clickmod = 0;
    this.up.hovermod = 0;
    this.h = new HandleArea(x, y, w, getHeight());
    h.soundMOVER = false;
    h.soundCLICK = false;
    this.showMax = false;
    this.disabled = false;
  }
  
  @Override
  public void update()
  {
    if (this.value <= this.min)
    {
      this.value = this.min;
      this.down.disabled = true;
    }
    else if (!this.disabled)
      this.down.disabled = false;
    if (this.value >= this.max)
    {
      this.value = this.max;
      this.up.disabled = true;
    }
    else if (!this.disabled)
      this.up.disabled = false;
    this.down.update();
    this.up.update();
    if (this.up.getState() == 1)
    {
      this.value += this.step;
      this.up.setState(0);
    }
    if (this.down.getState() == 1)
    {
      this.value -= this.step;
      this.down.setState(0);
    }
  }
  
  public double getValue()
  {
    return this.value;
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    this.h.update(v);
    Color c = g.getColor();
    if (this.h.state > 0 && !this.disabled)
      c = Colors.ORANGE;
    else c = Colors.DGRAY;
    Assistant.Shadow(new RoundRectangle2D.Double(getX(), getY(), getWidth(), getHeight(), 8, 8), c, 0.6f, g);
    DecimalFormat df = new DecimalFormat();
    df.setMinimumFractionDigits(this.decDigits);
    df.setMaximumFractionDigits(this.decDigits);
    Assistant.drawHorizontallyCenteredString(((this.title != null) ? title : "") + df.format(this.value), getX(), getWidth(), getY() + 22, g, 22, Color.white);
    this.down.draw(g, v);
    this.up.draw(g, v);
    if (this.disabled)
      Assistant.Shadow(new RoundRectangle2D.Double(getX(), getY(), getWidth(), getHeight(), 8, 8), c, 0.6f, g);
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {}
  
  @Override
  public void mouseDragged(MouseEvent e)
  {}
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    this.down.mouseMoved(e);
    this.up.mouseMoved(e);
    this.h.mouseMoved(e);
  }
  
  @Override
  public void mouseClicked(MouseEvent e)
  {}
  
  @Override
  public void mousePressed(MouseEvent e)
  {}
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    this.down.mouseReleased(e);
    this.up.mouseReleased(e);
    this.h.mouseReleased(e);
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
}
