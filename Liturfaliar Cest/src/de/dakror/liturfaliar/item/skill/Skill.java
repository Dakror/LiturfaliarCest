package de.dakror.liturfaliar.item.skill;

import java.awt.Graphics2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

public abstract class Skill
{
  Item     item;
  Creature caster;
  
  public Skill(Item i, Creature c)
  {
    item = i;
    caster = c;
  }
  
  public abstract boolean isInRange(Creature o);
  
  public abstract void draw(Graphics2D g, Viewport v, Map m);
  
  public abstract void drawBelow(Graphics2D g, Viewport v, Map m);
}
