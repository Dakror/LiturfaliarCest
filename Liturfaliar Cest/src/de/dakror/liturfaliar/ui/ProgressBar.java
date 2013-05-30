package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
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
  
  public ProgressBar(int x, int y, int width, float initProgress, boolean e, String c, String t, boolean s)
  {
    super(x, y, width, 23);
    value = initProgress;
    title = t;
    showPercentage = s;
    editable = e;
    filling = Assistant.loadImage("system/Bar-" + c + ".png");
  }
  
  public void setEnabled(boolean b)
  {
    disabled = b;
  }
  
  public void draw(Graphics2D g, Viewport v)
  {
    // draw base
    g.drawImage(Viewport.loadImage("system/BarBase.png"), getX(), getY(), getX() + 6, getY() + getHeight(), 0, 0, 6, 23, v.w);
    g.drawImage(Viewport.loadImage("system/BarBase.png"), getX() + 6, getY(), getX() + getWidth() - 6, getY() + getHeight(), 6, 0, 7, 23, v.w);
    g.drawImage(Viewport.loadImage("system/BarBase.png"), getX() + getWidth() - 6, getY(), getX() + getWidth(), getY() + getHeight(), 7, 0, 13, 23, v.w);
    // draw filling
    if (value > 0.0f && !disabled)
    {
      g.drawImage(filling, getX(), getY(), getX() + 6, getY() + getHeight(), 0, 0, 6, 23, v.w);
      g.drawImage(filling, getX() + 6, getY(), getX() + 6 + (int) ((getWidth() - 12) * value), getY() + getHeight(), 6, 0, 7, 23, v.w);
      g.drawImage(filling, getX() + 6 + (int) ((getWidth() - 12) * value), getY(), getX() + 12 + (int) ((getWidth() - 12) * value), getY() + getHeight(), 7, 0, 13, 23, v.w);
    }
    Font oldf = g.getFont();
    g.setFont(new Font("Arial", Font.BOLD, 14));
    if (!showPercentage && title != null)
      Assistant.drawHorizontallyCenteredString(Database.filterString(title), getX(), getWidth(), getY() + 16, g, 14, Color.black);
    else if (showPercentage)
      Assistant.drawHorizontallyCenteredString(((title != null) ? (Database.filterString(title) + ": ") : "") + (int) (value * 100) + "%", getX(), getWidth(), getY() + 16, g, 14, Color.black);
    g.setFont(oldf);
  }
  
  public void mouseDragged(MouseEvent e)
  {
    if (!editable)
      return;
    if (new Area(new Rectangle2D.Double(getX(), getY(), getWidth(), 23)).contains(e.getLocationOnScreen()))
    {
      value = (e.getXOnScreen() - getX()) / (float) getWidth();
    }
  }
  
  public void mouseReleased(MouseEvent e)
  {
    if (!editable)
      return;
    if (new Area(new Rectangle2D.Double(getX(), getY(), getWidth(), 23)).contains(e.getLocationOnScreen()))
    {
      value = (e.getXOnScreen() - getX()) / (float) getWidth();
    }
  }
  
  @Override
  public void update()
  {}
}
