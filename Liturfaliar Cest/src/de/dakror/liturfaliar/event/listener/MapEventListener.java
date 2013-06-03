package de.dakror.liturfaliar.event.listener;

import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.ui.Talk;

public interface MapEventListener
{
  public void fieldTouched(Creature c, Map m);
  
  public void fieldTriggered(Creature c, Map m);
  
  public void talkStarted(Talk t, Map m);
  
  public void talkEnded(Talk t, Map m);
  
  public void talkChanged(Talk old, Talk n, Map m);
}
