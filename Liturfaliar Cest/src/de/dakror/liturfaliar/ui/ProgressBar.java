package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;

public class ProgressBar extends Component
{
  public float  value;
  boolean       editable, disabled, showPercentage;
  public String title;
  public Image  filling;
  
  public ProgressBar(int x, int y, int width, float initProgress, boolean editable, String c, String title, boolean showPercentage)
  {
    super(x, y, width, 23);
    this.value = initProgress;
    this.title = title;
    this.showPercentage = showPercentage;
    this.editable = editable;
    this.filling = Assistant.loadImage("system/Bar-" + c + ".png");
  }
  
  public void setEnabled(boolean b)
  {
    this.disabled = b;
  }
  
  public void draw(Graphics2D g, Viewport v)
  {
    // draw base
    g.drawImage(Viewport.loadImage("system/BarBase.png"), this.getX(), this.getY(), this.getX() + 6, this.getY() + 23, 0, 0, 6, 23, v.w);
    g.drawImage(Viewport.loadImage("system/BarBase.png"), this.getX() + 6, this.getY(), this.getX() + this.getWidth() - 6, this.getY() + 23, 6, 0, 7, 23, v.w);
    g.drawImage(Viewport.loadImage("system/BarBase.png"), this.getX() + this.getWidth() - 6, this.getY(), this.getX() + this.getWidth(), this.getY() + 23, 7, 0, 13, 23, v.w);
    // draw filling
    if (this.value > 0.0f && !this.disabled)
    {
      g.drawImage(this.filling, this.getX(), this.getY(), this.getX() + 6, this.getY() + 23, 0, 0, 6, 23, v.w);
      g.drawImage(this.filling, this.getX() + 6, this.getY(), this.getX() + 6 + (int) ((this.getWidth() - 12) * this.value), this.getY() + 23, 6, 0, 7, 23, v.w);
      g.drawImage(this.filling, this.getX() + 6 + (int) ((this.getWidth() - 12) * this.value), this.getY(), this.getX() + 12 + (int) ((this.getWidth() - 12) * this.value), this.getY() + 23, 7, 0, 13, 23, v.w);
    }
    Font oldf = g.getFont();
    g.setFont(new Font("Arial", Font.BOLD, 14));
    if (!this.showPercentage && this.title != null)
      Assistant.drawHorizontallyCenteredString(Database.filterString(this.title), this.getX(), this.getWidth(), this.getY() + 16, g, 14, Color.black);
    else Assistant.drawHorizontallyCenteredString(((this.title != null) ? (Database.filterString(this.title) + ": ") : "") + (int) (this.value * 100) + "%", this.getX(), this.getWidth(), this.getY() + 16, g, 14, Color.black);
    g.setFont(oldf);
  }
  
  public void mouseDragged(MouseEvent e)
  {
    if (!this.editable)
      return;
    if (new Area(new Rectangle2D.Double(this.getX(), this.getY(), this.getWidth(), 23)).contains(e.getLocationOnScreen()))
    {
      this.value = (e.getXOnScreen() - this.getX()) / (float) this.getWidth();
    }
  }
  
  public void mouseReleased(MouseEvent e)
  {
    if (!this.editable)
      return;
    if (new Area(new Rectangle2D.Double(this.getX(), this.getY(), this.getWidth(), 23)).contains(e.getLocationOnScreen()))
    {
      this.value = (e.getXOnScreen() - this.getX()) / (float) this.getWidth();
    }
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
  public void mousePressed(MouseEvent e)
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
