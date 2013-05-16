package de.dakror.liturfaliar.fx;

import java.awt.Graphics2D;
import java.awt.Image;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

public class Emoticon
{
  Creature       parent;
  private int    type;
  boolean        animate;
  Image          allemoticons;
  long           length;
  long           time;
  public boolean done;
  
  public Emoticon(Creature parent, int type, boolean animate, long length)
  {
    this.parent = parent;
    setType(type);
    this.animate = animate;
    allemoticons = Viewport.loadImage("system/emoticon.png");
    this.length = length;
    time = System.currentTimeMillis();
    done = false;
  }
  
  public void draw(Graphics2D g, Map m, Viewport v)
  {
    int frame = (animate) ? (v.getFrame(0.35f) % 7) * 32 : 0;
    g.drawImage(allemoticons, m.getX() + parent.getRelativePos(m)[0] + (int) (parent.getWidth() * 0.3), m.getY() + parent.getRelativePos(m)[1] - parent.getWidth(), m.getX() + parent.getRelativePos(m)[0] + (int) (parent.getWidth() * 1.3f), m.getY() + parent.getRelativePos(m)[1], frame, getType() * 32, frame + 32, getType() * 32 + 32, v.w);
    if (System.currentTimeMillis() - time > length)
      done = true;
  }
  
  public int getType()
  {
    return type;
  }
  
  public void setType(int type)
  {
    this.type = type;
  }
}
