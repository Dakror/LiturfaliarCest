package de.dakror.liturfaliar.map.creature;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.listener.MapEventListener;
import de.dakror.liturfaliar.fx.Emoticon;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.skillanim.SkillAnimation;
import de.dakror.liturfaliar.map.Field;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.ai.CreatureAI;
import de.dakror.liturfaliar.map.creature.ai.Path;
import de.dakror.liturfaliar.settings.Attribute;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.settings.DamageType;
import de.dakror.liturfaliar.ui.DamageIndicator;
import de.dakror.liturfaliar.ui.Talk;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Vector;

public class Creature implements MapEventListener
{
  public static final Integer[]        DIRS  = { 3, 2, 0, 1 };
  public static final int              LEVEL = 25;
  
  private double                       speed;
  
  public int                           bw, bh, bx, by;
  public boolean                       frozen;
  
  protected int                        w, h;
  protected int                        dir;
  protected boolean                    massive;
  protected double                     layer;
  
  protected Vector                     lastPos, pos, goTo;
  protected Emoticon                   emoticon;
  
  protected Attributes                 attr;
  protected Equipment                  equipment;
  
  protected ArrayList<SkillAnimation>  skills;
  protected ArrayList<DamageIndicator> dmgIndicators;
  
  protected Area                       hitArea;
  protected CreatureAI                 AI;
  protected Path                       path;
  
  public Creature(int x, int y, int w, int h)
  {
    pos = lastPos = goTo = new Vector(x, y);
    massive = false;
    frozen = false;
    this.w = bw = w;
    this.h = bh = h;
    bx = by = 0;
    hitArea = new Area(new Rectangle2D.Double(0, 0, w, h));
    attr = new Attributes();
    
    skills = new ArrayList<SkillAnimation>();
    dmgIndicators = new ArrayList<DamageIndicator>();
  }
  
  public void setHuman()
  {
    bx = CFG.HUMANBUMPS[0];
    by = CFG.HUMANBUMPS[1];
    bw = CFG.HUMANBUMPS[2];
    bh = CFG.HUMANBUMPS[3];
    
    w = CFG.HUMANBOUNDS[0];
    h = CFG.HUMANBOUNDS[1];
  }
  
  public int getDir()
  {
    return dir;
  }
  
  public Area getHitArea(Map m)
  {
    return hitArea.createTransformedArea(AffineTransform.getTranslateInstance(pos.x + m.getX(), pos.y + m.getY()));
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
  
  public Point getPos()
  {
    return new Point((int) Math.round(pos.x), (int) Math.round(pos.y));
  }
  
  public void setFrozen(boolean b)
  {
    frozen = b;
  }
  
  public void setTarget(int x, int y)
  {
    goTo = new Vector(x, y);
  }
  
  public void move(Map map)
  {
    if (!frozen)
    {
      Vector targetVector = pos.sub(goTo);
      double distance = targetVector.length;
      if (targetVector.length >= getSpeed())
      {
        distance = getSpeed();
      }
      if (!map.getBumpMap().contains(new Rectangle2D.Double(map.getX() + pos.sub(targetVector.setLength(distance)).x + bx, map.getY() + pos.sub(targetVector.setLength(distance)).y + by, bw, bh)))
      {
        setTarget((int) pos.x, (int) pos.y);
        return;
      }
      for (Creature c : map.creatures)
      {
        if (c instanceof Player)
          continue;
        if (c.getBumpArea().intersects(new Rectangle2D.Double(map.getX() + pos.sub(targetVector.setLength(distance)).x + bx, map.getY() + pos.sub(targetVector.setLength(distance)).y + by, bw, bh)))
        {
          setTarget((int) pos.x, (int) pos.y);
          return;
        }
      }
      lastPos = pos;
      pos = pos.sub(targetVector.setLength(distance));
    }
  }
  
  public void setMassive(boolean b)
  {
    massive = b;
  }
  
  public void update(long timePassed, Map map)
  {
    move(map);
    for (Field f : map.fields)
    {
      if (getBumpArea().contains(new Point2D.Double(f.getX() * CFG.FIELDSIZE + CFG.FIELDSIZE * 0.5, f.getY() * CFG.FIELDSIZE + CFG.FIELDSIZE * 0.5)))
      {
        f.fieldTriggered(this, map);
      }
      else if (getBumpArea().intersects(f.getX() * CFG.FIELDSIZE, f.getY() * CFG.FIELDSIZE, CFG.FIELDSIZE, CFG.FIELDSIZE))
      {
        f.fieldTouched(this, map);
      }
    }
    for (Creature c : map.creatures)
    {
      if (c != null && !c.equals(this) && c.isAlive())
      {
        onIntersect(c, map);
      }
    }
    
    try
    {
      for (SkillAnimation skill : skills)
      {
        if (skill.isDone())
          skills.remove(skill);
        
        else
        {
          skill.update(timePassed, map);
        }
      }
    }
    catch (ConcurrentModificationException e)
    {}
    
    try
    {
      for (DamageIndicator dmgi : dmgIndicators)
      {
        if (dmgi.isDone())
          dmgIndicators.remove(dmgi);
      }
    }
    catch (ConcurrentModificationException e)
    {}
  }
  
  public void draw(Graphics2D g, Viewport v, Map m)
  {
    try
    {
      for (DamageIndicator dmgi : dmgIndicators)
      {
        if (!dmgi.isDone())
          dmgi.draw(m, g, v);
      }
    }
    catch (ConcurrentModificationException e)
    {}
    
    if (CFG.UIDEBUG)
    {
      Color color = g.getColor();
      g.setColor(Color.green);
      g.draw(new Rectangle2D.Double(m.getX() + getRelativePos().x, m.getY() + getRelativePos().y, w, h));
      Assistant.Shadow(new Rectangle2D.Double(m.getX() + getRelativePos().x + bx, m.getY() + getRelativePos().y + by, bw, bh), Color.orange, 1, g);
      g.setColor(color);
    }
  }
  
  public void drawEmoticon(Graphics2D g, Viewport v, Map m)
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
    Area copy = getBumpArea();
    copy.intersect(other.getBumpArea());
    return !copy.isEmpty();
  }
  
