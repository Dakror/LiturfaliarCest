package de.dakror.liturfaliar.ovscenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import de.dakror.liturfaliar.util.Assistant;

public class OVScene_Info extends OVScene
{
  long  frames;
  long  updates;
  long  time;
  long  updInterval;
  
  int   cacheFPS;
  int   cacheUPS;
  
  Point mouse;
  
  @Override
  public void construct()
  {
    time = System.currentTimeMillis();
    frames = 0;
    updates = 0;
    consistent = true;
    updInterval = 0;
    mouse = new Point(0, 0);
  }
  
  @Override
  public void update(long timePassed)
  {
    updates++;
    
    if (System.currentTimeMillis() - updInterval > 500)
    {
      cacheFPS = Math.round(frames / (float) ((System.currentTimeMillis() - time) / 1000.0f));
      cacheUPS = Math.round(updates / (float) ((System.currentTimeMillis() - time) / 1000.0f));
      updInterval = System.currentTimeMillis();
    }
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    frames++;
    // show fps
    Assistant.drawString(cacheFPS + " FPS", 0, 30, g, Color.white, g.getFont().deriveFont(30.0f));
    // show updates
    Assistant.drawString(cacheUPS + " UPS", 0, 60, g, Color.white, g.getFont().deriveFont(30.0f));
  }
  
  @Override
  public void destruct()
  {}
}
