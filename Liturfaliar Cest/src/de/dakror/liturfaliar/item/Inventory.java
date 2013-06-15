package de.dakror.liturfaliar.item;

import de.dakror.liturfaliar.event.listener.ItemSlotEventListener;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.ui.ItemSlot;

public interface Inventory extends ItemSlotEventListener
{
  public ItemSlot getPickedUpItemSlot();
  
  public void setPickedUpItemSlot(ItemSlot item);
  
  public ItemSlot getFirstSlot(Item item);
  
  public Map getMap();
  
  public void showContextMenu(ItemSlot slot, int x, int y);
  
  public void hideContextMenu();
}
