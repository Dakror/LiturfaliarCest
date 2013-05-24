package de.dakror.liturfaliar.ovscenes;

import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import de.dakror.liturfaliar.Viewport;

public abstract class OVScene implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
  public boolean consistent = false; 
  
  public abstract void init(Viewport v);
  
  public abstract void update(long timePassed);
  
  public abstract void draw(Graphics2D g);
  
  public abstract void setListenersEnabled(boolean b);
}
