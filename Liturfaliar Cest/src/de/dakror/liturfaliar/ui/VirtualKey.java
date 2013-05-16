package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;

public class VirtualKey extends Component
{
  private int     c;
  private boolean active;
  
  public VirtualKey(int x, int y, int c)
  {
    super(x, y, 64, 64);
    this.c = c;
    this.active = false;
  }
  
  @Override
  public void update()
  {}
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    g.drawImage(Viewport.loadImage("system/key" + ((this.active) ? "Active" : "") + ".png"), getX(), getY(), getWidth(), getHeight(), v.w);
    Assistant.drawCenteredString(KeyEvent.getKeyText(this.c), getX(), getWidth(), getY() + getHeight() / 16 * 11, g, 35, (this.active) ? Color.decode("#6d3600") : Color.decode("#252323"));
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
  {
    if (e.getExtendedKeyCode() == this.c)
      this.active = true;
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {
    if (e.getExtendedKeyCode() == this.c)
      this.active = false;
  }
}
