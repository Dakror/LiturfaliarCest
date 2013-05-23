package de.dakror.liturfaliar.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLayeredPane;

import de.dakror.liturfaliar.CFG;
import de.dakror.liturfaliar.util.Assistant;

public class MapPanel extends JLayeredPane
{
  private static final long serialVersionUID = 1L;
  MapEditor                 me;
  
  public MapPanel(MapEditor me)
  {
    this.me = me;
  }
  
  @Override
  public void paint(Graphics g)
  {
    super.paint(g);
    if (me.rasterview)
    {
      int w = 0, h = 0;
      for (Component c : getComponents())
      {
        if (c instanceof TileButton)
        {
          if (c.getX() > w)
            w = c.getX();
          if (c.getY() > h)
            h = c.getY();
        }
      }
      w += CFG.FIELDSIZE;
      h += CFG.FIELDSIZE;
      for (int i = 0; i < h / (double) CFG.FIELDSIZE; i++)
      {
        Assistant.Rect(0, i * CFG.FIELDSIZE, w, 1, Color.cyan, null, (Graphics2D) g);
      }
      for (int i = 0; i < w / (double) CFG.FIELDSIZE; i++)
      {
        Assistant.Rect(i * CFG.FIELDSIZE, 0, 1, h, Color.cyan, null, (Graphics2D) g);
      }
    }
  }
}
