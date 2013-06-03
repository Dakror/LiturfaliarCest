package de.dakror.liturfaliar.event.listener;

import java.awt.event.MouseEvent;

import de.dakror.liturfaliar.ui.ItemSlot;

public interface ItemSlotEventListener
{
  public void itemPressed(MouseEvent e, ItemSlot slot);
  
  public void itemDragged(MouseEvent e, ItemSlot slot);
  
  public void itemReleased(MouseEvent e, ItemSlot slot);
}
