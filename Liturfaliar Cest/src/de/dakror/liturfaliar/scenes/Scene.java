package de.dakror.liturfaliar.scenes;

import java.awt.Graphics2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Listener;

public interface Scene extends Listener
{
  public void init(Viewport v);
  
  public void update(long timePassed);
  
  public void draw(Graphics2D g);
}
