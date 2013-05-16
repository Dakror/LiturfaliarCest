package de.dakror.liturfaliar.ovscenes;

import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import de.dakror.liturfaliar.Viewport;

public interface OVScene extends KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
  public void init(Viewport v);
  
  public void update(long timePassed);
  
  public void draw(Graphics2D g);
  
  public void setListenersEnabled(boolean b);
}
