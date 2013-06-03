package de.dakror.liturfaliar.event.dispatcher;

import java.util.ArrayList;

import de.dakror.liturfaliar.event.listener.MapPackEventListener;
import de.dakror.liturfaliar.map.Map;

public class MapPackEventDispatcher
{
  static ArrayList<MapPackEventListener> listeners = new ArrayList<MapPackEventListener>();
  
  public static void addMapPackEventListener(MapPackEventListener l)
  {
    listeners.add(l);
  }
  
  public static void removeMapPackEventListener(MapPackEventListener l)
  {
    listeners.set(listeners.indexOf(l), null);
  }
  
  public static void dispatchMapChanged(Map oldmap, Map newmap) {
    for (MapPackEventListener l :listeners) {
      l.mapChanged(oldmap, newmap);
    }
  }
}
