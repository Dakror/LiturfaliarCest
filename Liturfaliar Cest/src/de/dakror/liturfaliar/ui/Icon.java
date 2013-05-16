package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import de.dakror.liturfaliar.CFG;
import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;

public class Icon
{
  int x, y, width, height, dx, dy;
  final int ICONSET_COLS = 16, ICONSET_ROWS = 629;
  boolean   focus;
  
  public Icon(int x, int y, int width, int height, int row, int col)
  {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.dx = row;
    this.dy = col;
    focus = false;
  }
  
  public void keyPressed(KeyEvent e)
  {
    if (!focus)
      return;
    switch (e.getExtendedKeyCode())
    {
      case 0x25:
        if (dx > 0)
          dx--;
        break;
      case 0x27:
        if (dx < ICONSET_COLS)
          dx++;
        break;
      case 0x26:
        if (dy > 0)
          dy--;
        break;
      case 0x28:
        if (dy < ICONSET_ROWS)
          dy++;
        break;
    }
  }
  
  public void mouseReleased(MouseEvent e)
  {
    if (!CFG.UIDEBUG)
      return;
    if (e.getButton() == 1 && new Area(new Rectangle2D.Double(x, y, width, height)).contains(e.getLocationOnScreen()))
    {
      focus = true;
    }
    else
    {
      focus = false;
    }
  }
  
  public void draw(Graphics2D g, Window w)
  {
    g.drawImage(Viewport.loadImage("system/icons.png"), x, y, x + width, y + height, dx * 24, dy * 24, dx * 24 + 24, dy * 24 + 24, w);
    if (focus)
    {
      Assistant.Rect(x, y, width, height, Color.green, null, g);
      Assistant.drawString(dx + "," + dy, x, y, g, Color.white);
    }
  }
}
