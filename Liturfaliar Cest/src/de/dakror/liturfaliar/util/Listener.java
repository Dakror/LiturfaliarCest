package de.dakror.liturfaliar.util;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public interface Listener
{
  public void keyTyped(KeyEvent e);
  
  public void keyPressed(KeyEvent e);
  
  public void keyReleased(KeyEvent e);
  
  public void mouseWheelMoved(MouseWheelEvent e);
  
  public void mouseDragged(MouseEvent e);
  
  public void mouseMoved(MouseEvent e);
  
  public void mouseClicked(MouseEvent e);
  
  public void mousePressed(MouseEvent e);
  
  public void mouseReleased(MouseEvent e);
  
  public void mouseEntered(MouseEvent e);
  
  public void mouseExited(MouseEvent e);
}