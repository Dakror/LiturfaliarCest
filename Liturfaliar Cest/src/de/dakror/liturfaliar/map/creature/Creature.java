package de.dakror.liturfaliar.map.creature;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.dakror.liturfaliar.CFG;
import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.fx.Emoticon;
import de.dakror.liturfaliar.map.Field;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.event.MapEventListener;
import de.dakror.liturfaliar.ui.Talk;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Vector;

public class Creature implements MapEventListener
{
  public static final int[] DIRS = { 3, 2, 0, 1 };
  public int                bw, bh, bx, by;
  protected int             w, h;
  protected boolean         massive;
  public boolean            frozen;
  protected int             dir;
  protected double          layer;
  private double            speed;
  protected Vector          lastPos, pos, goTo;
  protected Emoticon        emoticon;
  
  public Creature(int x, int y, int w, int h)
  {
    pos = lastPos = goTo = new Vector(x, y);
    massive = false;
    frozen = false;
    this.w = bw = w;
    this.h = bh = h;
    bx = by = 0;
  }
  
  public int getDir()
  {
    return dir;
  }
  
  public void setDir(int dir)
  {
    this.dir = dir;
  }
  
  public void setPos(int x, int y)
  {
    lastPos = pos;
    pos = new Vector(x, y);
  }
  
  public double getDistance()
  {
    return Vector.get_distance(pos, goTo);
  }
  
  public Vector getTargetVector()
  {
    return goTo;
  }
  
  public int[] getPos()
  {
    return new int[] { (int) pos.coords[0], (int) pos.coords[1] };
  }
  
  public void setFrozen(boolean b)
  {
    frozen = b;
  }
  
  public void setTarget(double x, double y)
  {
    goTo = new Vector(x, y);
  }
  
  public void move(Map map)
  {
    if (!frozen)
    {
      Vector targetVector = pos.sub(goTo);
      if (targetVector.length >= getSpeed())
      {
        lastPos = pos;
        pos = pos.sub(targetVector.setLength(getSpeed()));
      }
    }
  }
  
  public void setMassive(boolean b)
  {
    massive = b;
  }
  
  public void update(Map map)
  {
    move(map);
    for (Field f : map.fields)
    {
      if (getBumpArea(map).contains(new Point2D.Double(f.getX() * CFG.FIELDSIZE + CFG.FIELDSIZE * 0.5, f.getY() * CFG.FIELDSIZE + CFG.FIELDSIZE * 0.5)))
      {
        f.fieldTriggered(this, map);
      }
      else if (getBumpArea(map).intersects(f.getX() * CFG.FIELDSIZE, f.getY() * CFG.FIELDSIZE, CFG.FIELDSIZE, CFG.FIELDSIZE))
      {
        f.fieldTouched(this, map);
      }
    }
    for (Creature c : map.creatures)
    {
      if (c != null && !c.equals(this))
      {
        onIntersect(c, map);
      }
    }
  }
  
  public void draw(Graphics2D g, Viewport v, Map m)
  {
    if (emoticon != null)
      emoticon.draw(g, m, v);
  }
  
  public boolean isMassive()
  {
    return massive;
  }
  
  public boolean intersects(Creature other, Map map)
  {
    if (other.layer != layer)
      return false;
    Area copy = getBumpArea(map);
    copy.intersect(other.getBumpArea(map));
    return !copy.isEmpty();
  }
  
  public void onIntersect(Creature other, Map map)
  {
    if (!intersects(other, map))
      return;
    pos = lastPos;
  }
  
  public Area getBumpArea(Map map)
  {
    return new Area(new Rectangle2D.Double(getRelativePos(map)[0] + bx, getRelativePos(map)[1] + by, bw, bh));
  }
  
  public Area getArea(Map map)
  {
    return new Area(new Rectangle2D.Double(getRelativePos(map)[0], getRelativePos(map)[1], w, h));
  }
  
