package de.dakror.liturfaliar.ovscenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;

public class OVScene_Info extends OVScene
{
  long     frames;
  long     time;
  Viewport v;
  
  @Override
  public void init(Viewport v)
  {
    this.v = v;
    time = System.currentTimeMillis();
    frames = 0;
    consistent = true;
  }
  
  @Override
  public void update(long timePassed)
  {}
  
  @Override
  public void draw(Graphics2D g)
  {
    frames++;
    // show fps
    Assistant.drawString(Math.round(frames / (float) ((System.currentTimeMillis() - time) / 1000.0f)) + " FPS", 0, 30, g, Color.white, g.getFont().deriveFont(30.0f));
  }
  
  @Override
  public void keyPressed(KeyEvent e)
  {}
  
  @Override
  public void keyReleased(KeyEvent e)
  {}
  
  @Override
  public void keyTyped(KeyEvent e)
  {}
  
  @Override
  public void mouseClicked(MouseEvent e)
  {}
  
  @Override
  public void mouseEntered(MouseEvent e)
  {}
  
  @Override
  public void mouseExited(MouseEvent e)
  {}
  
  @Override
  public void mousePressed(MouseEvent e)
  {}
  
  @Override
  public void mouseReleased(MouseEvent e)
  {}
  
  @Override
  public void mouseDragged(MouseEvent e)
  {}
  
  @Override
  public void mouseMoved(MouseEvent e)
  {}
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {}
  
  @Override
  public void setListenersEnabled(boolean b)
  {}
}
