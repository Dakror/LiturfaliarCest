package de.dakror.liturfaliar.ovscenes;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import de.dakror.liturfaliar.Viewport;

public abstract class OVScene implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
  public boolean     consistent = false;
  protected Viewport v;
  
  public abstract void init(Viewport v);
  
  public abstract void update(long timePassed);
  
  public abstract void draw(Graphics2D g);
  
  public void setListenersEnabled(boolean b)
  {
    if (b)
    {
      v.w.addKeyListener(this);
      v.w.addMouseListener(this);
      v.w.addMouseMotionListener(this);
      v.w.addMouseWheelListener(this);
    }
    else
    {
      v.w.removeKeyListener(this);
      v.w.removeMouseListener(this);
      v.w.removeMouseMotionListener(this);
      v.w.removeMouseWheelListener(this);
    }
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
}
