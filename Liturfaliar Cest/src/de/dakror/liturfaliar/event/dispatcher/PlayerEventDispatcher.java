package de.dakror.liturfaliar.event.dispatcher;

import java.util.ArrayList;

import de.dakror.liturfaliar.event.listener.PlayerEventListener;

public class PlayerEventDispatcher
{
  static ArrayList<PlayerEventListener> listeners = new ArrayList<PlayerEventListener>();
  
  public static void addPlayerEventListener(PlayerEventListener l)
  {
    listeners.add(l);
  }
  
  public static void removePlayerEventListener(PlayerEventListener l)
  {
    listeners.set(listeners.indexOf(l), null);
  }
  
  public static void dispatchLevelUp(int oldLevel)
  {
    for (PlayerEventListener l : listeners)
    {
      if (l != null)
        l.levelUp(oldLevel);
    }
  }
}
