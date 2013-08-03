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
  
  public Emoticon(Creature p, int type, boolean a, long l)
  {
    parent = p;
    setType(type);
    animate = a;
    allemoticons = Viewport.loadImage("system/emoticon.png");
    length = l;
    time = System.currentTimeMillis();
    done = false;
  }
  
  public void draw(Graphics2D g, Map m, Viewport v)
  {
    int frame = (animate) ? (v.getFrame(0.35f) % 7) * 32 : 0;
    
    int size = 32;
    
    g.drawImage(allemoticons, m.getX() + (int) parent.getPos().x + (int) (parent.getWidth() * 0.3), m.getY() + (int) parent.getPos().y - size, m.getX() + (int) parent.getPos().x + size + (int) (parent.getWidth() * 0.3), m.getY() + (int) parent.getPos().y, frame, getType() * 32, frame + 32, getType() * 32 + 32, v.w);
    if (System.currentTimeMillis() - time > length && length > -1) done = true;
  }
  
  public int getType()
  {
    return type;
  }
  
  public void setType(int type)
  {
    this.type = type;
  }
  
  public Creature getParent()
  {
    return parent;
  }
  
  public void setParent(Creature p)
  {
    parent = p;
  }
}
