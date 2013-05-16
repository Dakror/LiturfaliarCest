package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import de.dakror.liturfaliar.util.Assistant;

public class CursorText
{
  public static HTMLString  cfg   = new HTMLString("", 22.0f, Color.decode("#d9d9d9"), Font.BOLD);
  private static HTMLString text;
  private static String     from;
  private static Point      mouse = new Point(0, 0);
  
  public static void setCursorText(HTMLString t, String f)
  {
    text = t;
    from = f;
  }
  
  public static void removeCursorText(HTMLString t)
  {
    if (text != null && text.equals(t))
    {
      text = null;
      from = null;
    }
  }
  
  public static void removeCursorText(String t)
  {
    if (text != null && from.equals(t))
    {
      text = null;
      from = null;
    }
  }
  
  public static void draw(Graphics2D g, Window w)
  {
    try
    {
      Assistant.Shadow(new RoundRectangle2D.Double(mouse.x + 16, mouse.y - 8, text.getWidth(g) + 16, text.getHeight(g), 8, 8), Color.black, 0.3f, g);
      text.drawString(mouse.x + 24, mouse.y + 16, g);
    }
    catch (NullPointerException e)
    {}
  }
  
  public static void mouseMoved(MouseEvent e)
  {
    mouse = e.getLocationOnScreen();
  }
}
