package de.dakror.liturfaliar.map.creature.ai;

import de.dakror.liturfaliar.item.Items;
import de.dakror.liturfaliar.item.action.SkillAction;
import de.dakror.liturfaliar.item.skillanim.SkillAnimation;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.map.creature.ai.path.Path;
import de.dakror.liturfaliar.util.Vector;

public class MeleeAI extends CreatureAI
{
  
  public MeleeAI(Creature c)
  {
    super(c);
  }
  
  @Override
  public Path findPath(Vector target)
  {
    return new Path(target);
  }
  
  @Override
  public boolean canAttack(Creature o)
  {
    // attacks with SKILL: Items.SWORD0
    SkillAnimation animation = ((SkillAction) Items.SWORD0.getItemAction()).getAnimation();
    animation.playAnimation(creature.getEquipment().getFirstWeapon(), creature);
    int l = animation.getMaximumRange();
    
    return creature.getRelativePos().distance(o.getRelativePos()) <= l;
  }
}
