package de.dakror.liturfaliar.ovscenes;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public abstract class OVScene
{
  public boolean     consistent = false;
  
  public abstract void construct();
  
  public abstract void destruct();
  
  public abstract void update(long timePassed);
  
  public abstract void draw(Graphics2D g);
  
  
  public void keyPressed(KeyEvent e)
  {}
  
  
  public void keyReleased(KeyEvent e)
  {}
  
  
  public void keyTyped(KeyEvent e)
  {}
  
  
  public void mouseClicked(MouseEvent e)
  {}
  
  
  public void mouseEntered(MouseEvent e)
  {}
  
  
  public void mouseExited(MouseEvent e)
  {}
  
  
  public void mousePressed(MouseEvent e)
  {}
  
  
  public void mouseReleased(MouseEvent e)
  {}
  
  
  public void mouseDragged(MouseEvent e)
  {}
  
  
  public void mouseMoved(MouseEvent e)
  {}
  
  
  public void mouseWheelMoved(MouseWheelEvent e)
  {}
}
