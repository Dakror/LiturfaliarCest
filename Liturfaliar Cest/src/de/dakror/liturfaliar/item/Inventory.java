package de.dakror.liturfaliar.item;

import de.dakror.liturfaliar.event.listener.ItemSlotEventListener;
import de.dakror.liturfaliar.ui.ItemSlot;

public interface Inventory extends ItemSlotEventListener
{
  public ItemSlot getPickedUpItemSlot();
  
  public void setPickedUpItemSlot(ItemSlot item);
}
