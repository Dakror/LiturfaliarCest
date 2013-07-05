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
  
  boolean             done;
  boolean             below;
  
  ArrayList<Creature> affected;
  
  public void playAnimation(Item i, Creature c)
  {
    item = i;
    caster = c;
    affected = new ArrayList<Creature>();
    init();
  }
  
  public abstract void init();
  
  public abstract boolean isInRange(Creature o, Map m);
  
  public void update(long timePassed, Map m)
  {
    for (Creature c : m.creatures)
    {
      if (c != null && c.isAlive() && !affected.contains(c))
      {
        if (isInRange(c, m))
        {
          affected.add(c);
          dealEffect(c);
        }
      }
    }
  }
  
  protected abstract void draw(Graphics2D g, Viewport v, Map m);
  
  public abstract void dealEffect(Creature c);
  
  public void drawBelow(Graphics2D g, Viewport v, Map m)
  {
    if (!below)
      return;
    
    draw(g, v, m);
  }
  
  public void drawAbove(Graphics2D g, Viewport v, Map m)
  {
    if (below)
      return;
    
    draw(g, v, m);
  }
  
  public boolean isDone()
  {
    return done;
  }
}
