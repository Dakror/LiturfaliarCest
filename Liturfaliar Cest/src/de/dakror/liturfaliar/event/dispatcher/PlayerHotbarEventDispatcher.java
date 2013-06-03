package de.dakror.liturfaliar.event.dispatcher;

import java.util.ArrayList;

import de.dakror.liturfaliar.event.listener.PlayerHotbarEventListener;
import de.dakror.liturfaliar.ui.ItemSlot;

public class PlayerHotbarEventDispatcher
{
  static ArrayList<PlayerHotbarEventListener> listeners = new ArrayList<PlayerHotbarEventListener>();
  
  public static void addPlayerHotbarEventListener(PlayerHotbarEventListener l)
  {
    listeners.add(l);
  }
  
  public static void removePlayerHotbarEventListener(PlayerHotbarEventListener l)
  {
    listeners.set(listeners.indexOf(l), null);
  }
  
  public static void dispatchSlotTriggered(ItemSlot slot)
  {
    for (PlayerHotbarEventListener l : listeners)
    {
      l.slotTriggered(slot);
    }
  }
}
