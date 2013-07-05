package de.dakror.liturfaliar.item.skillanim;

import java.awt.Graphics2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

public class EmptyAnimation extends SkillAnimation
{
  @Override
  public void init()
  {}
  
  @Override
  public boolean isInRange(Creature o, Map m)
  {
    return false;
  }
  
  @Override
  protected void draw(Graphics2D g, Viewport v, Map m)
  {}
  
  @Override
  public void dealEffect(Creature c)
  {}
}
