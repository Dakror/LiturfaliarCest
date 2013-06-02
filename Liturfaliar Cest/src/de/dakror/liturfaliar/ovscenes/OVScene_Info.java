package de.dakror.liturfaliar.ovscenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;

public class OVScene_Info extends OVScene
{
  long  frames;
  long  updates;
  long  time;
  
  Point mouse;
  
  @Override
  public void init(Viewport v)
  {
    this.v = v;
    time = System.currentTimeMillis();
    frames = 0;
    updates = 0;
    consistent = true;
    mouse = new Point(0, 0);
    
    v.w.addMouseMotionListener(this);
  }
  
  @Override
  public void update(long timePassed)
  {
    updates++;
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    frames++;
    // show fps
    Assistant.drawString(Math.round(frames / (float) ((System.currentTimeMillis() - time) / 1000.0f)) + " FPS", 0, 30, g, Color.white, g.getFont().deriveFont(30.0f));
    // show updates
    Assistant.drawString(Math.round(updates / (float) ((System.currentTimeMillis() - time) / 1000.0f)) + " UPS", 0, 60, g, Color.white, g.getFont().deriveFont(30.0f));
    // show mouseX
    Assistant.drawString(mouse.x + " x", 0, 90, g, Color.white, g.getFont().deriveFont(30.0f));
    // show mouseY
    Assistant.drawString(mouse.y + " y", 0, 120, g, Color.white, g.getFont().deriveFont(30.0f));
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    mouse = e.getLocationOnScreen();
  }
}
