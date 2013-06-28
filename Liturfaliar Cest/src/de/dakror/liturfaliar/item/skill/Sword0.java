package de.dakror.liturfaliar.item.skill;

import java.awt.Graphics2D;
import java.awt.Image;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

public class Sword0 extends Skill
{
  Image image;
  int rx, ry;
  
  public Sword0(Item i, Creature c)
  {
    super(i, c);
    
    
  }
  
  @Override
  public boolean isInRange(Creature o)
  {
    return false;
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v, Map m)
  {}
}