  public Area getArea()
  {
    return new Area(new Rectangle2D.Double(pos.coords[0], pos.coords[1], w, h));
  }
  
  public int[] getRelativePos(Map m)
  {
    return getPos();
  }
  
  public int getWidth()
  {
    return w;
  }
  
  public int getHeight()
  {
    return h;
  }
  
  public double getSpeed()
  {
    return speed;
  }
  
  public void setSpeed(double speed)
  {
    this.speed = speed;
  }
  
  public String getCharacter()
  {
    return null;
  }
  
  public Point2D getField(Map m)
  {
    return new Point2D.Double(Assistant.round(getRelativePos(m)[0] + bx + bw / 2, CFG.FIELDSIZE) / (double) CFG.FIELDSIZE, Assistant.round(getRelativePos(m)[1] + by + bh / 2, CFG.FIELDSIZE) / (double) CFG.FIELDSIZE);
  }
  
  public boolean isLookingAt(Creature c, Map m)
  {
    double x = getRelativePos(m)[0] + getWidth() / 2.0;
    double y = getRelativePos(m)[1] + getHeight() / 2.0;
    switch (dir)
    {
      case 0:
        // down
        return c.getArea(m).intersects(new Rectangle2D.Double(x, y + getHeight() / 2.0, 1, m.getHeight() * CFG.FIELDSIZE));
      case 1:
        // left
        return c.getArea(m).intersects(new Rectangle2D.Double(0, y, x - getWidth() / 2.0, 1));
      case 2:
        // right
        return c.getArea(m).intersects(new Rectangle2D.Double(x + getWidth() / 2, y, m.getWidth() * CFG.FIELDSIZE, 1));
      case 3:
        // up
        return c.getArea(m).intersects(new Rectangle2D.Double(x, 0, 1, y - getHeight() / 2));
      default:
        return false;
    }
  }
  
  public void lookAt(Creature c, Map m)
  {
    if (isLookingAt(c, m))
      return;
    for (int i = 0; i < 4; i++)
    {
      double x = getRelativePos(m)[0] + getWidth() / 2.0;
      double y = getRelativePos(m)[1] + getHeight() / 2.0;
      if (c.getArea(m).intersects(new Rectangle2D.Double(x, y + getHeight() / 2.0, 1, m.getHeight() * CFG.FIELDSIZE)))
      {
        dir = 0;
      }
      else if (c.getArea(m).intersects(new Rectangle2D.Double(0, y, x - getWidth() / 2.0, 1)))
      {
        dir = 1;
      }
      else if (c.getArea(m).intersects(new Rectangle2D.Double(x + getWidth() / 2, y, m.getWidth() * CFG.FIELDSIZE, 1)))
      {
        dir = 2;
      }
      else if (c.getArea(m).intersects(new Rectangle2D.Double(x, 0, 1, y - getHeight() / 2)))
      {
        dir = 3;
      }
      else CFG.p("wtf");
    }
  }
  
  public void keyPressed(KeyEvent e, Map m)
  {}
  
  public void keyReleased(KeyEvent e, Map m)
  {}
  
  public void mouseDragged(MouseEvent e, Map m)
  {}
  
  public void mouseMoved(MouseEvent e, Map m)
  {}
  
  public void mouseClicked(MouseEvent e, Map m)
  {}
  
  public void mousePressed(MouseEvent e, Map m)
  {}
  
  public void mouseReleased(MouseEvent e, Map m)
  {}
  
  public void mouseEntered(MouseEvent e, Map m)
  {}
  
  public void mouseExited(MouseEvent e, Map m)
  {}
  
  @Override
  public void fieldTouched(Creature c, Map m)
  {}
  
  @Override
  public void fieldTriggered(Creature c, Map m)
  {}
  
  @Override
  public void talkStarted(Talk t, Map m)
  {}
  
  @Override
  public void talkEnded(Talk t, Map m)
  {}
  
  @Override
  public void talkChanged(Talk old, Talk n, Map m)
  {}
}