  public void onIntersect(Creature other, Map map)
  {
    if (!intersects(other, map))
      return;
    
    pos = lastPos;
  }
  
  public Area getBumpArea()
  {
    return new Area(new Rectangle2D.Double(getRelativePos().x + bx, getRelativePos().y + by, bw, bh));
  }
  
  public Area getRelativeArea()
  {
    return new Area(new Rectangle2D.Double(getRelativePos().x, getRelativePos().y, w, h));
  }
  
  public Area getArea()
  {
    return new Area(new Rectangle2D.Double(pos.x, pos.y, w, h));
  }
  
  public Point getRelativePos()
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
  
  public Point2D getField()
  {
    return new Point2D.Double(Assistant.round(getRelativePos().x + bx + bw / 2, CFG.FIELDSIZE) / (double) CFG.FIELDSIZE, Assistant.round(getRelativePos().y + by + bh / 2, CFG.FIELDSIZE) / (double) CFG.FIELDSIZE);
  }
  
  public void setEmoticon(Emoticon e)
  {
    emoticon = e;
  }
  
  public void setEmoticon(int type, boolean animate, long length)
  {
    emoticon = new Emoticon(this, type, animate, length);
  }
  
  public Equipment getEquipment()
  {
    return equipment;
  }
  
  public void setEquipment(Equipment equipment)
  {
    this.equipment = equipment;
  }
  
  public Emoticon getEmoticon()
  {
    return emoticon;
  }
  
  public boolean isLookingAt(Creature c, Map m)
  {
    double x = getRelativePos().x + getWidth() / 2.0;
    double y = getRelativePos().y + getHeight() / 2.0;
    switch (dir)
    {
      case 0:
        // down
        return c.getRelativeArea().intersects(new Rectangle2D.Double(x, y + getHeight() / 2.0, 1, m.getHeight() * CFG.FIELDSIZE));
      case 1:
        // left
        return c.getRelativeArea().intersects(new Rectangle2D.Double(0, y, x - getWidth() / 2.0, 1));
      case 2:
        // right
        return c.getRelativeArea().intersects(new Rectangle2D.Double(x + getWidth() / 2, y, m.getWidth() * CFG.FIELDSIZE, 1));
      case 3:
        // up
        return c.getRelativeArea().intersects(new Rectangle2D.Double(x, 0, 1, y - getHeight() / 2));
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
      double x = getRelativePos().x + getWidth() / 2.0;
      double y = getRelativePos().y + getHeight() / 2.0;
      if (c.getRelativeArea().intersects(new Rectangle2D.Double(x, y + getHeight() / 2.0, 1, m.getHeight() * CFG.FIELDSIZE)))
      {
        dir = 0;
      }
      else if (c.getRelativeArea().intersects(new Rectangle2D.Double(0, y, x - getWidth() / 2.0, 1)))
      {
        dir = 1;
      }
      else if (c.getRelativeArea().intersects(new Rectangle2D.Double(x + getWidth() / 2, y, m.getWidth() * CFG.FIELDSIZE, 1)))
      {
        dir = 2;
      }
      else if (c.getRelativeArea().intersects(new Rectangle2D.Double(x, 0, 1, y - getHeight() / 2)))
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
  
  public Attributes getAttributes()
  {
    return attr;
  }
  
  public void setAttributes(Attributes a)
  {
    this.attr = a;
  }
  
  public int getLevel()
  {
    if (attr.getAttribute(Attr.experience).isEmpty())
      return 1;
    
    int lvl = (int) Math.sqrt(attr.getAttribute(Attr.experience).getValue() / (double) LEVEL);
    
    if (lvl == 0)
      return 1;
    
    return lvl;
  }
  
  public int getXPforLevel(int rl)
  {
    return (int) Math.round(Math.pow(getLevel() + rl, 2) * LEVEL);
  }
  
  public void playSkill(Item i, SkillAnimation s)
  {
    s.playAnimation(i, this);
    skills.add(s);
  }
  
  public boolean isPlayingSkill(SkillAnimation s)
  {
    for (SkillAnimation sa : skills)
    {
      if (sa.getClass().equals(s.getClass()))
      {
        return !sa.isDone();
      }
    }
    return false;
  }
  
  public boolean isAlive()
  {
    if (attr.getAttribute(Attr.health).isEmpty())
      return true;
    
    else return attr.getAttribute(Attr.health).getValue() > 0;
  }
  
  public void dealDamage(Creature causer, DamageType type, Integer damage)
  {
    Attribute a = attr.getAttribute(Attr.health);
    int val = (int) (damage + a.getValue());
    
    if (val > a.getMaximum())
    {
      int dif = (int) (a.getMaximum() - a.getValue());
      if (dif == 0)
        return;
      
      else val = (int) (dif + a.getValue());
    }
    
    attr.getAttribute(Attr.health).setValue(val);
    addDamageIndicator(new DamageIndicator(this, type, Math.abs(damage)));
  }
  
  public void addDamageIndicator(DamageIndicator d)
  {
    dmgIndicators.add(d);
  }
  
  public Vector getTrackingNode()
  {
    return new Vector(pos.x + bx + bw / 2, pos.y + by + bh / 2);
  }
}
