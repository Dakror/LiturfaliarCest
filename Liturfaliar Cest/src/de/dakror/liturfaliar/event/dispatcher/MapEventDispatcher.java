package de.dakror.liturfaliar.event.dispatcher;

import java.util.ArrayList;

import de.dakror.liturfaliar.event.listener.MapEventListener;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.ui.Talk;

public class MapEventDispatcher
{
  static ArrayList<MapEventListener> listeners = new ArrayList<MapEventListener>();
  
  public static void addMapEventListener(MapEventListener l)
  {
    listeners.add(l);
  }
  
  public static void removeMapEventListener(MapEventListener l)
  {
    listeners.set(listeners.indexOf(l), null);
  }
  
  public static void dispatchFieldTouched(Creature c, Map m)
  {}
  
  public static void dispatchFieldTriggered(Creature c, Map m)
  {}
  
  public static void dispatchTalkStarted(Talk t, Map m)
  {}
  
  public static void dispatchTalkEnded(Talk t, Map m)
  {}
  
  public static void dispatchTalkChanged(Talk old, Talk n, Map m)
  {}
}
