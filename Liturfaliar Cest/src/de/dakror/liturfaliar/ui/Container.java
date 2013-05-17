package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.RoundRectangle2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;

public class Container extends Component
{
  public String tileset = "Wood";
  
  public Container(int x, int y, int w, int h)
  {
    super(x, y, w, h);
  }
  
  public Container(int x, int y, int w, int h, String tileset)
  {
    super(x, y, w, h);
    this.tileset = tileset;
  }
  
  @Override
  public void update()
  {}
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    if (this.tileset != null)
      Assistant.stretchTileset(Viewport.loadImage("tileset/" + this.tileset + ".png"), getX(), getY(), getWidth(), getHeight(), g, v.w);
    else Assistant.Shadow(new RoundRectangle2D.Double(getX(), getY(), getWidth(), getHeight(), 8, 8), Color.decode("#222222"), 0.6f, g);
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {}
  
  @Override
  public void mouseDragged(MouseEvent e)
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
  public void mouseReleased(MouseEvent e)
  {
    HelpOverlayClicked(e, "container");
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