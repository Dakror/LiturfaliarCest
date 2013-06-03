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
  
  public static void dispatchItemPressed(MouseEvent e, ItemSlot slot)
  {
    for (ItemSlotEventListener l : listeners)
    {
      l.itemPressed(e, slot);
    }
  }
  
  public static void dispatchItemDragged(MouseEvent e, ItemSlot slot)
  {
    for (ItemSlotEventListener l : listeners)
    {
      l.itemDragged(e, slot);
    }
  }
  
  public static void dispatchItemReleased(MouseEvent e, ItemSlot slot)
  {
    for (ItemSlotEventListener l : listeners)
    {
      l.itemReleased(e, slot);
    }
  }
  
}
