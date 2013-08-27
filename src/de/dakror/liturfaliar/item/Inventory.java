package de.dakror.liturfaliar.item;

import de.dakror.liturfaliar.event.Listener;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.ui.ItemSlot;

public interface Inventory extends Listener
{
	public ItemSlot getPickedUpItemSlot();
	
	public void setPickedUpItemSlot(ItemSlot item);
	
	public ItemSlot getFirstSlot(Item item);
	
	public Map getMap();
	
	public void showContextMenu(ItemSlot slot, int x, int y);
	
	public void hideContextMenu();
}
