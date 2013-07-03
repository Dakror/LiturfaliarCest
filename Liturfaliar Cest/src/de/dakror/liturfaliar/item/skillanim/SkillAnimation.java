package de.dakror.liturfaliar.item.skillanim;

import java.awt.Graphics2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

public abstract class SkillAnimation
{
  Item     item;
  Creature caster;
  
  boolean  done;
  boolean  below;
  
  public void playAnimation(Item i, Creature c)
  {
    item = i;
    caster = c;
    init();
  }
  
  public abstract void init();
  
  public abstract boolean isInRange(Creature o);
  
  public abstract void update(long timePassed, Map m);
  
  protected abstract void draw(Graphics2D g, Viewport v, Map m);
  
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
