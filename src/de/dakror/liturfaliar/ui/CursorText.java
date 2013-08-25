package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import de.dakror.liturfaliar.util.Assistant;

public class CursorText
{
  public static HTMLString  cfg   = new HTMLString("", 22.0f, Color.decode("#d9d9d9"), Font.BOLD);
  private static HTMLString text;
  private static String     from;
  private static String     parent;
  private static Point      mouse = new Point(0, 0);
  
  public static void setCursorText(HTMLString t, String f, String p)
  {
    text = t;
    from = f;
    parent = p;
  }
  
  public static void removeCursorText(HTMLString t)
  {
    if (text != null && text.equals(t))
    {
      text = null;
      from = null;
      parent = null;
    }
  }
  
  public static void removeCursorText(String t)
  {
    if (text != null && from.equals(t))
    {
      text = null;
      from = null;
      parent = null;
    }
  }
  
  public static void removeCursorTextByParent(String p)
  {
    if (text != null && parent.equals(p))
    {
      text = null;
      from = null;
      parent = null;
    }
  }
  
  public static void draw(Graphics2D g)
  {
    try
    {
      Assistant.Shadow(new RoundRectangle2D.Double(mouse.x + 14, mouse.y + 18, text.getWidth(g) + 16, text.getHeight(g), 8, 8), Color.black, 0.3f, g);
      text.drawString(mouse.x + 26, mouse.y + 40, g);
    }
    catch (NullPointerException e)
    {}
  }
  
  public static void mouseMoved(MouseEvent e)
  {
    mouse = e.getPoint();
  }
}
