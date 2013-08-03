package de.dakror.liturfaliar.DEPRECATEDeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import javax.swing.JLayeredPane;

import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.util.Assistant;

@Deprecated
public class MapPanel extends JLayeredPane
{
  private static final long serialVersionUID = 1L;
  
  public Point              mouseDown;
  public Point              mousePos;
  
  public boolean            delSelection;
  
  public int                selX;
  public int                selY;
  public int                selW;
  public int                selH;
  
  MapEditor                 me;
  
  public MapPanel(MapEditor m)
  {
    me = m;
    
    addMouseListener(m.new SelectionListener(null));
    addMouseMotionListener(m.new SelectionListener(null));
  }
  
  @Override
  public void paint(Graphics graphics)
  {
    try
    {
      super.paint(graphics);
      
      Graphics2D g = (Graphics2D) graphics;
      
      
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
          Assistant.Rect(0, i * CFG.FIELDSIZE, w, 1, Color.cyan, null, g);
        }
        for (int i = 0; i < w / (double) CFG.FIELDSIZE; i++)
        {
          Assistant.Rect(i * CFG.FIELDSIZE, 0, 1, h, Color.cyan, null, g);
        }
      }
      
      if (mouseDown != null && mousePos != null)
      {
        selX = (mouseDown.x < mousePos.x) ? mouseDown.x : mousePos.x;
        selY = (mouseDown.y < mousePos.y) ? mouseDown.y : mousePos.y;
        
        selW = (mouseDown.x < mousePos.x) ? mousePos.x - mouseDown.x : mouseDown.x - mousePos.x;
        selH = (mouseDown.y < mousePos.y) ? mousePos.y - mouseDown.y : mouseDown.y - mousePos.y;
        
        Assistant.Shadow(new Rectangle2D.Double(selX, selY, selW, selH), Color.decode((delSelection) ? "#ff3600" : "#0086ff"), 0.6f, g);
        
        if (selW > CFG.FIELDSIZE)
        {
          Assistant.drawHorizontallyCenteredString((selW / CFG.FIELDSIZE) + "", selX, selW, selY + 20, g, 20, Color.white);
        }
        if (selH > CFG.FIELDSIZE)
        {
          Assistant.drawVerticallyCenteredString((selH / CFG.FIELDSIZE) + "", selX + 20, selY + 10, selH, g, 270, 20, Color.white);
        }
      }
    }
    catch (Exception e)
    {}
  }
}
