package de.dakror.liturfaliar.map.creature.ai;

import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.util.Vector;

public class StraightLineAI extends CreatureAI
{
  
  public StraightLineAI(Creature c)
  {
    super(c);
  }
  
  @Override
  public Path findPath(Vector target)
  {
    return new Path(target);
  }
}
