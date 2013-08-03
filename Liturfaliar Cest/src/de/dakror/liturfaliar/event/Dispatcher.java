package de.dakror.liturfaliar.event;

import java.util.ArrayList;


public class Dispatcher
{
  static ArrayList<Listener> listeners = new ArrayList<Listener>();
  
  public static void addListener(Listener l)
  {
    listeners.add(l);
  }
  
  public static void removeListener(Listener l)
  {
    if (listeners.indexOf(l) == -1) return;
    listeners.set(listeners.indexOf(l), null);
  }
  
  public static void dispatch(Events type, Object... params)
  {
    Event e = new Event(type, params);
    for (Listener l : listeners)
    {
      if (l != null) l.onEvent(e);
    }
  }
}
