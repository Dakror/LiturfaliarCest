package de.dakror.liturfaliar.map.creature;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import de.dakror.liturfaliar.event.Event;
import de.dakror.liturfaliar.event.Events;
import de.dakror.liturfaliar.event.Listener;
import de.dakror.liturfaliar.fx.Emoticon;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.skillanim.SkillAnimation;
import de.dakror.liturfaliar.map.Field;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.ai.CreatureAI;
import de.dakror.liturfaliar.map.creature.ai.path.Path;
import de.dakror.liturfaliar.settings.Attribute;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.settings.DamageType;
import de.dakror.liturfaliar.ui.DamageIndicator;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Projection;
import de.dakror.liturfaliar.util.Vector;

public class Creature implements Listener
{
  public static final Integer[]        DIRS  = { 3, 2, 0, 1 };
  public static final int              LEVEL = 25;
  
  private double                       speed;
  private boolean                      didntMove;
  
  private Path                         path;
  
  public int                           bw, bh, bx, by;
  public boolean                       frozen;
  
  protected int                        w, h;
  protected int                        dir;
  protected boolean                    massive;
  protected double                     layer;
  
  protected Vector                     lastPos, relPos, goTo;
  protected Emoticon                   emoticon;
  protected Attributes                 attr;
  protected Equipment                  equipment;
  protected ArrayList<SkillAnimation>  skills;
  protected ArrayList<DamageIndicator> dmgIndicators;
  protected Area                       hitArea;
  protected Area[][]                   realAreas;
  protected CreatureAI                 AI;
  protected String                     name;
  
  
  public Creature(int x, int y, int w, int h)
  {
    relPos = lastPos = goTo = new Vector(x, y);
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
    return hitArea.createTransformedArea(AffineTransform.getTranslateInstance(relPos.x + m.getX(), relPos.y + m.getY()));
  }
  
  public void setDir(int dir)
  {
    this.dir = dir;
  }
  
  public void setPos(int x, int y)
  {
    lastPos = relPos;
    relPos = new Vector(x, y);
  }
  
  public double getDistance()
  {
    return Vector.getDistance(relPos, goTo);
  }
  
