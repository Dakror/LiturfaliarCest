package de.dakror.liturfaliar.event.listener;

import java.awt.event.MouseEvent;

import de.dakror.liturfaliar.ui.ItemSlot;

public interface ItemSlotEventListener
{
  public void slotPressed(MouseEvent e, ItemSlot slot);
  
  public void slotDragged(MouseEvent e, ItemSlot slot);
  
  public void slotReleased(MouseEvent e, ItemSlot slot);
}
