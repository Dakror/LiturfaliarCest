package de.dakror.liturfaliar.item.skillanim;

import java.awt.Graphics2D;
import java.util.ArrayList;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

public abstract class SkillAnimation
{
  Item                item;
  Creature            caster;
  String              sound;
  
  boolean             done;
  boolean             below;
  boolean             hit;
  boolean             played;
  
  long                time;
  
  ArrayList<Creature> affected;
  
  public void playAnimation(Item i, Creature c)
  {
    item = i;
    caster = c;
    affected = new ArrayList<Creature>();
    played = false;
    hit = false;
    init();
    time = System.currentTimeMillis();
  }
  
  public abstract void init();
  
  public abstract boolean isInRange(Creature o, Map m);
  
  public abstract int getMaximumRange();
  
  public void update(long timePassed, Map m)
  {
    if (m == null) return;
    
    for (Creature c : m.creatures)
    {
      if (c != null && c.isAlive() && !affected.contains(c))
      {
        if (isInRange(c, m)) dealEffect(c);
      }
    }
  }
  
  protected abstract void draw(Graphics2D g, Map m);
  
  public abstract void dealEffect(Creature c);
  
  public void drawBelow(Graphics2D g, Map m)
  {
    if (!played)
    {
      Viewport.playSound("064-Swing03");
      played = true;
    }
    
    if (hit)
    {
      Viewport.playSound("185-Hit01");
      hit = false;
    }
    
    if (!below) return;
    
    draw(g, m);
  }
  
  public void drawAbove(Graphics2D g, Map m)
  {
    if (below) return;
    
    draw(g, m);
  }
  
  public boolean isDone()
  {
    return done;
  }
}