  public Vector getTarget()
  {
    return goTo;
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
      Vector newPos = getMovePos(map);
      
      if (!map.getBumpMap().contains(new Rectangle2D.Double(map.getX() + newPos.x + bx, map.getY() + newPos.y + by, bw, bh)))
      {
        resetTarget();
        return;
      }
 
      lastPos = relPos;
      
      if (newPos.equals(relPos))
      {
        if (!didntMove) didntMove = true;
        else
        {
          setPath(null);
          resetTarget();
          didntMove = false;
        }
      }
      
      relPos = newPos;
    }
  }
  
  public Vector getMovePos(Map map) {
    Vector targetVector = relPos.sub(goTo);
    double distance = targetVector.length;
    
    if (targetVector.length >= getSpeed())
    {
      distance = getSpeed();
    }
    
    Vector newPos = relPos.sub(targetVector.setLength(distance));
    
    for (Creature c : map.creatures)
    {
      if (!c.equals(this) && c.isAlive())
      {
        Vector v = getIntersection(newPos, c, map);
        if (v != null)
        {
          if (getIntersection(newPos.sub(v), c, map) != null) newPos = newPos.add(v);
          
          else newPos = newPos.sub(v);
        }
      }
    }
    
    return newPos;
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
        f.onEvent(new Event(Events.fieldTriggered, this, map));
      }
      else if (getBumpArea().intersects(f.getX() * CFG.FIELDSIZE, f.getY() * CFG.FIELDSIZE, CFG.FIELDSIZE, CFG.FIELDSIZE))
      {
        f.onEvent(new Event(Events.fieldTouched, this, map));
      }
    }
    
    try
    {
      for (SkillAnimation skill : skills)
      {
        if (skill.isDone()) skills.remove(skill);
        
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
        if (dmgi.isDone()) dmgIndicators.remove(dmgi);
      }
    }
    catch (ConcurrentModificationException e)
    {}
  }
  
  public void draw(Graphics2D g, Map m)
  {
    try
    {
      for (DamageIndicator dmgi : dmgIndicators)
      {
        if (!dmgi.isDone()) dmgi.draw(m, g);
      }
    }
    catch (ConcurrentModificationException e)
    {}
    
    if (CFG.UIDEBUG)
    {
      Color color = g.getColor();
      g.setColor(Color.green);
      g.draw(new Rectangle2D.Double(m.getX() + getPos().x, m.getY() + getPos().y, w, h));
      Assistant.Shadow(new Rectangle2D.Double(m.getX() + getPos().x + bx, m.getY() + getPos().y + by, bw, bh), Color.orange, 1, g);
      g.setColor(color);
      
      Field f = getField(m);
      Assistant.Rect(m.getX() + f.getX(), m.getY() + f.getY(), CFG.FIELDSIZE, CFG.FIELDSIZE, Color.gray, null, g);
      
      if (path != null) path.draw(g, m);
    }
  }
  
  public void drawEmoticon(Graphics2D g, Map m)
  {
    if (emoticon != null) emoticon.draw(g, m);
  }
  
  public boolean isMassive()
  {
    return massive;
  }
  
  public Vector getIntersection(Vector newPos, Creature other, Map m)
  {
    Vector[] v = Vector.translateGroup(getBumpVertices(), newPos.x, newPos.y);
    Vector[] ov = Vector.translateGroup(other.getBumpVertices(), other.getPos().x, other.getPos().y);
    
    // -- x Axis -- //
    Projection myX = new Projection(v[4].x, v[1].x);
    Projection otherX = new Projection(ov[4].x, ov[1].x);
    
    if (!myX.intersects(otherX)) return null;
    
    // -- y Axis -- //
    Projection myY = new Projection(v[4].y, v[3].y);
    Projection otherY = new Projection(ov[4].y, ov[3].y);
    if (!myY.intersects(otherY)) return null;
    
    double overlapX = myX.getIntersection(otherX);
    double overlapY = myY.getIntersection(otherY);
    
    Vector overlapStart = new Vector((overlapX < overlapY) ? Math.max(myX.min, otherX.min) : 0, (overlapY < overlapX) ? Math.max(myY.min, otherY.min) : 0);
    Vector overlapEnd = new Vector((overlapX < overlapY) ? Math.min(myX.max, otherX.max) : 0, (overlapY < overlapX) ? Math.min(myY.max, otherY.max) : 0);
    
    return overlapStart.sub(overlapEnd);
  }
  
  public Area getBumpArea()
  {
    return new Area(new Rectangle2D.Double(getPos().x + bx, getPos().y + by, bw, bh));
  }
  
  public Area getArea()
  {
    return new Area(new Rectangle2D.Double(getPos().x, getPos().y, w, h));
  }
  
  public Vector getPos()
  {
    return relPos;
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
  
  public Field getField(Map m)
  {
    return m.findField(relPos.x + bx + bw / 2, relPos.y + by + bh);
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
    double x = getPos().x + getWidth() / 2.0;
    double y = getPos().y + getHeight() / 2.0;
    switch (dir)
    {
      case 0:
        // down
        return c.getArea().intersects(new Rectangle2D.Double(x, y + getHeight() / 2.0, 1, m.getHeight() * CFG.FIELDSIZE));
      case 1:
        // left
        return c.getArea().intersects(new Rectangle2D.Double(0, y, x - getWidth() / 2.0, 1));
      case 2:
        // right
        return c.getArea().intersects(new Rectangle2D.Double(x + getWidth() / 2, y, m.getWidth() * CFG.FIELDSIZE, 1));
      case 3:
        // up
        return c.getArea().intersects(new Rectangle2D.Double(x, 0, 1, y - getHeight() / 2));
      default:
        return false;
    }
  }
  
  public void lookAt(Creature c, Map m)
  {
    if (isLookingAt(c, m)) return;
    for (int i = 0; i < 4; i++)
    {
      double x = getPos().x + getWidth() / 2.0;
      double y = getPos().y + getHeight() / 2.0;
      if (c.getArea().intersects(new Rectangle2D.Double(x, y + getHeight() / 2.0, 1, m.getHeight() * CFG.FIELDSIZE)))
      {
        dir = 0;
      }
      else if (c.getArea().intersects(new Rectangle2D.Double(0, y, x - getWidth() / 2.0, 1)))
      {
        dir = 1;
      }
      else if (c.getArea().intersects(new Rectangle2D.Double(x + getWidth() / 2, y, m.getWidth() * CFG.FIELDSIZE, 1)))
      {
        dir = 2;
      }
      else if (c.getArea().intersects(new Rectangle2D.Double(x, 0, 1, y - getHeight() / 2)))
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
    if (attr.getAttribute(Attr.experience).isEmpty()) return 1;
    
    int lvl = (int) Math.sqrt(attr.getAttribute(Attr.experience).getValue() / (double) LEVEL);
    
    if (lvl == 0) return 1;
    
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
    if (attr.getAttribute(Attr.health).isEmpty()) return true;
    
    else return attr.getAttribute(Attr.health).getValue() > 0;
  }
  
  public void dealDamage(Creature causer, DamageType type, Integer damage)
  {
    Attribute a = attr.getAttribute(Attr.health);
    int val = (int) (damage + a.getValue());
    
    if (val > a.getMaximum())
    {
      int dif = (int) (a.getMaximum() - a.getValue());
      if (dif == 0) return;
      
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
    return new Vector(relPos.x + bx + bw / 2, relPos.y + by + bh / 2);
  }
  
  public Path getPath()
  {
    return path;
  }
  
  public void setPath(Path p)
  {
    didntMove = false;
    path = p;
  }
  
  /**
   * Get the to pos relative vectors of the vertices of the bump area.<br>
   * Indices:<br>
   * &nbsp;&nbsp;4---1<br>
   * &nbsp;&nbsp;|&nbsp;&nbsp;0&nbsp;&nbsp;|<br>
   * &nbsp;&nbsp;3---2<br>
   * 
   * @return
   */
  public Vector[] getBumpVertices()
  {
    return new Vector[] { new Vector(bx + bw / 2, by + bh / 2), new Vector(bx + bw, by), new Vector(bx + bw, by + bh), new Vector(bx, by + bh), new Vector(bx, by) };
  }
  
  public void resetTarget()
  {
    goTo = relPos;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String s)
  {
    name = s;
  }
  
  @Override
  public void onEvent(Event e)
  {}
}
