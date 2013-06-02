package de.dakror.liturfaliar.event;

import de.dakror.liturfaliar.map.Map;

public interface MapPackEventListener
{
  public void onMapChange(Map oldmap, Map newmap);
}
