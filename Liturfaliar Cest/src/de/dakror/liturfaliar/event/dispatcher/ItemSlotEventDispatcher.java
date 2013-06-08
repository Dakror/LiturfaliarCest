package de.dakror.liturfaliar.event.dispatcher;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import de.dakror.liturfaliar.event.listener.ItemSlotEventListener;
import de.dakror.liturfaliar.ui.ItemSlot;

public class ItemSlotEventDispatcher
{
  static ArrayList<ItemSlotEventListener> listeners = new ArrayList<ItemSlotEventListener>();
  
  public static void addItemSlotEventListener(ItemSlotEventListener l)
  {
    listeners.add(l);
  }
  
  public static void removeItemSlotEventListener(ItemSlotEventListener l)
  {
    listeners.set(listeners.indexOf(l), null);
  }
  
  public static void dispatchSlotPressed(MouseEvent e, ItemSlot slot)
  {
    try
    {
      for (ItemSlotEventListener l : listeners)
      {
        if (l != null)
          l.slotPressed(e, slot);
      }
    }
    catch (Exception e1)
    {}
  }
  
  public static void dispatchSlotHovered(MouseEvent e, ItemSlot slot)
  {
    for (ItemSlotEventListener l : listeners)
    {
      if (l != null)
        l.slotHovered(e, slot);
    }
  }
  
  public static void dispatchSlotReleased(MouseEvent e, ItemSlot slot)
  {
    for (ItemSlotEventListener l : listeners)
    {
      if (l != null)
        l.slotReleased(e, slot);
    }
  }
  
}
