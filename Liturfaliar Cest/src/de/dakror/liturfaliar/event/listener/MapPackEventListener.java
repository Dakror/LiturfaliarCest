package de.dakror.liturfaliar.event.listener;

import de.dakror.liturfaliar.map.Map;

public interface MapPackEventListener
{
  public void mapChanged(Map oldmap, Map newmap);
}
