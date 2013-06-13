package de.dakror.liturfaliar.item.action;

import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.CFG;

public class Potion implements ItemAction
{
  Attributes changes;
  String     targetID;
  
  public Potion(String t, Object... param)
  {
    changes = new Attributes(param);
    targetID = t;
  }
  
  @Override
  public void actionTriggered(Item item)
  {
    if (item.getItemSlot() == null)
      return;
    Creature target = item.getItemSlot().getInventory().getMap().getCreatureByAccessKey(targetID);
    
    CFG.p(target);
  }
}
