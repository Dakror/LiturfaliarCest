package de.dakror.liturfaliar.fx;

import java.awt.Graphics2D;
import java.awt.Image;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

public class Animation
{
  public static final int SIZE = 192;
  
  boolean                 frozen;
  boolean                 below;
  
  int                     x;
  int                     y;
  int                     w;
  int                     h;
  int                     size;
  int                     startFrame;
  int                     endFrame;
  int                     frame;
  
  float                   speed;
  
  long                    time;
  
  Image                   image;
  Creature                c;
  
  public Animation(int x, int y, int size, int startFrame, int endFrame, float speed, boolean below, String file)
  {
    this.x = x;
    this.y = y;
    this.size = size;
    this.startFrame = startFrame;
    this.frame = startFrame;
    this.endFrame = endFrame;
    this.speed = speed;
    this.image = Viewport.loadImage("Animations/" + file);
    this.w = this.image.getWidth(null) / SIZE;
    // this.image = Viewport.loadScaledImage("Animations/" + file, (w = (this.image.getWidth(null) / SIZE)) * size, (h = (this.image.getHeight(null) / SIZE)) * size);
    this.frozen = true;
    this.c = null;
  }
  
  public Animation(int rx, int ry, int size, int startFrame, int endFrame, float speed, boolean below, String file, Creature c)
  {
    this(rx, ry, size, startFrame, endFrame, speed, below, file);
    this.c = c;
  }
  
  public void draw(Map m, Graphics2D g, Viewport v)
  {
    if (!frozen && v.getFrame(time, speed) != 0)
      frame = v.getFrame(time, speed) % (endFrame - startFrame) + 1;
    
    int x = this.x + ((m == null) ? 0 : m.getX()) + ((c == null) ? 0 : c.getRelativePos()[0]);
    int y = this.y + ((m == null) ? 0 : m.getY()) + ((c == null) ? 0 : c.getRelativePos()[1]);
    
    g.drawImage(image, x, y, x + size, y + size, SIZE * (frame % w), SIZE * (int) Math.floor((frame / (double) w)), SIZE * (frame % w) + SIZE, SIZE * (int) Math.floor((frame / (double) w)) + SIZE, v.w);
  }
  
  public boolean isDone()
  {
    return frame == endFrame;
  }
  
  public void playAnimation()
  {
    setFrozen(false);
    time = System.currentTimeMillis();
  }
  
  public void setFrozen(boolean f)
  {
    frozen = f;
  }
  
  public boolean isFrozen()
  {
    return frozen;
  }
  
  public boolean isBelow()
  {
    return below;
  }
}
